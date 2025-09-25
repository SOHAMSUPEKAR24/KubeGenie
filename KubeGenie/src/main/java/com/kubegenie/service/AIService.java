package com.kubegenie.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kubegenie.model.ClusterState;
import com.kubegenie.payload.QueryResponse;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AIService {

    private final OpenAiChatClient chatClient;
    private final ObjectMapper mapper = new ObjectMapper();
    private final KubernetesService kubernetesService;

    // Simple section parsing for suggested commands/optimizations
    private static final Pattern COMMANDS_SECTION =
            Pattern.compile("(?is)```commands\\s*(.*?)\\s*```");
    private static final Pattern OPTIMIZE_SECTION =
            Pattern.compile("(?is)```optimize\\s*(.*?)\\s*```");

    public AIService(OpenAiChatClient chatClient, KubernetesService kubernetesService) {
        this.chatClient = chatClient;
        this.kubernetesService = kubernetesService;
    }

    public String explainClusterState(String naturalLanguageQuery, ClusterState state) {
        String stateJson;
        try {
            stateJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(state);
        } catch (JsonProcessingException e) {
            stateJson = "{\"error\":\"failed to serialize cluster state\"}";
        }

        String system = """
                You are KubeGenie, an expert SRE / Kubernetes assistant.
                Goals:
                1) Read the user's question and the cluster snapshot JSON.
                2) Provide a concise, plain-English diagnosis and explanation.
                3) When helpful, include suggested kubectl commands inside a fenced block:
                   ```commands
                   kubectl get pods -A
                   kubectl describe pod <name> -n <ns>
                   ```
                4) If applicable, include resource/cost optimization tips in a fenced block:
                   ```optimize
                   - Consider reducing requests on underutilized pods...
                   ```
                Rules:
                - Be accurate and safe; do not invent resources that don't exist in the snapshot.
                - Prefer commands that can be run immediately.
                - If information is missing, clearly state assumptions.
                """;

        String user = """
                User question:
                %s

                Cluster snapshot (truncated OK if large):
                %s
                """.formatted(naturalLanguageQuery, stateJson);

        var prompt = new Prompt(List.of(new SystemMessage(system), new UserMessage(user)));

        // Updated to use OpenAiChatClient properly
        return chatClient.chat(prompt)
                .blockOptional()
                .map(response -> response.getMessages().get(0).getContent())
                .orElse("Failed to get AI response");
    }

    public QueryResponse summarizeIntoResponse(String aiText, ClusterState state) {
        QueryResponse resp = new QueryResponse();
        resp.setAiExplanation(aiText);

        // Extract fenced sections if present
        List<String> commands = extractLines(COMMANDS_SECTION, aiText);
        if (commands.isEmpty()) {
            // Provide some generic, safe fallbacks
            commands = List.of(
                    "kubectl get nodes -o wide",
                    "kubectl get pods -A -o wide",
                    "kubectl describe pod <pod> -n <namespace>",
                    "kubectl get events -A --sort-by=.lastTimestamp"
            );
        }
        resp.setSuggestedKubectlCommands(commands);

        List<String> optimize = extractLines(OPTIMIZE_SECTION, aiText);
        if (optimize.isEmpty()) {
            optimize = baselineOptimizations(state);
        }
        resp.setOptimizationAdvice(optimize);

        resp.setClusterSummary(kubernetesService.buildSummary(state));
        return resp;
    }

    private static List<String> extractLines(Pattern section, String text) {
        Matcher m = section.matcher(text);
        if (!m.find()) return List.of();
        String body = m.group(1).trim();
        String[] lines = body.split("\\R+");
        List<String> out = new ArrayList<>();
        for (String l : lines) {
            String t = l.strip();
            if (!t.isBlank()) out.add(t);
        }
        return out;
    }

    private List<String> baselineOptimizations(ClusterState state) {
        List<String> tips = new ArrayList<>();
        int pods = state.getPods() == null ? 0 : state.getPods().size();
        int nodes = state.getNodes() == null ? 0 : state.getNodes().size();
        if (nodes > 0 && pods == 0) {
            tips.add("Cluster appears idle: consider scaling node group to minimum or pausing dev environments.");
        }
        tips.add("Review resource requests/limits for underutilized workloads; align requests to p95 usage.");
        tips.add("Enable horizontal pod autoscaling (HPA) for variable-traffic deployments.");
        tips.add("Check recent Warning events for crashloops or image pull issues.");
        return tips;
    }
}

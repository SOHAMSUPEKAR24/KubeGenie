package com.kubegenie.service;

import com.kubegenie.model.ClusterState;
import com.kubegenie.payload.QueryResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.openai.OpenAiChatClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class AIServiceTest {

    @Test
    void summarize_parsesSections() {
        // Use OpenAiChatClient instead of OpenAiChatModel
        OpenAiChatClient mockClient = Mockito.mock(OpenAiChatClient.class);

        KubernetesService kubeSvc = Mockito.mock(KubernetesService.class);
        when(kubeSvc.buildSummary(Mockito.any())).thenReturn(java.util.Map.of("podsTotal", 0));

        AIService svc = new AIService(mockClient, kubeSvc);

        String text = """
                Everything looks fine.

                ```commands
                kubectl get pods -A
                kubectl describe pod foo -n bar
                ```
                ```optimize
                - reduce requests for api deployment
                ```
                """;

        QueryResponse resp = svc.summarizeIntoResponse(text, new ClusterState());
        assertEquals(2, resp.getSuggestedKubectlCommands().size());
        assertEquals(1, resp.getOptimizationAdvice().size());
    }
}

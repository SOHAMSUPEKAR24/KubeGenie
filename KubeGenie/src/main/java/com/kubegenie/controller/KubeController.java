package com.kubegenie.controller;

import com.kubegenie.model.ClusterState;
import com.kubegenie.payload.QueryRequest;
import com.kubegenie.payload.QueryResponse;
import com.kubegenie.service.AIService;
import com.kubegenie.service.KubernetesService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class KubeController {

    private final KubernetesService kubernetesService;
    private final AIService aiService;

    public KubeController(KubernetesService kubernetesService, AIService aiService) {
        this.kubernetesService = kubernetesService;
        this.aiService = aiService;
    }

    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public QueryResponse queryCluster(@Valid @RequestBody QueryRequest request) {
        ClusterState state = kubernetesService.snapshotClusterState();
        String aiExplanation = aiService.explainClusterState(request.getQuery(), state);

        // The AI response may include suggested commands and optimization tips.
        // We parse simple sections if present; otherwise, AIService will synthesize them.
        return aiService.summarizeIntoResponse(aiExplanation, state);
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}

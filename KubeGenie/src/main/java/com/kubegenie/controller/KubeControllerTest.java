package com.kubegenie.controller;

import com.kubegenie.model.ClusterState;
import com.kubegenie.payload.QueryRequest;
import com.kubegenie.payload.QueryResponse;
import com.kubegenie.service.AIService;
import com.kubegenie.service.KubernetesService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KubeController.class)
class KubeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    KubernetesService kubernetesService;

    @MockBean
    AIService aiService;

    @Test
    void queryCluster_ok() throws Exception {
        Mockito.when(kubernetesService.snapshotClusterState())
                .thenReturn(new ClusterState());
        Mockito.when(aiService.explainClusterState(Mockito.anyString(), Mockito.any()))
                .thenReturn("AI: cluster looks healthy.");
        Mockito.when(aiService.summarizeIntoResponse(Mockito.anyString(), Mockito.any()))
                .thenReturn(new QueryResponse());

        String payload = """
                {"query":"Why are my pods CrashLoopBackOff?"}
                """;

        mockMvc.perform(post("/api/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());
    }
}

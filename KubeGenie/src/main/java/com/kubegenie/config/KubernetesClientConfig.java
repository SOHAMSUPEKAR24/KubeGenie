package com.kubegenie.config;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileReader;

@Configuration
public class KubernetesClientConfig {

    /**
     * If kubernetes.config is provided, use that kubeconfig file.
     * Otherwise, try in-cluster config; if that fails, fall back to default kubeconfig (~/.kube/config).
     */
    @Bean
    public ApiClient apiClient(
            @Value("${kubernetes.config:}") String kubeConfigPath
    ) throws Exception {
        ApiClient client;
        if (kubeConfigPath != null && !kubeConfigPath.isBlank()) {
            try (FileReader fr = new FileReader(kubeConfigPath)) {
                KubeConfig config = KubeConfig.loadKubeConfig(fr);
                client = ClientBuilder.kubeconfig(config).build();
            }
        } else {
            try {
                client = ClientBuilder.cluster().build(); // In-cluster
            } catch (Exception e) {
                client = ClientBuilder.defaultClient(); // Local default (~/.kube/config)
            }
        }
        // Reasonable timeouts
        client.setConnectTimeout(10_000);
        client.setReadTimeout(30_000);
        io.kubernetes.client.openapi.Configuration.setDefaultApiClient(client);
        return client;
    }
}

package com.kubegenie.config;

import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI's OpenAiChatClient can talk to Groq when we point baseUrl to Groq's OpenAI-compatible endpoint.
 */
@Configuration
public class OpenAIConfig {

    @Bean
    public OpenAiApi openAiApi(
            @Value("${spring.ai.openai.api-key}") String apiKey,
            @Value("${spring.ai.openai.base-url:https://api.groq.com/openai/v1}") String baseUrl
    ) {
        return new OpenAiApi(baseUrl, apiKey);
    }

    @Bean
    public OpenAiChatClient openAiChatClient(OpenAiApi openAiApi,
                                             @Value("${kubegenie.ai.model:llama3-70b-8192}") String model) {
        // The model name should match a Groq-supported model (e.g., llama3-70b-8192 or mixtral-8x7b-32768)
        return new OpenAiChatClient(openAiApi, model);
    }
}

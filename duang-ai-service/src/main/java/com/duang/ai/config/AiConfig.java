package com.duang.ai.config;

import com.duang.ai.config.vl.VlConnectionProperties;
import com.duang.ai.config.vl.VlProperties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(OpenAiChatModel openAiChatModel) {
        return ChatClient.builder(openAiChatModel).build();
    }

    @Bean("vl")
    @ConditionalOnProperty("vl.api-key")
    public ChatClient vlChatClient(VlConnectionProperties connectionProperties, VlProperties properties) {
        OpenAiApi openAiApi = OpenAiApi.builder().apiKey(connectionProperties.getApiKey()).baseUrl(connectionProperties.getBaseUrl()).build();
        OpenAiChatModel openAiChatModel = OpenAiChatModel.builder().openAiApi(openAiApi).defaultOptions(properties.getOptions()).build();
        return ChatClient.builder(openAiChatModel).build();
    }

}

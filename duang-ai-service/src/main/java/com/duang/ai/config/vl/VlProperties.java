package com.duang.ai.config.vl;

import lombok.Data;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(VlProperties.CONFIG_PREFIX)
public class VlProperties {

    public static final String CONFIG_PREFIX = "vl.chat";

    @NestedConfigurationProperty
    private OpenAiChatOptions options;

}

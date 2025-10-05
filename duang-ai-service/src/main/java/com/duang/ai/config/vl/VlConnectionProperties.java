package com.duang.ai.config.vl;

import com.duang.ai.config.AiParentProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(VlConnectionProperties.CONFIG_PREFIX)
public class VlConnectionProperties extends AiParentProperties {

    public static final String CONFIG_PREFIX = "vl";

}

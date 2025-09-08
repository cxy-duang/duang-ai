# 项目介绍

基于`OpenAI`规范的基础`spring-ai`项目，提供了`非流式输出`、`流式输出`、`VL模型调用`

## 快速开始

在配置文件中填写`api-key`后启动，配置文件中提供了`DeepSeek`和`Qwen`的配置

## 接口介绍

1. `/ai/chat`：非流式输出
2. `/ai/chat/stream`：流式输出
3. `/vl/chat`：视觉模型，由于视觉模型一般只识别图片OCR，所以`PDF`需要转换为图片，并提供了`usage`等相关信息

## 依赖选择介绍

1. `spring-boot`版本选择`3.3.13`，因为`spring-cloud-alibaba`，目前最新版本是`2023.0.3.3`，最多支持到`spring-boot`的`3.3.x`

    ```xml
        <properties>
            <spring-cloud.version>2023.0.6</spring-cloud.version>
            <spring-alibaba.version>2023.0.3.3</spring-alibaba.version>
        </properties>
    
        <dependencyManagement>
            <dependencies>
                <dependency>
                    <groupId>org.springframework.ai</groupId>
                    <artifactId>spring-ai-bom</artifactId>
                    <version>1.0.1</version>
                    <type>pom</type>
                    <scope>import</scope>
                </dependency>
                <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-dependencies</artifactId>
                    <version>${spring-cloud.version}</version>
                    <type>pom</type>
                    <scope>import</scope>
                </dependency>
                <dependency>
                    <groupId>com.alibaba.cloud</groupId>
                    <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                    <version>${spring-alibaba.version}</version>
                    <type>pom</type>
                    <scope>import</scope>
                </dependency>
            </dependencies>
        </dependencyManagement>
    ```

## 坑

1. 流式输出`stream`必须包装一个类`ChunkDTO`否则前端无法正常显示
2. 配置文件`base-url`不需要填写`/v1`或`/v1/chat/completions`，因为`org.springframework.ai.model.openai.autoconfigure.OpenAiChatProperties`默认`DEFAULT_COMPLETIONS_PATH`为`/v1/chat/completions`
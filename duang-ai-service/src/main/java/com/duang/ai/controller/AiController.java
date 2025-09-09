package com.duang.ai.controller;

import com.alibaba.fastjson2.JSON;
import com.duang.ai.dto.ChatInputDTO;
import com.duang.ai.dto.ChatOutputDTO;
import com.duang.ai.dto.ChunkDTO;
import com.duang.ai.dto.UsageDTO;
import com.duang.ai.utils.AiUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Validated
@RestController
@RequestMapping("/ai")
public class AiController {

    public static final String PDF = "pdf";

    @Autowired
    private ChatClient chatClient;

    @Autowired
    @Qualifier("vl")
    private ChatClient vlChatClient;

    @RequestMapping(value = "/chat", method = RequestMethod.POST)
    public String chat(@RequestBody ChatInputDTO inputDTO) {
        String system = inputDTO.getSystem();
        String user = inputDTO.getUser();

        ChatClient.ChatClientRequestSpec prompt = chatClient.prompt();
        // 提示词
        if (StringUtils.hasText(system)) {
            prompt.system(system);
        }
        return prompt.user(user).call().content();
    }

    @RequestMapping(value = "/chat/stream", method = RequestMethod.POST, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestBody ChatInputDTO inputDTO) {
        String system = inputDTO.getSystem();
        String user = inputDTO.getUser();

        ChatClient.ChatClientRequestSpec prompt = chatClient.prompt();
        // 提示词
        if (StringUtils.hasText(system)) {
            prompt.system(system);
        }
        return prompt.user(user).stream().content().map(e -> JSON.toJSONString(new ChunkDTO(e)));
    }

    @RequestMapping(value = "/vl/chat", method = RequestMethod.POST)
    public ChatOutputDTO clChat(ChatInputDTO inputDTO) {
        long startTimeMillis = System.currentTimeMillis();
        String system = inputDTO.getSystem();
        String user = inputDTO.getUser();

        List<MultipartFile> files = inputDTO.getFiles();


        List<Media> imageMediaList = new ArrayList<>();

        for (MultipartFile file : files) {
            String contentType = file.getContentType();

            // 图片
            if (Objects.equals(MediaType.IMAGE_PNG_VALUE, contentType) || Objects.equals(MediaType.IMAGE_JPEG_VALUE, contentType) || Objects.equals(MediaType.IMAGE_GIF_VALUE, contentType)) {
                imageMediaList.add(new Media(MediaType.IMAGE_PNG, file.getResource()));
            }

            // PDF
            if (Objects.equals(MediaType.APPLICATION_PDF_VALUE, contentType)) {
                try {
                    imageMediaList.addAll(AiUtils.pdfToImages(file.getInputStream()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }

        // 提示词
        Message systemMessage = new SystemMessage(system);

        // 用户输入
        UserMessage userMessage = UserMessage.builder().media(imageMediaList).text(user).build();

        // 构建整体提示词
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        ChatClient.CallResponseSpec call = vlChatClient.prompt(prompt).call();

        ChatResponse chatResponse = call.chatResponse();

        // token
        ChatResponseMetadata metadata = chatResponse.getMetadata();
        String model = metadata.getModel();
        Usage usage = metadata.getUsage();

        UsageDTO usageDTO = new UsageDTO();
        usageDTO.setPromptTokens(usage.getPromptTokens());
        usageDTO.setCompletionTokens(usage.getCompletionTokens());
        usageDTO.setTotalTokens(usage.getTotalTokens());

        // 结果
        String content = AiUtils.getContentFromChatResponse(chatResponse);

        ChatOutputDTO chatOutputDTO = new ChatOutputDTO();
        chatOutputDTO.setModel(model);
        chatOutputDTO.setUsage(usageDTO);
        chatOutputDTO.setContent(content);
        chatOutputDTO.setTimeMillis(System.currentTimeMillis() - startTimeMillis);
        return chatOutputDTO;
    }

}

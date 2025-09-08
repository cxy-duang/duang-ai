package com.duang.ai.dto;

import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@ToString
public class ChatInputDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -2619895873428965088L;

    /**
     * 提示词
     */
    private String system;

    /**
     * 用户输入
     */
    private String user;

    /**
     * 支持图片和PDF
     */
    private List<MultipartFile> files;

}

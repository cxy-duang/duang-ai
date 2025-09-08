package com.duang.ai.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Data
@ToString
public class ChatOutputDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7728481490773493231L;

    /**
     * 模型名称
     */
    private String model;

    /**
     * Token信息
     */
    private UsageDTO usage;

    /**
     * 结果
     */
    private String content;

    /**
     * 耗时
     */
    private Long timeMillis;

}

package com.duang.ai.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Data
@ToString
public class UsageDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4821107301156897696L;

    /**
     * 用户的输入转换成 Token 后的长度
     */
    private Integer promptTokens;

    /**
     * 用户的输入转换成 Token 后的长度
     */
    private Integer completionTokens;

    /**
     * prompt_tokens与completion_tokens的总和
     */
    private Integer totalTokens;

}

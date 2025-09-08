package com.duang.ai.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Data
@ToString
public class ChunkDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -5838368730500994230L;

    public ChunkDTO(String c) {
        this.c = c;
    }

    private String c;

}

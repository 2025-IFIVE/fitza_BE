package com.ifive.fitza.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CoordinationItemDTO {
    private Long clothId;
    private Double x;
    private Double y;
    private Double size;
    private String imagePath;
}

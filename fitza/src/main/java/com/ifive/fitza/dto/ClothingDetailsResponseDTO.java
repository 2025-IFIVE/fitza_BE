package com.ifive.fitza.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClothingDetailsResponseDTO {
    private Long clothid;
    private String type;
    private String category;
    private String length;
    private String sleeve;
    private String neckline;
    private String neck;
    private String fit;
    private String color;
    private String material;
    private String detail;
    private String print;
    private String style;
    private String substyle;
    private String imagePath;
}


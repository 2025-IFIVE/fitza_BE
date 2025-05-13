package com.ifive.fitza.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileResponseDTO {
    private Long id;
    private String style;
    private String comment;
    private String imagePath;
    private String nickname;  
}


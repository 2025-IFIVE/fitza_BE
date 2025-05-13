package com.ifive.fitza.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileRequestDTO {
    private String style;   // 예: "모던,로맨틱"
    private String comment;
}

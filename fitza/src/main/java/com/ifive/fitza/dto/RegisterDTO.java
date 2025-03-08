package com.ifive.fitza.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {
    private String username; // 아이디
    private String name;  // 사용자 이름 추가
    private String password; // 비밀번호
    private String nickname; // 닉네임
    private String phone; // 전화번호
}

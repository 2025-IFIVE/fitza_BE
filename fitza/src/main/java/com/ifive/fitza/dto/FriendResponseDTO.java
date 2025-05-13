package com.ifive.fitza.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FriendResponseDTO {
    private Long id;          // 친구의 사용자 ID
    private String username;  // 친구의 로그인 ID
    private String nickname;  // 친구의 닉네임
}


package com.ifive.fitza.dto;

import com.ifive.fitza.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private String name;  // 사용자 이름 추가
    private String username; // 아이디
    private String nickname; // 닉네임
    private String phone; // 전화번호
    public static UserResponseDTO toDto(UserEntity entity) {
        return UserResponseDTO.builder()
                .username(entity.getUsername())
                .name(entity.getName()) // 추가: name 설정
                .nickname(entity.getNickname()) // 추가: nickname 설정
                .phone(entity.getPhone()) // 추가: phone 설정
                .build();
    }
}
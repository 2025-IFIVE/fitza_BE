package com.ifive.fitza.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FriendRequestResponseDTO {
    private Long requestId;    // 친구 요청 ID (FriendEntity.id)
    private String username;   // 친구 요청 보낸 사람의 username
    private String nickname;   // 친구 요청 보낸 사람의 nickname
}


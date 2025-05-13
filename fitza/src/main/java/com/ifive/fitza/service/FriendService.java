package com.ifive.fitza.service;

import com.ifive.fitza.dto.FriendRequestDTO;
import com.ifive.fitza.dto.FriendResponseDTO;
import com.ifive.fitza.entity.FriendEntity;
import com.ifive.fitza.entity.UserEntity;
import com.ifive.fitza.repository.FriendRepository;
import com.ifive.fitza.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    // 친구 신청
    public void sendFriendRequest(String username, Long friendId) {
        UserEntity user = getUserByUsername(username);
        UserEntity friend = getUserById(friendId);

        if (friendRepository.findByUserAndFriend(user, friend).isPresent()) {
            throw new IllegalStateException("이미 신청한 친구입니다.");
        }

        FriendEntity request = FriendEntity.builder()
                .user(user)
                .friend(friend)
                .status("PENDING")
                .build();
        friendRepository.save(request);
    }

    // 친구 수락/거절
    @Transactional
    public void respondToRequest(String username, Long requestId, boolean accept) {
        UserEntity responder = getUserByUsername(username);
        FriendEntity request = friendRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("요청이 존재하지 않습니다."));

        if (!request.getFriend().getUserid().equals(responder.getUserid())) {
            throw new IllegalStateException("응답할 권한이 없습니다.");
        }

        request.setStatus(accept ? "ACCEPTED" : "REJECTED");
    }

    // 친구 목록 조회
    public List<FriendResponseDTO> getFriends(String username) {
        UserEntity user = getUserByUsername(username);

        List<FriendEntity> accepted = friendRepository.findByUserOrFriendAndStatus(user, user, "ACCEPTED");

        return accepted.stream()
                .map(f -> {
                    UserEntity target = f.getUser().equals(user) ? f.getFriend() : f.getUser();
                    return new FriendResponseDTO(target.getUserid(), target.getUsername(), target.getNickname());
                })
                .collect(Collectors.toList());
    }

    private UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
    }

    private UserEntity getUserById(Long id) {
        return userRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저 없음"));
    }

    //친구 삭제
    public void deleteFriend(String username, Long friendId) {
    UserEntity user = getUserByUsername(username);
    UserEntity target = getUserById(friendId);

    // 친구 관계를 양방향으로 조회
    FriendEntity relation = friendRepository.findByUserAndFriend(user, target)
            .orElseGet(() -> friendRepository.findByUserAndFriend(target, user)
                    .orElseThrow(() -> new IllegalArgumentException("해당 친구 관계가 존재하지 않습니다.")));

    if (!"ACCEPTED".equals(relation.getStatus())) {
        throw new IllegalStateException("수락된 친구만 삭제할 수 있습니다.");
    }

    friendRepository.delete(relation);
    }
}


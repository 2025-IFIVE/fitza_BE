package com.ifive.fitza.service;

import com.ifive.fitza.dto.FriendRequestDTO;
import com.ifive.fitza.dto.FriendRequestResponseDTO;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    // 친구 신청
    // 기존 거절 기록 삭제 후 재신청 허용
    public void sendFriendRequest(String username, String friendPhone) {
        UserEntity user = getUserByUsername(username);
        UserEntity friend = getUserByPhone(friendPhone);

        if (user.getUserid().equals(friend.getUserid())) {
            throw new IllegalArgumentException("자기 자신에게 친구 요청을 보낼 수 없습니다.");
        }

        Optional<FriendEntity> existing = friendRepository.findByUserAndFriend(user, friend);

        if (existing.isPresent()) {
            String status = existing.get().getStatus();
            if ("PENDING".equals(status)) {
                throw new IllegalStateException("이미 친구 요청을 보냈습니다.");
            } else if ("ACCEPTED".equals(status)) {
                throw new IllegalStateException("이미 친구입니다.");
            } else if ("REJECTED".equals(status)) {
                friendRepository.delete(existing.get());
            }
        }

        FriendEntity request = FriendEntity.builder()
                .user(user)
                .friend(friend)
                .status("PENDING")
                .build();

        friendRepository.save(request);
    }

    private UserEntity getUserByPhone(String phone) {
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new IllegalArgumentException("해당 전화번호의 사용자를 찾을 수 없습니다."));
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
    UserEntity me = getUserByUsername(username);

    List<FriendEntity> accepted = friendRepository.findAcceptedFriends(me);  //ACCEPTED만 조회

    return accepted.stream()
            .map(friendEntity -> {
                UserEntity target = friendEntity.getUser().equals(me)
                        ? friendEntity.getFriend()
                        : friendEntity.getUser();

                return new FriendResponseDTO(
                        target.getUserid(),
                        target.getUsername(),
                        target.getNickname()
                );
            })
            .distinct()
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

    public List<FriendRequestResponseDTO> getReceivedRequests(String username) {
    UserEntity me = getUserByUsername(username);

    // 나에게 온 친구 요청 중 PENDING 상태만 조회
    List<FriendEntity> requests = friendRepository.findByFriendAndStatus(me, "PENDING");

    return requests.stream()
            .map(req -> {
                UserEntity sender = req.getUser(); // 신청 보낸 유저
                return new FriendRequestResponseDTO(
                        req.getId(),
                        sender.getUsername(),
                        sender.getNickname()
                );
            })
            .collect(Collectors.toList());
    }
}


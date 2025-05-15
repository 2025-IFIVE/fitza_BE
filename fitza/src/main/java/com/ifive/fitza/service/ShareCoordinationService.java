package com.ifive.fitza.service;

import com.ifive.fitza.dto.*;
import com.ifive.fitza.entity.*;
import com.ifive.fitza.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShareCoordinationService {

    private final UserRepository userRepository;
    private final ShareCoordinationRepository shareRepo;
    private final ShareCoordinationItemRepository itemRepo;
    private final ClothingRepository clothingRepository;
    private final FriendRepository friendRepository;

    // 공유 코디 등록
    public void saveShareCoordination(String username, ShareCoordinationRequestDTO dto) {
        UserEntity user = getUserByUsername(username);

        ShareCoordination share = ShareCoordination.builder()
                .title(dto.getTitle())
                .date(dto.getDate())
                .weather(dto.getWeather())
                .user(user)
                .build();
        shareRepo.save(share);

        List<ShareCoordinationItem> items = dto.getItems().stream().map(itemDTO -> {
            ClothingDetails clothing = clothingRepository.findById(itemDTO.getClothId())
                    .orElseThrow(() -> new RuntimeException("옷 ID 오류"));
            return ShareCoordinationItem.builder()
                    .share(share)
                    .clothing(clothing)
                    .x(itemDTO.getX())
                    .y(itemDTO.getY())
                    .size(itemDTO.getSize())
                    .build();
        }).toList();

        itemRepo.saveAll(items);
        share.setItems(items);
    }

    // 공유 코디 단건 조회 (본인 또는 친구만)
    public ShareCoordinationResponseDTO getShareCoordination(String requesterUsername, Long shareId) {
        ShareCoordination share = shareRepo.findById(shareId)
                .orElseThrow(() -> new RuntimeException("코디 없음"));

        if (!isFriendOrSelf(requesterUsername, share.getUser())) {
            throw new RuntimeException("조회 권한 없음");
        }

        List<CoordinationItemDTO> items = itemRepo.findByShare(share).stream().map(item -> {
            ClothingDetails clothing = item.getClothing();
            return CoordinationItemDTO.builder()
                    .clothId(clothing.getClothid())
                    .x(item.getX())
                    .y(item.getY())
                    .size(item.getSize())
                    .imagePath(clothing.getImagePath())
                    .build();
        }).toList();

        return ShareCoordinationResponseDTO.builder()
                .shareId(share.getShareId())
                .title(share.getTitle())
                .date(share.getDate())
                .weather(share.getWeather())
                .ownerNickname(share.getUser().getNickname())
                .items(items)
                .build();
    }

    // 내 공유 코디 전체 조회
    public List<ShareCoordinationResponseDTO> getMyShareCoordinations(String username) {
        UserEntity user = getUserByUsername(username);
        return shareRepo.findByUser(user).stream().map(this::toDTO).toList();
    }

    // 친구들의 공유 코디 전체 조회
    public List<ShareCoordinationResponseDTO> getFriendShares(String username) {
        UserEntity user = getUserByUsername(username);
        List<FriendEntity> friends = friendRepository.findByUserOrFriendAndStatus(user, user, "ACCEPTED");

        return friends.stream()
                .map(f -> f.getUser().equals(user) ? f.getFriend() : f.getUser())
                .flatMap(friend -> shareRepo.findByUser(friend).stream())
                .map(this::toDTO)
                .toList();
    }

    // 특정 친구 공유 코디 조회
    public List<ShareCoordinationResponseDTO> getFriendSharesById(String requester, Long friendId) {
        UserEntity requesterUser = getUserByUsername(requester);
        UserEntity friend = getUserById(friendId);

        if (!isFriendOrSelf(requester, friend)) {
            throw new RuntimeException("조회 권한 없음");
        }

        return shareRepo.findByUser(friend).stream().map(this::toDTO).toList();
    }

    // 공유 코디 수정 (덮어쓰기)
    @Transactional
    public void updateShareCoordination(Long shareId, ShareCoordinationRequestDTO dto, String username) {
        ShareCoordination share = shareRepo.findById(shareId)
                .orElseThrow(() -> new RuntimeException("코디 없음"));

        if (!share.getUser().getUsername().equals(username)) {
            throw new RuntimeException("수정 권한 없음");
        }

        share.setTitle(dto.getTitle());
        share.setDate(dto.getDate());
        share.setWeather(dto.getWeather());

        itemRepo.deleteByShare(share);

        List<ShareCoordinationItem> newItems = dto.getItems().stream().map(itemDTO -> {
            ClothingDetails clothing = clothingRepository.findById(itemDTO.getClothId())
                    .orElseThrow(() -> new RuntimeException("옷 ID 오류"));
            return ShareCoordinationItem.builder()
                    .share(share)
                    .clothing(clothing)
                    .x(itemDTO.getX())
                    .y(itemDTO.getY())
                    .size(itemDTO.getSize())
                    .build();
        }).toList();

        itemRepo.saveAll(newItems);
    }

    // 삭제
    @Transactional
    public void deleteShareCoordination(Long shareId, String username) {
        ShareCoordination share = shareRepo.findById(shareId)
                .orElseThrow(() -> new RuntimeException("코디 없음"));

        if (!share.getUser().getUsername().equals(username)) {
            throw new RuntimeException("삭제 권한 없음");
        }

        itemRepo.deleteByShare(share);
        shareRepo.delete(share);
    }

    private UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
    }

    private UserEntity getUserById(Long id) {
        return userRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new RuntimeException("유저 ID 없음"));
    }

    private ShareCoordinationResponseDTO toDTO(ShareCoordination share) {
        List<CoordinationItemDTO> items = itemRepo.findByShare(share).stream().map(item -> {
            ClothingDetails clothing = item.getClothing();
            return CoordinationItemDTO.builder()
                    .clothId(clothing.getClothid())
                    .x(item.getX())
                    .y(item.getY())
                    .size(item.getSize())
                    .imagePath(clothing.getImagePath())
                    .build();
        }).toList();

        return ShareCoordinationResponseDTO.builder()
                .shareId(share.getShareId())
                .title(share.getTitle())
                .date(share.getDate())
                .weather(share.getWeather())
                .ownerNickname(share.getUser().getNickname())
                .items(items)
                .build();
    }

    private boolean isFriendOrSelf(String requesterUsername, UserEntity target) {
        if (target.getUsername().equals(requesterUsername)) return true;

        UserEntity requester = getUserByUsername(requesterUsername);
        return friendRepository.findByUserOrFriendAndStatus(requester, target, "ACCEPTED")
                .stream()
                .anyMatch(f -> f.getUser().equals(requester) && f.getFriend().equals(target) ||
                               f.getUser().equals(target) && f.getFriend().equals(requester));
    }
}

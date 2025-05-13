package com.ifive.fitza.service;

import com.ifive.fitza.dto.ProfileRequestDTO;
import com.ifive.fitza.dto.ProfileResponseDTO;
import com.ifive.fitza.entity.ProfileEntity;
import com.ifive.fitza.entity.UserEntity;
import com.ifive.fitza.repository.ProfileRepository;
import com.ifive.fitza.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    private final String PROFILE_DIR = System.getProperty("user.dir") + File.separator + "profileimages";

    // 프로필 생성 또는 수정
    public ProfileResponseDTO saveOrUpdateProfile(String username, ProfileRequestDTO request, MultipartFile imageFile) throws IOException {
    UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

    File dir = new File(PROFILE_DIR);
    if (!dir.exists()) dir.mkdirs();

    // 기존 프로필 있는 경우 기존 이미지 삭제
    ProfileEntity profile = profileRepository.findByUser(user).orElse(
            ProfileEntity.builder().user(user).build()
    );

    if (profile.getImagePath() != null) {
        String fullPath = System.getProperty("user.dir") + profile.getImagePath().replace("/", File.separator);
        File oldFile = new File(fullPath);
        if (oldFile.exists()) {
            oldFile.delete(); // 💣 기존 이미지 삭제
        }
    }

    // 새 이미지 저장
    String savedFilename = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
    File destination = new File(dir, savedFilename);
    imageFile.transferTo(destination);

    String imagePath = "/profileimages/" + savedFilename;

    // 프로필 정보 업데이트
    profile.setStyle(request.getStyle());
    profile.setComment(request.getComment());
    profile.setImagePath(imagePath);

    ProfileEntity saved = profileRepository.save(profile);
    return toDTO(saved);
}

    // 프로필 조회
    public ProfileResponseDTO getProfile(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        ProfileEntity profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("프로필이 존재하지 않습니다."));

        return toDTO(profile);
    }

    private ProfileResponseDTO toDTO(ProfileEntity entity) {
        return ProfileResponseDTO.builder()
            .id(entity.getId())
            .style(entity.getStyle())
            .comment(entity.getComment())
            .imagePath(entity.getImagePath())
            .nickname(entity.getUser().getNickname())  
            .build();
    }
}


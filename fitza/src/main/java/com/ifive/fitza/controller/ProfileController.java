package com.ifive.fitza.controller;

import com.ifive.fitza.dto.ProfileRequestDTO;
import com.ifive.fitza.dto.ProfileResponseDTO;
import com.ifive.fitza.jwt.JWTUtil;
import com.ifive.fitza.response.ResponseDTO;
import com.ifive.fitza.service.ProfileService;
import com.ifive.fitza.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final JWTUtil jwtUtil;

    // 프로필 생성 또는 수정
    @PostMapping
    public ResponseEntity<ResponseDTO> createOrUpdateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestPart("file") MultipartFile image,
            @RequestPart("style") String style,
            @RequestPart("comment") String comment
    ) throws Exception {
        String username = jwtUtil.getUsername(authHeader.replace("Bearer ", ""));

        ProfileRequestDTO request = new ProfileRequestDTO();
        request.setStyle(style);
        request.setComment(comment);

        ProfileResponseDTO result = profileService.saveOrUpdateProfile(username, request, image);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_UPDATE.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_UPDATE, result));
    }

    // 프로필 조회
    @GetMapping
    public ResponseEntity<ResponseDTO> getProfile(
            @RequestHeader("Authorization") String authHeader
    ) {
        String username = jwtUtil.getUsername(authHeader.replace("Bearer ", ""));
        ProfileResponseDTO profile = profileService.getProfile(username);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_USER.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_USER, profile));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDTO> getProfileByUserId(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long userId) {

        String username = jwtUtil.getUsername(authHeader.replace("Bearer ", ""));

        ProfileResponseDTO profile = profileService.getProfileByUserId(userId);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_RETRIEVE_USER.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_USER, profile));
    }


    @PutMapping
    public ResponseEntity<ResponseDTO> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestPart("file") MultipartFile image,
            @RequestPart("style") String style,
            @RequestPart("comment") String comment
    ) throws Exception {
        String username = jwtUtil.getUsername(authHeader.replace("Bearer ", ""));

        ProfileRequestDTO request = new ProfileRequestDTO();
        request.setStyle(style);
        request.setComment(comment);

        ProfileResponseDTO result = profileService.saveOrUpdateProfile(username, request, image);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_UPDATE.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_UPDATE, result));
    }

}


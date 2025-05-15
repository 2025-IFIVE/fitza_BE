package com.ifive.fitza.controller;

import com.ifive.fitza.code.SuccessCode;
import com.ifive.fitza.dto.ShareCoordinationRequestDTO;
import com.ifive.fitza.dto.ShareCoordinationResponseDTO;
import com.ifive.fitza.jwt.JWTUtil;
import com.ifive.fitza.response.ResponseDTO;
import com.ifive.fitza.service.ShareCoordinationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/share")
@RequiredArgsConstructor
public class ShareCoordinationController {

    private final ShareCoordinationService shareCoordinationService;
    private final JWTUtil jwtUtil;

    // 공유 코디 등록
    @PostMapping
    public ResponseEntity<ResponseDTO> saveShare(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ShareCoordinationRequestDTO dto) {

        String username = jwtUtil.getUsername(authHeader.replace("Bearer ", ""));
        shareCoordinationService.saveShareCoordination(username, dto);
        return ResponseEntity.ok(new ResponseDTO<>(SuccessCode.SUCCESS_CREATE_COORDINATION, null));
    }

    // 공유 코디 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ShareCoordinationResponseDTO> getShare(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        String username = jwtUtil.getUsername(authHeader.replace("Bearer ", ""));
        ShareCoordinationResponseDTO result = shareCoordinationService.getShareCoordination(username, id);
        return ResponseEntity.ok(result);
    }

    // 내 공유 코디 전체 조회
    @GetMapping("/my")
    public ResponseEntity<List<ShareCoordinationResponseDTO>> getMyShares(
            @RequestHeader("Authorization") String authHeader) {

        String username = jwtUtil.getUsername(authHeader.replace("Bearer ", ""));
        return ResponseEntity.ok(shareCoordinationService.getMyShareCoordinations(username));
    }

    // 친구 전체 공유 코디 조회
    @GetMapping("/friends")
    public ResponseEntity<List<ShareCoordinationResponseDTO>> getFriendShares(
            @RequestHeader("Authorization") String authHeader) {

        String username = jwtUtil.getUsername(authHeader.replace("Bearer ", ""));
        return ResponseEntity.ok(shareCoordinationService.getFriendShares(username));
    }

    // 특정 친구 공유 코디 조회
    @GetMapping("/friends/{friendId}")
    public ResponseEntity<List<ShareCoordinationResponseDTO>> getFriendShareById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long friendId) {

        String username = jwtUtil.getUsername(authHeader.replace("Bearer ", ""));
        return ResponseEntity.ok(shareCoordinationService.getFriendSharesById(username, friendId));
    }

    // 공유 코디 수정
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> updateShare(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestBody ShareCoordinationRequestDTO dto) {

        String username = jwtUtil.getUsername(authHeader.replace("Bearer ", ""));
        shareCoordinationService.updateShareCoordination(id, dto, username);
        return ResponseEntity.ok(new ResponseDTO<>(SuccessCode.SUCCESS_UPDATE_COORDINATION, null));
    }

    // 공유 코디 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deleteShare(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        String username = jwtUtil.getUsername(authHeader.replace("Bearer ", ""));
        shareCoordinationService.deleteShareCoordination(id, username);
        return ResponseEntity.ok(new ResponseDTO<>(SuccessCode.SUCCESS_DELETE_COORDINATION, null));
    }
}

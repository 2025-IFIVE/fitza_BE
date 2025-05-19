package com.ifive.fitza.controller;

import com.ifive.fitza.code.SuccessCode;
import com.ifive.fitza.dto.ClothingDetailsResponseDTO;
import com.ifive.fitza.entity.ClothingDetails;
import com.ifive.fitza.jwt.JWTUtil;
import com.ifive.fitza.response.ResponseDTO;
import com.ifive.fitza.service.ClothingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clothing")
public class ClothingController {

    private final ClothingService clothingService;
    private final JWTUtil jwtUtil;

    // 옷 등록 (이미지 전송)
    @PostMapping("/upload")
    public ResponseEntity<ClothingDetailsResponseDTO> uploadClothing(
            @RequestHeader("Authorization") String authHeader,
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsername(token);
        ClothingDetails saved = clothingService.saveClothing(file, username);
        return ResponseEntity.ok(clothingService.toDTO(saved));
    }

    // 로그인한 유저의 옷 전체 조회
    @GetMapping("/my")
    public ResponseEntity<List<ClothingDetailsResponseDTO>> getMyClothes(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsername(token);
        List<ClothingDetails> myClothes = clothingService.getClothingByUser(username);
        List<ClothingDetailsResponseDTO> dtos = myClothes.stream()
                .map(clothingService::toDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ClothingDetailsResponseDTO> getClothing(@PathVariable Long id) {
        ClothingDetails cloth = clothingService.getClothingById(id);
        return ResponseEntity.ok(clothingService.toDTO(cloth));
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deleteClothing(@PathVariable Long id) {
        clothingService.deleteClothing(id);
        return ResponseEntity
        .status(SuccessCode.SUCCESS_DELETE_CLOTHING.getStatus().value())
        .body(new ResponseDTO<>(SuccessCode.SUCCESS_DELETE_CLOTHING, null));
    }

    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<ClothingDetailsResponseDTO> updateClothing(
            @PathVariable Long id,
            @RequestBody ClothingDetailsResponseDTO requestDTO
    ) {
        ClothingDetails updated = clothingService.updateClothing(id, requestDTO);
        return ResponseEntity.ok(clothingService.toDTO(updated));
    }

    //cropped 이미지 수정
    @PutMapping("/{id}/cropped-image")
    public ResponseEntity<String> updateCroppedImage(
            @PathVariable("id") Long clothingId,
            @RequestParam("image") MultipartFile imageFile) {
        try {
            clothingService.updateCroppedImage(clothingId, imageFile);
            return ResponseEntity.ok("Cropped 이미지가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("이미지 수정 중 오류 발생: " + e.getMessage());
        }
    }
}

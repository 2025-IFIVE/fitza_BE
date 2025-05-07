package com.ifive.fitza.controller;

import com.ifive.fitza.dto.ClothingDetailsResponseDTO;
import com.ifive.fitza.entity.ClothingDetails;
import com.ifive.fitza.jwt.JWTUtil;
import com.ifive.fitza.service.ClothingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
}


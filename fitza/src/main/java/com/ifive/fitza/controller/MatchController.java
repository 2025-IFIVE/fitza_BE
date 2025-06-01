package com.ifive.fitza.controller;

import com.ifive.fitza.dto.MatchResultDTO;
import com.ifive.fitza.jwt.JWTUtil;
import com.ifive.fitza.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;
    private final JWTUtil jwtUtil;

    @PostMapping("/ootd")
    public ResponseEntity<?> matchOOTD(
            @RequestHeader("Authorization") String authHeader,
            @RequestPart("file") MultipartFile file) throws IOException {

        // 🔐 토큰에서 username과 token 추출
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsername(token);

        // ✅ 토큰까지 함께 전달
        MatchResultDTO result = matchService.matchOutfitWithUsername(file, username, token);

        return ResponseEntity.ok(result);
    }
}

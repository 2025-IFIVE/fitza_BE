package com.ifive.fitza.controller;

import com.ifive.fitza.dto.MatchResultDTO;
import com.ifive.fitza.service.MatchService;
import lombok.RequiredArgsConstructor;
import com.ifive.fitza.jwt.JWTUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

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

        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsername(token);
        MatchResultDTO result = matchService.matchOutfitWithUsername(file, username);
        return ResponseEntity.ok(result);
    }
}


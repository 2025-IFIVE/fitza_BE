package com.ifive.fitza.controller;

import com.ifive.fitza.response.ResponseDTO;
import com.ifive.fitza.code.SuccessCode;
import com.ifive.fitza.jwt.JWTUtil;
import com.ifive.fitza.service.BodyService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/body")
public class BodyController {

    private final JWTUtil jwtUtil;
    private final BodyService bodyService;

    @PostMapping("/analyze")
    public ResponseEntity<ResponseDTO> analyzeBody(
            @RequestHeader("Authorization") String authHeader,
            @RequestPart("file") MultipartFile file) throws IOException {

        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsername(token);

        String result = bodyService.analyzeAndSaveBodyShape(username, file);

        return ResponseEntity.ok(new ResponseDTO<>(SuccessCode.SUCCESS_ANALYZE_BODY, result));
    }
}

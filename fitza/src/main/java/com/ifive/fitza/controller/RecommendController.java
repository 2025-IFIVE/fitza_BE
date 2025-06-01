package com.ifive.fitza.controller;

import com.ifive.fitza.dto.RecommendRequestDTO;
import com.ifive.fitza.entity.UserEntity;
import com.ifive.fitza.jwt.JWTUtil;
import com.ifive.fitza.repository.UserRepository;
import com.ifive.fitza.response.ResponseDTO;
import com.ifive.fitza.code.SuccessCode;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String MODEL_SERVER_URL = "http://localhost:8000/recommend";

    @PostMapping
public ResponseEntity<ResponseDTO> getRecommendation(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody RecommendRequestDTO request) {

    String username = jwtUtil.getUsername(authHeader.replace("Bearer ", ""));
    UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("유저 정보를 찾을 수 없습니다."));
    Long userId = user.getUserid();

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("userId", userId);
    requestBody.put("weather", request.getWeather());

    // ✅ headers 설정
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

    ResponseEntity<Map> response = restTemplate.postForEntity(MODEL_SERVER_URL, entity, Map.class);

    return ResponseEntity.ok(
            new ResponseDTO<>(SuccessCode.SUCCESS_RECOMMEND, response.getBody())
    );
}

}

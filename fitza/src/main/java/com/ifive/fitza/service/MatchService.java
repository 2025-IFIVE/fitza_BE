package com.ifive.fitza.service;

import com.ifive.fitza.dto.MatchResultDTO;
import com.ifive.fitza.entity.UserEntity;
import com.ifive.fitza.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MatchService {

    // FastAPI 서버 주소 (POST /match)
    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8000")
            .build();

    private final UserRepository userRepository;

    public MatchResultDTO matchOutfitWithUsername(MultipartFile file, String username, String token) throws IOException {
        // DB에서 사용자 조회 (확인용, 불필요하면 생략 가능)
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        // MultipartFile → Resource로 래핑
        ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename(); // 파일명 유지
            }
        };

        // multipart/form-data 구성
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("file", fileResource);

        // WebClient로 FastAPI POST 요청 전송
        return webClient.post()
                .uri("/match")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(formData))
                .retrieve()
                .bodyToMono(MatchResultDTO.class)
                .block(); // 블로킹 방식으로 결과 대기
    }
}

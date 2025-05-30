package com.ifive.fitza.service;

import com.ifive.fitza.dto.MatchResultDTO;
import com.ifive.fitza.entity.UserEntity;
import com.ifive.fitza.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
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

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8000") // FastAPI 서버 주소
            .build();

    private final UserRepository userRepository;

    public MatchResultDTO matchOutfitWithUsername(MultipartFile file, String username) throws IOException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        // 파일을 전송 가능한 리소스로 래핑
        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename(); // 파일 이름 설정
            }
        };

        // multipart/form-data 구성
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("file", resource);
        formData.add("userId", String.valueOf(user.getUserid())); // userId는 Form 필드로 전송

        // WebClient 요청
        return webClient.post()
                .uri("/match")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(formData))
                .retrieve()
                .bodyToMono(MatchResultDTO.class)
                .block(); // 동기 처리
    }
}

package com.ifive.fitza.service;

import com.ifive.fitza.entity.UserEntity;
import com.ifive.fitza.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BodyService {

    private final UserRepository userRepository;
    private final WebClient webClient = WebClient.builder().baseUrl("http://localhost:8000").build();

    public String analyzeAndSaveBodyShape(String username, MultipartFile file) throws IOException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        List<String> result = webClient.post()
                .uri("/body-shape")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", resource))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .block();

        if (result == null || result.isEmpty()) {
            throw new RuntimeException("체형 분석 결과가 유효하지 않습니다.");
        }

        String bodyShape = result.get(0);
        user.setBodyinfo(bodyShape);
        userRepository.save(user);
        return bodyShape;
    }
}

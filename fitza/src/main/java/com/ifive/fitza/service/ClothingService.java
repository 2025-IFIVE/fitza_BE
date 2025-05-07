package com.ifive.fitza.service;

import com.ifive.fitza.dto.ClothingDetailsResponseDTO;
import com.ifive.fitza.entity.ClothingDetails;
import com.ifive.fitza.entity.UserEntity;
import com.ifive.fitza.repository.ClothingRepository;
import com.ifive.fitza.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClothingService {

    private final ClothingRepository clothingRepository;
    private final UserRepository userRepository;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8000")
            .build();

    public ClothingDetails saveClothing(MultipartFile file, String username) throws IOException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 1. 파일 저장
        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String originalFilename = file.getOriginalFilename();
        String savedFileName = System.currentTimeMillis() + "_" + originalFilename;
        File destination = new File(dir, savedFileName);
        file.transferTo(destination);

        String imagePath = "/uploads/" + savedFileName;

        // 2. FastAPI 호출
        byte[] imageBytes = Files.readAllBytes(destination.toPath());
        ByteArrayResource resource = new ByteArrayResource(imageBytes) {
            @Override
            public String getFilename() {
                return savedFileName;
            }
        };

        System.out.println("✅ WebClient 호출 전");
        Map<String, Object> result = webClient.post()
                .uri("/analyze")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", resource))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        System.out.println("✅ WebClient 응답 수신 완료");
        
        if (result == null || result.get("의류종류") == null) {
            throw new RuntimeException("FastAPI 응답이 유효하지 않습니다.");
        }

        String type = (String) result.get("의류종류");
        Map<String, Object> attrMap = (Map<String, Object>) ((Map<String, Object>) result.get("속성")).get(type);
        Map<String, Object> styleMap = (Map<String, Object>) result.get("스타일");

        // 3. 결과 저장
        ClothingDetails details = ClothingDetails.builder()
                .type(type)
                .category(parseAttr(attrMap.get("카테고리")))
                .length(parseAttr(attrMap.get("기장")))
                .sleeve(parseAttr(attrMap.get("소매기장")))
                .neckline(parseAttr(attrMap.get("넥라인")))
                .neck(parseAttr(attrMap.get("칼라")))
                .fit(parseAttr(attrMap.get("핏")))
                .color(parseAttr(attrMap.get("색상")))
                .material(parseAttr(attrMap.get("소재")))
                .detail(parseAttr(attrMap.get("디테일")))
                .print(parseAttr(attrMap.get("프린트")))
                .style(styleMap != null ? (String) styleMap.get("스타일") : null)
                .substyle(styleMap != null ? (String) styleMap.get("서브스타일") : null)
                .imagePath(imagePath)
                .user(user)
                .build();

        return clothingRepository.save(details);
    }

    private String parseAttr(Object obj) {
        if (obj instanceof List<?> list) {
            return String.join(", ", list.stream().map(String::valueOf).toList());
        } else if (obj instanceof String s) {
            return s;
        }
        return null;
    }

    public List<ClothingDetails> getClothingByUser(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
        return clothingRepository.findByUser(user);
    }

    public ClothingDetails getClothingById(Long id) {
        return clothingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 옷을 찾을 수 없습니다."));
    }

    public void deleteClothing(Long id) {
        clothingRepository.deleteById(id);
    }

    public ClothingDetails updateClothing(Long id, ClothingDetailsResponseDTO dto) {
        ClothingDetails cloth = getClothingById(id);

        cloth.setType(dto.getType());
        cloth.setCategory(dto.getCategory());
        cloth.setLength(dto.getLength());
        cloth.setSleeve(dto.getSleeve());
        cloth.setNeckline(dto.getNeckline());
        cloth.setNeck(dto.getNeck());
        cloth.setFit(dto.getFit());
        cloth.setColor(dto.getColor());
        cloth.setMaterial(dto.getMaterial());
        cloth.setDetail(dto.getDetail());
        cloth.setPrint(dto.getPrint());
        cloth.setStyle(dto.getStyle());
        cloth.setSubstyle(dto.getSubstyle());
        cloth.setImagePath(dto.getImagePath()); 

        return clothingRepository.save(cloth);
    }

    public ClothingDetailsResponseDTO toDTO(ClothingDetails entity) {
        return ClothingDetailsResponseDTO.builder()
                .clothid(entity.getClothid())
                .type(entity.getType())
                .category(entity.getCategory())
                .length(entity.getLength())
                .sleeve(entity.getSleeve())
                .neckline(entity.getNeckline())
                .neck(entity.getNeck())
                .fit(entity.getFit())
                .color(entity.getColor())
                .material(entity.getMaterial())
                .detail(entity.getDetail())
                .print(entity.getPrint())
                .style(entity.getStyle())
                .substyle(entity.getSubstyle())
                .imagePath(entity.getImagePath())
                .build();
    }
}

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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClothingService {

    private final ClothingRepository clothingRepository;
    private final UserRepository userRepository;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8000")
            .build();

    private final String CROPPED_DIR = System.getProperty("user.dir") + File.separator + "uploads/cropped";

    public ClothingDetails saveClothing(MultipartFile file, String username) throws IOException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 1. 원본 이미지 저장 (uploads/original/)
        String baseDir = System.getProperty("user.dir") + File.separator + "uploads";
        String originalDir = baseDir + File.separator + "original";
        File originalFolder = new File(originalDir);
        if (!originalFolder.exists()) originalFolder.mkdirs();

        String originalFilename = file.getOriginalFilename();
        String savedFileName = System.currentTimeMillis() + "_" + originalFilename;
        File destination = new File(originalFolder, savedFileName);
        file.transferTo(destination);

        // 2. FastAPI analyze 호출
        byte[] imageBytes = Files.readAllBytes(destination.toPath());
        ByteArrayResource resource = new ByteArrayResource(imageBytes) {
            @Override
            public String getFilename() {
                return savedFileName;
            }
        };

        Map<String, Object> result = webClient.post()
                .uri("/analyze")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", resource))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (result == null || result.get("의류종류") == null) {
            throw new RuntimeException("FastAPI 응답이 유효하지 않습니다.");
        }

        String type = (String) result.get("의류종류");
        Map<String, Object> attrMap = (Map<String, Object>) ((Map<String, Object>) result.get("속성")).get(type);
        Map<String, Object> styleMap = (Map<String, Object>) result.get("스타일");

        String imagePath = (String) result.get("imageUrl");       // ✅ 원본
String croppedPath = (String) result.get("croppedUrl");   // ✅ 크롭된 이미지 경로

        // 3. ClothingDetails 저장
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
                .imagePath("/uploads/original/" + savedFileName)
                .croppedPath(croppedPath)
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
        cloth.setCroppedPath(dto.getCroppedPath());

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
                .croppedPath(entity.getCroppedPath())
                .build();
    }

    public void updateCroppedImage(Long clothingId, MultipartFile imageFile) throws IOException {
    ClothingDetails clothing = clothingRepository.findById(clothingId)
        .orElseThrow(() -> new IllegalArgumentException("옷 정보 없음"));

    // 기존 이미지 삭제
    if (clothing.getCroppedPath() != null) {
        File oldFile = new File(System.getProperty("user.dir") + clothing.getCroppedPath().replace("/", File.separator));
        if (oldFile.exists()) oldFile.delete();
    }

    // 새 이미지 저장
    File dir = new File(CROPPED_DIR);
    if (!dir.exists()) dir.mkdirs();

    String savedFilename = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
    File destination = new File(dir, savedFilename);
    imageFile.transferTo(destination);

    // DB 경로 갱신
    String imagePath = "/uploads/cropped/" + savedFilename;
    clothing.setCroppedPath(imagePath);
    clothingRepository.save(clothing);
}

    public List<ClothingDetailsResponseDTO> getClothesByUserId(Long userId) {
    List<ClothingDetails> clothes = clothingRepository.findByUserId(userId);
    return clothes.stream()
        .map(clothing -> ClothingDetailsResponseDTO.builder()
            .clothid(clothing.getClothid())
            .type(clothing.getType())
            .category(clothing.getCategory())
            .length(clothing.getLength())
            .sleeve(clothing.getSleeve())
            .neckline(clothing.getNeckline())
            .neck(clothing.getNeck())
            .fit(clothing.getFit())
            .color(clothing.getColor())
            .material(clothing.getMaterial())
            .detail(clothing.getDetail())
            .print(clothing.getPrint())
            .style(clothing.getStyle())
            .substyle(clothing.getSubstyle())
            .imagePath(clothing.getImagePath())
            .croppedPath(clothing.getCroppedPath())
            .build()
        ).collect(Collectors.toList());
}

    public List<ClothingDetails> getRecentClothingByUser(String username) {
    UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));
    return clothingRepository.findByUserOrderByClothidDesc(user);
}
}

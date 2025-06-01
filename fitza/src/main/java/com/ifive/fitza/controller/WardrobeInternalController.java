package com.ifive.fitza.controller;

import com.ifive.fitza.entity.ClothingDetails;
import com.ifive.fitza.repository.ClothingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/internal/wardrobe")
public class WardrobeInternalController {

    private final ClothingRepository clothingRepository;

    // FastAPI → GET /api/internal/wardrobe/{userId}
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserWardrobe(@PathVariable Long userId) {
        List<ClothingDetails> list = clothingRepository.findByUser_Userid(userId);
        List<Map<String, Object>> result = list.stream().map(item -> {
            Map<String, Object> map = new HashMap<>();
            map.put("clothId", item.getClothid()); // ✅ 추가
            map.put("imagePath", item.getImagePath());
            map.put("croppedPath", item.getCroppedPath());
            return map;
    }).toList();
    return ResponseEntity.ok(result);
}

}

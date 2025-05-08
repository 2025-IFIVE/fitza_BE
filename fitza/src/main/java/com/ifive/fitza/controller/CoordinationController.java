package com.ifive.fitza.controller;

import com.ifive.fitza.dto.CoordinationRequestDTO;
import com.ifive.fitza.dto.CoordinationResponseDTO;
import com.ifive.fitza.dto.CoordinationSimpleDTO;
import com.ifive.fitza.jwt.JWTUtil;
import com.ifive.fitza.response.ResponseDTO;
import com.ifive.fitza.code.SuccessCode;
import com.ifive.fitza.service.CoordinationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coordination")
@RequiredArgsConstructor
public class CoordinationController {

    private final CoordinationService coordinationService;
    private final JWTUtil jwtUtil;

    @PostMapping
    public ResponseEntity<ResponseDTO> saveCoordination(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CoordinationRequestDTO dto
    ) {
        String username = jwtUtil.getUsername(authHeader.replace("Bearer ", ""));
        coordinationService.saveCoordination(username, dto);
        return ResponseEntity.ok(new ResponseDTO<>(SuccessCode.SUCCESS_CREATE_COORDINATION, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoordinationResponseDTO> getCoordination(@PathVariable Long id) {
        return ResponseEntity.ok(coordinationService.getCoordination(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> updateCoordination(
            @PathVariable Long id,
            @RequestBody CoordinationRequestDTO dto
    ) {
        coordinationService.updateCoordinationItems(id, dto);
        return ResponseEntity.ok(new ResponseDTO<>(SuccessCode.SUCCESS_UPDATE_COORDINATION, null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deleteCoordination(@PathVariable Long id) {
        coordinationService.deleteCoordination(id);
        return ResponseEntity.ok(new ResponseDTO<>(SuccessCode.SUCCESS_DELETE_COORDINATION, null));
    }

    @GetMapping("/my")
    public ResponseEntity<List<CoordinationSimpleDTO>> getMyCoordinations(
            @RequestHeader("Authorization") String authHeader
    ) {
        String username = jwtUtil.getUsername(authHeader.replace("Bearer ", ""));
        return ResponseEntity.ok(coordinationService.getUserCoordinations(username));
    }
}

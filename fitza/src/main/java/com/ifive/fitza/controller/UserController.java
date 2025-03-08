package com.ifive.fitza.controller;

import com.ifive.fitza.code.ErrorCode;
import com.ifive.fitza.code.SuccessCode;
import com.ifive.fitza.dto.RegisterDTO;
import com.ifive.fitza.dto.UserResponseDTO;
import com.ifive.fitza.jwt.JWTUtil;
import com.ifive.fitza.response.ResponseDTO;
import com.ifive.fitza.service.UserService;
import com.ifive.fitza.util.TokenErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JWTUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> registerUser(@RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return ResponseEntity
                .status(SuccessCode.SUCCESS_REGISTER.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_REGISTER, null));
    }

    @GetMapping("/mypage")
    public ResponseEntity<ResponseDTO> mypage() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserResponseDTO response = userService.mypage(username);
        return ResponseEntity
                .status(SuccessCode.SUCCESS_REGISTER.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_USER, response));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ResponseDTO> reissue(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 헤더에서 refresh키에 담긴 토큰을 꺼냄
        String refreshToken = request.getHeader("refresh");

        if (refreshToken == null) {
            TokenErrorResponse.sendErrorResponse(response, ErrorCode.TOKEN_MISSING);
            return null; // 오류 발생 시 메서드 종료
        }

        try {
            if (jwtUtil.isExpired(refreshToken)) {
                TokenErrorResponse.sendErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
                return null; // 오류 발생 시 메서드 종료
            }
        } catch (ExpiredJwtException e) {
            TokenErrorResponse.sendErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
            return null; // 오류 발생 시 메서드 종료
        }

        String type = jwtUtil.getType(refreshToken);
        if (!"refreshToken".equals(type)) {
            TokenErrorResponse.sendErrorResponse(response, ErrorCode.INVALID_REFRESH_TOKEN);
            return null; // 오류 발생 시 메서드 종료
        }

        String username = jwtUtil.getUsername(refreshToken);

        // 새로운 Access token과 refreshToken 생성 (role 정보는 제외)
        String newAccessToken = jwtUtil.createJwt("accessToken", username, 600000L);
        String newRefreshToken = jwtUtil.createJwt("refreshToken", username, 600000L);

        response.setHeader("accessToken", "Bearer " + newAccessToken);
        response.setHeader("refreshToken", "Bearer " + newRefreshToken);

        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_REISSUE, null));
    }
    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO> logout() {
        userService.logout();
        return ResponseEntity
                .status(SuccessCode.SUCCESS_LOGOUT.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_LOGOUT, null));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDTO> deleteUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.deleteUser(username);
        return ResponseEntity
                .status(SuccessCode.SUCCESS_DELETE_USER.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_DELETE_USER, null));
    }
}
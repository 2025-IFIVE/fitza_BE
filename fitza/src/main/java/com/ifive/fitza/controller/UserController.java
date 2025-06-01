package com.ifive.fitza.controller;

import com.ifive.fitza.code.ErrorCode;
import com.ifive.fitza.code.SuccessCode;
import com.ifive.fitza.dto.RegisterDTO;
import com.ifive.fitza.dto.UserResponseDTO;
import com.ifive.fitza.entity.UserEntity;
import com.ifive.fitza.jwt.JWTUtil;
import com.ifive.fitza.response.ResponseDTO;
import com.ifive.fitza.service.UserService;
import com.ifive.fitza.util.TokenErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
                .status(SuccessCode.SUCCESS_RETRIEVE_USER.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_RETRIEVE_USER, response));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ResponseDTO> reissue(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String refreshToken = request.getHeader("refresh");

        if (refreshToken == null) {
            TokenErrorResponse.sendErrorResponse(response, ErrorCode.TOKEN_MISSING);
            return null;
        }

        try {
            if (jwtUtil.isExpired(refreshToken)) {
                TokenErrorResponse.sendErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
                return null;
            }
        } catch (ExpiredJwtException e) {
            TokenErrorResponse.sendErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
            return null;
        }

        String type = jwtUtil.getType(refreshToken);
        if (!"refreshToken".equals(type)) {
            TokenErrorResponse.sendErrorResponse(response, ErrorCode.INVALID_REFRESH_TOKEN);
            return null;
        }

        String username = jwtUtil.getUsername(refreshToken);

        // 👉 username으로 userId 가져오기
        UserEntity user = userService.getUserEntityByUsername(username);

        String newAccessToken = jwtUtil.createJwt("accessToken", username, user.getUserid(), 600000L);
        String newRefreshToken = jwtUtil.createJwt("refreshToken", username, user.getUserid(), 600000L);

        response.setHeader("accessToken", "Bearer " + newAccessToken);
        response.setHeader("refreshToken", "Bearer " + newRefreshToken);

        return ResponseEntity
                .status(HttpStatus.OK)
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

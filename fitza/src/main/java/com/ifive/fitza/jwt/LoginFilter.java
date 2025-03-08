package com.ifive.fitza.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifive.fitza.code.ErrorCode;
import com.ifive.fitza.code.SuccessCode;
import com.ifive.fitza.dto.CustomUserDetails;
import com.ifive.fitza.dto.LoginRequest;
import com.ifive.fitza.response.ErrorResponseDTO;
import com.ifive.fitza.response.ResponseDTO;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        // JSON 데이터로부터 LoginRequest 객체를 생성
        LoginRequest loginRequest;
        try {
            loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
        } catch (IOException e) {
            throw new RuntimeException("Invalid login request", e);
        }

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        System.out.println(username);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String username = customUserDetails.getUsername();

        // JWT 생성
        String accessToken = jwtUtil.createJwt("accessToken", username, 86400000L);
        String refreshToken = jwtUtil.createJwt("refreshToken", username, 86400000L);

        response.addHeader("accessToken", "Bearer " + accessToken);
        response.addHeader("refreshToken", "Bearer " + refreshToken);

        ResponseDTO<?> responseDTO = new ResponseDTO<>(SuccessCode.SUCCESS_LOGIN, null);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String jsonResponse = objectMapper.writeValueAsString(responseDTO);
        response.getWriter().write(jsonResponse);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {

        response.setStatus(401);

        ErrorResponseDTO responseDTO = new ErrorResponseDTO(ErrorCode.USER_NOT_FOUND);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String jsonResponse = objectMapper.writeValueAsString(responseDTO);
        response.getWriter().write(jsonResponse);
    }
}

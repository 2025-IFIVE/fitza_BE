package com.ifive.fitza.jwt;

import com.ifive.fitza.code.ErrorCode;
import com.ifive.fitza.dto.CustomUserDetails;
import com.ifive.fitza.entity.UserEntity;
import com.ifive.fitza.util.TokenErrorResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Request에서 Authorization 헤더를 찾음
        String authorization = request.getHeader("Authorization");

        // Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return; // 조건이 해당되면 메소드 종료
        }

        String token = authorization.split(" ")[1];

        // 토큰 소멸 시간 검증
        if (jwtUtil.isExpired(token)) {
            TokenErrorResponse.sendErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
            return; // 조건이 해당되면 메소드 종료
        }

        // Access token을 입력했는지 확인
        String type = jwtUtil.getType(token);
        if (!"accessToken".equals(type)) {
            TokenErrorResponse.sendErrorResponse(response, ErrorCode.INVALID_ACCESS_TOKEN);
            return; // 조건이 해당되면 메소드 종료
        }

        String username = jwtUtil.getUsername(token);

        // 임시로 사용자 정보를 만듦 (DB를 통해 실제 사용자를 로드할 수도 있음)
        UserEntity user = UserEntity.builder()
                .username(username)
                .password("temppassword") // 비밀번호는 실제 검증에 사용되지 않음
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}

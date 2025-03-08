package com.ifive.fitza.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifive.fitza.code.ErrorCode;
import com.ifive.fitza.response.ErrorResponseDTO;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
public class TokenErrorResponse {

    public static void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException, IOException {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(errorCode);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.setStatus(HttpStatus.BAD_REQUEST.value());

        PrintWriter writer = response.getWriter();
        writer.print(jsonResponse);
        writer.flush();
        writer.close();
    }
}
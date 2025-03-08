package com.ifive.fitza.service;

import com.ifive.fitza.dto.RegisterDTO;
import com.ifive.fitza.dto.UserResponseDTO;
import com.ifive.fitza.entity.UserEntity;
import com.ifive.fitza.exception.DuplicateUsernameException;
import com.ifive.fitza.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void register(RegisterDTO registerDTO) {
        String username = registerDTO.getUsername();
        String password = registerDTO.getPassword();
        String name = registerDTO.getName();
        String nickname = registerDTO.getNickname();
        String phone = registerDTO.getPhone();
        

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException("중복된 아이디가 존재합니다.");
        }

        UserEntity user = UserEntity.builder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(password))
                .name(name)
                .nickname(nickname)
                .phone(phone)
                .build();

        userRepository.save(user);

    }

    public UserResponseDTO mypage(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));
        return new UserResponseDTO(user.getUsername(), user.getName(), user.getNickname(), user.getPhone());
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }

    public void deleteUser(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));
        userRepository.delete(user);
    }

}
package com.ifive.fitza.service;

import com.ifive.fitza.dto.*;
import com.ifive.fitza.entity.*;
import com.ifive.fitza.repository.CalendarCoordinationRepository;
import com.ifive.fitza.repository.ClothingRepository;
import com.ifive.fitza.repository.CoordinationItemRepository;
import com.ifive.fitza.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoordinationService {

    private final UserRepository userRepository;
    private final CalendarCoordinationRepository calendarRepo;
    private final CoordinationItemRepository itemRepo;
    private final ClothingRepository clothingRepository;


    public CalendarCoordination saveCoordination(String username, CoordinationRequestDTO dto) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        CalendarCoordination calendar = CalendarCoordination.builder()
                .title(dto.getTitle())
                .date(dto.getDate())
                .weather(dto.getWeather())
                .user(user)
                .build();
        calendarRepo.save(calendar);

        List<CoordinationItem> items = dto.getItems().stream().map(itemDTO -> {
            ClothingDetails clothing = clothingRepository.findById(itemDTO.getClothId())
                    .orElseThrow(() -> new RuntimeException("옷 ID 오류"));
            return CoordinationItem.builder()
                    .calendar(calendar)
                    .clothing(clothing)
                    .x(itemDTO.getX())
                    .y(itemDTO.getY())
                    .size(itemDTO.getSize())
                    .build();
        }).toList();

        itemRepo.saveAll(items);
        calendar.setItems(items);
        return calendar;
    }

    //캘린더 코디 단건 조회 (imagePath 포함)
    public CoordinationResponseDTO getCoordination(Long calendarId) {
        CalendarCoordination calendar = calendarRepo.findById(calendarId)
                .orElseThrow(() -> new RuntimeException("코디 없음"));

        List<CoordinationItemDTO> items = itemRepo.findByCalendar(calendar).stream()
                .map(item -> {
                    ClothingDetails clothing = item.getClothing();
                    return CoordinationItemDTO.builder()
                            .clothId(clothing.getClothid())
                            .x(item.getX())
                            .y(item.getY())
                            .size(item.getSize())
                            .imagePath(clothing.getImagePath())
                            .croppedPath(clothing.getCroppedPath())
                            .build();
                })
                .toList();

        return CoordinationResponseDTO.builder()
                .calendarId(calendar.getCalendarId())
                .title(calendar.getTitle())
                .date(calendar.getDate())
                .weather(calendar.getWeather())
                .items(items)
                .build();
    }

    // ✅ 캘린더 코디 삭제
    @Transactional
    public void deleteCoordination(Long calendarId) {
        CalendarCoordination calendar = calendarRepo.findById(calendarId)
                .orElseThrow(() -> new RuntimeException("코디 없음"));
        itemRepo.deleteByCalendar(calendar);
        calendarRepo.delete(calendar);
    }

    // ✅ 아이템 수정 (덮어쓰기)
    @Transactional
    public void updateCoordinationItems(Long calendarId, CoordinationRequestDTO dto) {
        CalendarCoordination calendar = calendarRepo.findById(calendarId)
                .orElseThrow(() -> new RuntimeException("코디 없음"));

        calendar.setTitle(dto.getTitle());
        calendar.setDate(dto.getDate());
        calendar.setWeather(dto.getWeather());
        
        itemRepo.deleteByCalendar(calendar); // 전체 삭제 후 덮어쓰기

        List<CoordinationItem> newItems = dto.getItems().stream().map(itemDTO -> {
            ClothingDetails clothing = clothingRepository.findById(itemDTO.getClothId())
                    .orElseThrow(() -> new RuntimeException("옷 ID 오류"));
            return CoordinationItem.builder()
                    .calendar(calendar)
                    .clothing(clothing)
                    .x(itemDTO.getX())
                    .y(itemDTO.getY())
                    .size(itemDTO.getSize())
                    .build();
        }).toList();

        itemRepo.saveAll(newItems);
    }

    // ✅ 내 코디 전체 목록 조회
    public List<CoordinationSimpleDTO> getUserCoordinations(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        return calendarRepo.findAll().stream()
                .filter(c -> c.getUser().equals(user))
                .map(c -> CoordinationSimpleDTO.builder()
                        .calendarId(c.getCalendarId())
                        .title(c.getTitle())
                        .date(c.getDate())
                        .weather(c.getWeather())
                        .build())
                .toList();
    }
}

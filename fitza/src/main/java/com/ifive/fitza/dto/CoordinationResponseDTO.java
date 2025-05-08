package com.ifive.fitza.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class CoordinationResponseDTO {
    private Long calendarId;
    private String title;
    private LocalDate date;
    private String weather;
    private List<CoordinationItemDTO> items;
}

package com.ifive.fitza.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class CoordinationSimpleDTO {
    private Long calendarId;
    private String title;
    private LocalDate date;
    private String weather;
}

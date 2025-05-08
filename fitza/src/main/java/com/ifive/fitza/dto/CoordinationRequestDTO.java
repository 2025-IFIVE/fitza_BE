package com.ifive.fitza.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class CoordinationRequestDTO {
    private String title;
    private LocalDate date;
    private String weather;
    private List<CoordinationItemDTO> items;
}

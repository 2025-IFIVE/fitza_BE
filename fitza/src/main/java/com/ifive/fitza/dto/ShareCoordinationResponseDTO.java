package com.ifive.fitza.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class ShareCoordinationResponseDTO {
    private Long shareId;
    private String title;
    private LocalDate date;
    private String weather;
    private String ownerNickname;
    private List<CoordinationItemDTO> items;
}

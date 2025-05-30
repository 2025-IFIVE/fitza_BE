package com.ifive.fitza.dto;

import lombok.Data;
import java.util.List;

@Data
public class MatchResultDTO {
    private List<String> matchedImages;
    private List<String> labels;
    private List<Double> scores;
}

package com.app.miliwili.src.main.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class PlanMainData {
    private final Long planId;
    private final String title;
}
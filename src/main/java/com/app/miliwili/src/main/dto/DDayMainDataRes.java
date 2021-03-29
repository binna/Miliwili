package com.app.miliwili.src.main.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class DDayMainDataRes {
    private final Long ddayId;
    private final String date;
    private final String title;
}
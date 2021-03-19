package com.app.miliwili.src.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Builder
@Getter
public class GetOrdinaryLeaveRes {
    private final List<Map<String, String>> hi;
}
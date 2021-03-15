package com.app.miliwili.src.user.dto;

import com.app.miliwili.src.user.models.AbnormalPromotionState;
import com.app.miliwili.src.user.models.NormalPromotionState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class PatchUserRes {
    private final Long userId;
    private final String name;
    private final String serveType;
    private final Integer stateIdx;
    private final String startDate;
    private final String endDate;
    private final String socialType;
    private final String goal;
    private final String profileImg;
    private final NormalPromotionState normalPromotionState;
    private final AbnormalPromotionState abnormalPromotionState;
}
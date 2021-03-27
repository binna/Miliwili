package com.app.miliwili.src.main.dto;

import lombok.*;

import javax.annotation.Nonnull;
import java.time.LocalDate;

@AllArgsConstructor
@Builder
@Data
public class UserCalendarMainData {
    private final String name;
    private final String profileImg;
    private final LocalDate birthday;
    private final Integer stateIdx;
    private final String serveType;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalDate strPrivate;
    private final LocalDate strCorporal;
    private final LocalDate strSergeant;
    private final Integer hobong;
    private final Integer normalPromotionStateIdx;
    private final LocalDate proDate;
    private final String goal;
//    private final Integer vacationTotalDays;
//    private final Integer vacationUseDays;
//    private final Integer vacationPlanUseDays;

}
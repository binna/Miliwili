package com.app.miliwili.src.main;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.config.BaseResponseStatus;
import com.app.miliwili.src.main.model.GetEndDayRes;
import com.app.miliwili.src.user.UserProvider;
import com.app.miliwili.src.user.models.GetAbnormalUserEndDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static com.app.miliwili.config.BaseResponseStatus.*;


@RequiredArgsConstructor
@Service
public class MainProvider {
    private final UserProvider userProvider;

    /**
     * 전역일 계산
     * @return GetEndDayRes
     * @Auther vivi
     */
    public GetEndDayRes retrieveUserEndDay(long id) throws BaseException{
        //for return
        GetEndDayRes getEndDayRes;


        //일반병사인지 아닌지 검사
        int userStateIdx = 0;
        try{
            userStateIdx = userProvider.retrieveUserstateIdx(id);
        }catch (Exception e){
            e.printStackTrace();
            throw new BaseException(FAILED_TO_GET_USER_STATE_IDX);
        }


//        //일반병사일 경우
//        if(userStateIdx == 1){
//
//        }
//        //부사관, 준사관, 장교일 경우
//        else{

            GetAbnormalUserEndDate abnormalEnd;

            try{
                abnormalEnd = userProvider.retrieveAbnormalUserEndDate(id);
            }catch (Exception e){
                e.printStackTrace();
                throw new BaseException(FAILED_TO_GET_ABNORMAL_END);
            }
            getEndDayRes = changeAbnormalToRes(abnormalEnd);


//        }

        return getEndDayRes;
    }


    /**
     * GetAbnormalUserEndDate --> GetEndDayRes
     */
    public GetEndDayRes changeAbnormalToRes(GetAbnormalUserEndDate abnormal){
        //현재 날짜
        LocalDate currentDate = LocalDate.now();
        //전역일
        LocalDate abEndDate = abnormal.getEndDate();


        long id = abnormal.getId();
        String profileImg = abnormal.getProfileImg();
        String name = abnormal.getName();

        //전역일 D-day
        Period endDdayPeroid = currentDate.until(abEndDate);
        int endday = endDdayPeroid.getDays();
        String endDday = "D - "+endday;

        String abnormalProStr = "진급심사일";

        //진급 심사일 D-day
        Period proDdayPeriod = currentDate.until(abEndDate);
        int proDday = proDdayPeriod.getDays();
        String abnormalProDday = "D - "+proDday;

        //전역까지 퍼센트
        // 100 - ((남은 Dday / 총 복무 일수 ) *100)
        Period totalDayPeriod =abnormal.getStartDate().until(abEndDate);
        int totalDay = totalDayPeriod.getDays();
        //반올림
        int endRate = (int)Math.ceil(100.0 - (((double)endday / (double)totalDay) * 100.0));


        //22-01-01 format
        String endDateStr = abEndDate.toString();
        String endDate = endDateStr.substring(2,4)+"."+endDateStr.substring(5,7)+"."+endDateStr.substring(8,10);

        String goal = abnormal.getGoal();

        GetEndDayRes getEndDayRes = GetEndDayRes.builder()
                .id(id)
                .name(name)
                .endDday(endDday)
                .abnormalProStr(abnormalProStr)
                .abnormalProDday(abnormalProDday)
                .endRate(endRate)
                .endDate(endDate)
                .goal(goal)
                .build();

        return getEndDayRes;


    }
}

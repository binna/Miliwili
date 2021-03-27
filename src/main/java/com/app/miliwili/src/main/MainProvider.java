package com.app.miliwili.src.main;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.main.dto.GetUserCalendarMainRes;
import com.app.miliwili.src.main.dto.UserCalendarMainData;
import com.app.miliwili.src.main.model.GetEndDayRes;
import com.app.miliwili.src.user.UserProvider;
import com.app.miliwili.src.user.UserSelectRepository;
import com.app.miliwili.src.user.dto.GetAbnormalUserEndDate;
import com.app.miliwili.src.user.models.AbnormalPromotionState;
import com.app.miliwili.src.user.models.NormalPromotionState;
import com.app.miliwili.src.user.models.UserInfo;
import com.app.miliwili.src.user.models.Vacation;
import com.app.miliwili.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.eclipse.jdt.internal.compiler.ast.NormalAnnotation;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import static com.app.miliwili.config.BaseResponseStatus.*;


@RequiredArgsConstructor
@Service
public class MainProvider {
    public final UserSelectRepository userSelectRepository;
    private final JwtService jwtService;

    /**
     * 메인 페이지 UserCalendarMainData 가져오기
     */
    // TODO
    public UserCalendarMainData retrieveMainDataListByUserId(Long userId) throws BaseException{
        UserCalendarMainData mainData;
        try{
            mainData = userSelectRepository.findtest(userId);
            if (mainData == null)
                throw new BaseException(NOT_FOUND_MAIN_INFO);
        }catch (Exception e){
            e.printStackTrace();
            throw new  BaseException(FAILED_TO_GET_USER_MAIN_INFO);
        }
        return mainData;
    }

    /**
     * main
     */
    public UserCalendarMainData retrieveUserMainById() throws BaseException{
        Long userId = jwtService.getUserId();
        UserCalendarMainData userMainInfo;
        try{
            UserCalendarMainData mainResList = retrieveMainDataListByUserId(userId);
            userMainInfo = mainResList;
        }catch (Exception e){
            e.printStackTrace();
            throw new BaseException(FAILED_TO_GET_USER_MAIN_LIST);
        }

        if(userMainInfo == null){
            System.out.println("???????");
        }



        return userMainInfo;


    }

    /**
     * Converter
     * UserCalendarMainData -> GetUserCalendarMainRes
     */
    public GetUserCalendarMainRes changeMainDataToRes(UserCalendarMainData mainData){
        GetUserCalendarMainRes mainRes = GetUserCalendarMainRes.builder()
                .profileImg(mainData.getProfileImg())
                .birthday((mainData.getBirthday() == null)? "": mainData.getBirthday().toString())
                .stateIdx(mainData.getStateIdx())
                .serveType(mainData.getServeType())
                .endDate(mainData.getEndDate().toString())
                .strPrivate(mainData.getStrPrivate().toString())
                .strCorporal(mainData.getStrCorporal().toString())
                .hobong(mainData.getHobong())
                .normalPromotionStateIdx(mainData.getNormalPromotionStateIdx())
                .proDate(mainData.getProDate().toString())
                .goal(mainData.getGoal())
                .build();

     return mainRes;
    }
}

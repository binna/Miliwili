package com.app.miliwili.src.user;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.user.models.GetAbnormalUserEndDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
public class UserProvider {
    private final UserSelectRepository userSelectRepository;





    /**
     * 로그인시 존재하는 구글 아이디(socialId) 검사
     */
    public List<Long> isGoogleUser(String gSocialId) throws BaseException{
        List<Long> userList = null;

        try{
            userList = userSelectRepository.findUsersIdByGoogleId(gSocialId);
        }catch (Exception e){
            e.printStackTrace();
            throw new BaseException(FAILED_TO_GET_USER);
        }


        return userList;
    }


    /**
     * stateIdx --> 일반병사인지 아닌지 확인
     * @Auther vivi
     */
    public int retrieveUserstateIdx(long id) throws BaseException{
        int userStateIdx = 0;
        try{
            List<Integer> userStateList = userSelectRepository.findUserStateIdxByUserId(id);
            if(userStateList.size() == 0)
                throw new BaseException(FAILED_TO_GET_USER);

            userStateIdx = userStateList.get(0);

        }catch (Exception e){
            e.printStackTrace();
            throw new BaseException(FAILED_TO_GET_USER);
        }

        return userStateIdx;
    }




    /**
     * for 전역일 계산기 (MainProvider)
     * [ 부사관, 준사관, 장교 ]들을 위한
     * @return GetAbnormalUserEndDate
     * @Auther vivi
     */
    public GetAbnormalUserEndDate retrieveAbnormalUserEndDate(long id) throws BaseException{
        GetAbnormalUserEndDate abnormalUserEndDate;
        try{
            List<GetAbnormalUserEndDate> userList=userSelectRepository.findEndDateInfoByUserId(id);

            if(userList.size() ==0)
                throw new BaseException(FAILED_TO_GET_USER);

            abnormalUserEndDate = userList.get(0);

        }catch (Exception e){
            e.printStackTrace();
            throw new BaseException(FAILED_TO_GET_USER);
        }

        return abnormalUserEndDate;
    }
}
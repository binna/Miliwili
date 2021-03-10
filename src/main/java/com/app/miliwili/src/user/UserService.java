package com.app.miliwili.src.user;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.config.BaseResponseStatus;
import com.app.miliwili.src.user.models.*;
import com.app.miliwili.utils.JwtService;
import com.app.miliwili.utils.ValidationLength;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserProvider userProvider;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AbnormalPromotionStateRepository abnormalPromotionStateRepository;
    private final NormalPromotionStateRepository normalPromotionStateRepository;


    /**
     * [로그인 - 구글 ]
     */
    public PostLoginRes createGoogleJwtToken(String googleSocialId) throws BaseException {

        String socialId = googleSocialId.substring(1,googleSocialId.length()-1);

        PostLoginRes postLoginRes;
        String jwtToken="";
        boolean isMemeber = true;
        long id = 0;

        List<Long> userIdList;
        try{
            userIdList = userProvider.isGoogleUser(socialId);
        }catch (Exception e){
            throw new BaseException(FAILED_TO_GET_USER);
        }


        //id 추출
        if(userIdList == null || userIdList.size() == 0){
            isMemeber = false;
        }else{
            id = userIdList.get(0);
        }


        if (isMemeber == true) { //회원일 떄
            jwtToken = jwtService.createJwt(id);
        }else{ //회원이 아닐 때
            jwtToken = "";
        }


        postLoginRes = PostLoginRes.builder()
                .isMember(isMemeber)
                .jwt(jwtToken)
                .build();
        return postLoginRes;

    }


    /**
     * [ 회원가입 - 구글 ]
     *
     */
    public PostSignUpRes createGoogleUser(PostSignUpReq param, String gSocialId) throws BaseException{

        String name = param.getName();
        String serveType = param.getServeType();
        int stateIdx = param.getStateIdx();
        String socialType = "G";

        //""제거
        String socialId = gSocialId.substring(1,gSocialId.length()-1);

        String goal = param.getGoal();
        String profileImg = "https://miliwili-storage.s3.ap-northeast-2.amazonaws.com/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2021-03-10+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+7.21.24.png";
        LocalDate startDate = LocalDate.parse(param.getStartDate(), DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate.parse(param.getEndDate(), DateTimeFormatter.ISO_DATE);


        //회원 정보 데이터 생성
        User newUser = User.builder()
                .name(name)
                .serveType(serveType)
                .stateIdx(stateIdx)
                .socialType(socialType)
                .socialId(socialId)
                .goal(goal)
                .profileImg(profileImg)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        try {
            userRepository.save(newUser);
        }catch (Exception e){
            e.printStackTrace();
            throw new BaseException(FAILED_TO_SIGNUP_USER);
        }


        //일반병사 데이터 생성
        if(param.getStateIdx() == 1){
            NormalPromotionState normalPromotionState = NormalPromotionState.builder()
                    .firstDate(LocalDate.parse(param.getStrPrivate(), DateTimeFormatter.ISO_DATE))
                    .secondDate(LocalDate.parse(param.getStrCorporal(), DateTimeFormatter.ISO_DATE))
                    .thirdDate(LocalDate.parse(param.getStrSergeant(), DateTimeFormatter.ISO_DATE))
                    .user(newUser)
                    .build();

            try{
                normalPromotionStateRepository.save(normalPromotionState);
            }catch (Exception e){
                e.printStackTrace();
                throw new BaseException(FAILED_TO_SIGNUP_USER_NORMAL_STATE);
            }

        //부사관, 장교, 준사관 데이터 생성
        }else{
            String proDate="";
            AbnormalPromotionState abnormalPromotionState;

            if(!ValidationLength.isFullString(param.getProDate())){
                //처음에 진급 심사일을 설정하지 않았을 때
                abnormalPromotionState = AbnormalPromotionState.builder()
                        .proDate(null)
                        .user(newUser)
                        .build();
            }else{
                abnormalPromotionState = AbnormalPromotionState.builder()
                        .proDate(LocalDate.parse(param.getProDate(), DateTimeFormatter.ISO_DATE))
                        .user(newUser)
                        .build();
            }


            try{
                abnormalPromotionStateRepository.save(abnormalPromotionState);
            }catch (Exception e){
                e.printStackTrace();
                throw new BaseException(FAILED_TO_SIGNUP_USER_ABNORMAL_STATE);
            }

        }


        PostSignUpRes postSignUpRes = PostSignUpRes.builder()
                .userId(newUser.getId())
                .jwt(jwtService.createJwt(newUser.getId()))
                .build();



        return postSignUpRes;



    }
}
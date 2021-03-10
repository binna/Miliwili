package com.app.miliwili.src.user;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.config.BaseResponse;
import com.app.miliwili.src.user.models.*;
import com.app.miliwili.utils.GoogleService;
import com.app.miliwili.utils.JwtService;
import com.app.miliwili.utils.ValidationLength;
import com.app.miliwili.utils.ValidationRegex;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/app")
public class UserController {
    private final JwtService jwtService;
    private final GoogleService googleService;
    private final UserService userService;
    private final UserProvider userProvider;


    /**
     * JWT 검증 API
     * [GET]
     * @RequestHeader jwt
     * @return BaseResponse<Void>
     * @Auther shine
     */
    @GetMapping("/users/jwt")
    public BaseResponse<Void> jwt(@RequestHeader(value = "jwt") String jwt) {
        try {
            Long userId = jwtService.getUserId();
            // TODO 중복된 회원조회 필요 -> 회원가입 API 만든 후 만들 예정
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 로그인 --> 구글
     * [POST]
     * @RequestHeader token(google accessToken)
     * @return BaseResponse<PostLoginRes>
     * @Auther vivi
     */
    @ResponseBody
    @PostMapping("/users/login-google")
    public BaseResponse<PostLoginRes> postLoginGoogle(@RequestHeader("X-ACCESS-TOKEN") String token){

        try{
            String socialId = googleService.userIdFromGoogle(token);

            PostLoginRes returnRes = userService.createGoogleJwtToken(socialId);
            return new BaseResponse<>(SUCCESS,returnRes);
        }catch(BaseException e){
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 회원가입 --> 구글
     * @RequestHeader token(google accessToken)
     * @return BaseResponse<PostLoginRes>
     * @Auther vivi
     */
    @ResponseBody
    @PostMapping("/users/google")
    public BaseResponse<PostSignUpRes> postSignUpGoogle(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                        @RequestBody PostSignUpReq param){
        //이름 check
        if(!ValidationLength.isFullString(param.getName())){
            return new BaseResponse<>(EMPTY_NAME);

        }

        //복무형태 check  (일반병사, 부사관, 준사관, 장교)
        if(param.getStateIdx() > 5 || param.getStateIdx() < 1){
            return new BaseResponse<>(INVALID_STATEIDX);
        }

        //하위 복무형태 check(육군, 해군)
        if(!ValidationLength.isFullString(param.getServeType())){
            return new BaseResponse<>(EMPTY_SERVE_TYPE);
        }


        //입대일 check
        if(!ValidationLength.isFullString(param.getStartDate())) {
            return new BaseResponse<>(EMPTY_START_DATE);
        }
        if(!ValidationRegex.isRegexDate(param.getStartDate())){
            return new BaseResponse<>(INVALID_START_DATE);
        }


        //전역일 check
        if(!ValidationLength.isFullString(param.getEndDate())) {
            return new BaseResponse<>(EMPTY_END_DATE);
        }
        if(!ValidationRegex.isRegexDate(param.getEndDate())){
            return new BaseResponse<>(INVALID_END_DATE);
        }



        if(param.getStateIdx() == 1){   //일반병사일 떄
            if(!ValidationLength.isFullString(param.getStrPrivate())) {
                return new BaseResponse<>(EMPTY_FIRST_DATE);
            }
            if(!ValidationRegex.isRegexDate(param.getStrPrivate())){
                return new BaseResponse<>(INVALID_END_DATE);
            }

            if(!ValidationLength.isFullString(param.getStrCorporal())) {
                return new BaseResponse<>(EMPTY_SECOND_DATE);
            }
            if(!ValidationRegex.isRegexDate(param.getStrCorporal())){
                return new BaseResponse<>(INVALID_SECOND_DATE);
            }

            if(!ValidationLength.isFullString(param.getStrSergeant())) {
                return new BaseResponse<>(EMPTY_THIRD_DATE);
            }
            if(!ValidationRegex.isRegexDate(param.getStrSergeant())){
                return new BaseResponse<>(INVALID_THIRD_DATE);
            }

        }else{
            if(ValidationLength.isFullString(param.getProDate())) {
                if (!ValidationRegex.isRegexDate(param.getProDate())) {
                    return new BaseResponse<>(INVALID_PRO_DATE);
                }
            }
        }


        try{
            String socialId = googleService.userIdFromGoogle(token);
            PostSignUpRes postSignUpRes = userService.createGoogleUser(param,socialId);
            return new BaseResponse<>(SUCCESS, postSignUpRes);
        }catch(BaseException e){
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }

    }
}
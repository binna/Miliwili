package com.app.miliwili.src.user;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.config.BaseResponse;
import com.app.miliwili.src.user.models.PostLoginRes;
import com.app.miliwili.src.user.models.PostSignUpReq;
import com.app.miliwili.src.user.models.PostSignUpRes;
import com.app.miliwili.utils.JwtService;
import com.app.miliwili.utils.SNSLogin;
import com.app.miliwili.utils.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/app")
public class UserController {
    private final JwtService jwtService;
    private final SNSLogin SNSLogin;
    private final UserService userService;
    private final UserProvider userProvider;

    /**
     * JWT 검증 API
     * [GET] /app/users/jwt
     *
     * @return BaseResponse<Void>
     * @RequestHeader X-ACCESS-TOKEN
     * @Auther shine
     */
    @GetMapping("/users/jwt")
    public BaseResponse<Void> jwt(@RequestHeader("X-ACCESS-TOKEN") String jwt) {
        try {
            Long userId = jwtService.getUserId();
            userProvider.retrieveUserByIdAndStatusY(userId);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 로그인 --> 구글
     * [POST]
     *
     * @return BaseResponse<PostLoginRes>
     * @RequestHeader token(google accessToken)
     * @Auther vivi
     */
    @ResponseBody
    @PostMapping("/users/login-google")
    public BaseResponse<PostLoginRes> postLoginGoogle(@RequestHeader("X-ACCESS-TOKEN") String token) {

        String socialId = "";
        try {
            socialId = SNSLogin.userIdFromGoogle(token);
        } catch (BaseException e) {
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }

        try {
            PostLoginRes returnRes = userService.createGoogleJwtToken(socialId);
            return new BaseResponse<>(SUCCESS, returnRes);
        } catch (BaseException e) {
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 카카오 로그인
     * [POST] /app/users/login-kakao
     *
     * @return BaseResponse<PostLoginRes>
     * @RequestHeader X-ACCESS-TOKEN
     * @Auther shine
     */
    @ResponseBody
    @PostMapping("/users/login-kakao")
    public BaseResponse<PostLoginRes> postLoginKakao(@RequestHeader("X-ACCESS-TOKEN") String token) {
        try {
            PostLoginRes postLoginRes = userService.loginUser(SNSLogin.userIdFromKakao(token));
            return new BaseResponse<>(SUCCESS, postLoginRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 회원가입
     * [POST] /app/users
     *
     * @return BaseResponse<Void>
     * @RequestHeader X-ACCESS-TOKEN
     * @Auther shine
     */
    @ResponseBody
    @PostMapping("/users")
    public BaseResponse<PostSignUpRes> postSignUpKakao(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                       @RequestBody(required = false) PostSignUpReq parameters) {
        if (Objects.isNull(parameters.getName()) || parameters.getName().length() == 0) {
            return new BaseResponse<>(EMPTY_NAME);
        }
        if (Objects.isNull(parameters.getStateIdx())) {
            return new BaseResponse<>(EMPTY_STATEIDX);
        }
        if (1 > parameters.getStateIdx() || parameters.getStateIdx() > 4) {
            return new BaseResponse<>(INVALID_STATEIDX);
        }
        if(Objects.isNull(parameters.getServeType()) || parameters.getServeType().length() == 0) {
            return new BaseResponse<>(EMPTY_SERVE_TYPE);
        }
        if (Objects.isNull(parameters.getStartDate()) || parameters.getStartDate().length() == 0) {
            return new BaseResponse<>(EMPTY_START_DATE);
        }
        if (!Validation.isRegexDate((parameters.getStartDate()))) {
            return new BaseResponse<>(INVALID_START_DATE);
        }
        if (Objects.isNull(parameters.getEndDate()) || parameters.getEndDate().length() == 0) {
            return new BaseResponse<>(EMPTY_END_DATE);
        }
        if (!Validation.isRegexDate((parameters.getEndDate()))) {
            return new BaseResponse<>(INVALID_END_DATE);
        }

        LocalDate startDate = LocalDate.parse(parameters.getStartDate(), DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate.parse(parameters.getEndDate(), DateTimeFormatter.ISO_DATE);
        if (startDate.isAfter(endDate)) {
            return new BaseResponse<>(FASTER_THAN_START_DATE);
        }

        if (parameters.getStateIdx() == 1) {
            if (Objects.isNull(parameters.getStrPrivate()) || parameters.getStrPrivate().length() == 0) {
                return new BaseResponse<>(EMPTY_FIRST_DATE);
            }
            if (!Validation.isRegexDate((parameters.getStrPrivate()))) {
                return new BaseResponse<>(INVALID_FIRST_DATE);
            }
            LocalDate firstDate = LocalDate.parse(parameters.getStrPrivate(), DateTimeFormatter.ISO_DATE);
            if (startDate.isAfter(firstDate)) {
                return new BaseResponse<>(FASTER_THAN_FIRST_DATE);
            }

            if (Objects.isNull(parameters.getStrCorporal()) || parameters.getStrCorporal().length() == 0) {
                return new BaseResponse<>(EMPTY_SECOND_DATE);
            }
            if (!Validation.isRegexDate((parameters.getStrCorporal()))) {
                return new BaseResponse<>(INVALID_SECOND_DATE);
            }
            LocalDate secondDate = LocalDate.parse(parameters.getStrCorporal(), DateTimeFormatter.ISO_DATE);
            if (firstDate.isAfter(secondDate)) {
                return new BaseResponse<>(FASTER_THAN_SECOND_DATE);
            }

            if (Objects.isNull(parameters.getStrSergeant()) || parameters.getStrSergeant().length() == 0) {
                return new BaseResponse<>(EMPTY_THIRD_DATE);
            }
            LocalDate thirdDate = LocalDate.parse(parameters.getStrSergeant(), DateTimeFormatter.ISO_DATE);
            if (!Validation.isRegexDate((parameters.getStrSergeant()))) {
                return new BaseResponse<>(INVALID_THIRD_DATE);
            }
            if (secondDate.isAfter(thirdDate)) {
                return new BaseResponse<>(FASTER_THAN_THIRD_DATE);
            }
            if (thirdDate.isAfter(endDate)) {
                return new BaseResponse<>(FASTER_THAN_END_DATE);
            }
        } else {
            if (Objects.isNull(parameters.getStrPrivate()) || Objects.isNull(parameters.getStrCorporal()) || Objects.isNull(parameters.getStrSergeant())) {
                if (Objects.isNull(parameters.getProDate()) || parameters.getProDate().length() == 0) {
                    return new BaseResponse<>(EMPTY_PRO_DATE);
                }
                if (!Validation.isRegexDate((parameters.getProDate()))) {
                    return new BaseResponse<>(INVALID_PRO_DATE);
                }
                LocalDate proDate = LocalDate.parse(parameters.getProDate(), DateTimeFormatter.ISO_DATE);
                if (startDate.isAfter(proDate)) {
                    return new BaseResponse<>(FASTER_THAN_PRO_DATE);
                }
            }
        }

        try {
            PostSignUpRes postSignUpRes = userService.createUser(parameters, token);
            return new BaseResponse<>(SUCCESS, postSignUpRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 테스트용 jwt 생성, 나중에 삭제할거
     * [post] /api/jwt
     */
    @PostMapping("/jwt/{id}")
    public String postJWT(@PathVariable Long id) {
        return jwtService.createJwt(id);
    }
}
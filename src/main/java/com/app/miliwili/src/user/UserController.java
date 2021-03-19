package com.app.miliwili.src.user;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.config.BaseResponse;
import com.app.miliwili.src.user.dto.*;
import com.app.miliwili.utils.JwtService;
import com.app.miliwili.utils.SNSLogin;
import com.app.miliwili.utils.Validation;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@RestController
@EnableSwagger2
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
    @ApiOperation(value = "jwt 검증", notes = "X-ACCESS-TOKEN jwt 필요")
    @GetMapping("/users/jwt")
    public BaseResponse<Void> jwt(@RequestHeader("X-ACCESS-TOKEN") String token) {
        try {
            Long userId = jwtService.getUserId();
            userProvider.retrieveUserByIdAndStatusY(userId);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 구글 로그인 API
     * [POST] /app/users/login-google
     *
     * @return BaseResponse<PostLoginRes>
     * @RequestHeader token(google accessToken)
     * @Auther vivi
     */
    @ApiOperation(value = "구글 로그인", notes = "X-ACCESS-TOKEN 구글 엑세스 토큰 필요")
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
     * 카카오 로그인 API
     * [POST] /app/users/login-kakao
     *
     * @return BaseResponse<PostLoginRes>
     * @RequestHeader X-ACCESS-TOKEN
     * @Auther shine
     */
    @ApiOperation(value = "카카오 로그인", notes = "X-ACCESS-TOKEN 카카오 엑세스 토큰 필요")
    @ResponseBody
    @PostMapping("/users/login-kakao")
    public BaseResponse<PostLoginRes> postLoginKakao(@RequestHeader("X-ACCESS-TOKEN") String token) {
        try {
            PostLoginRes postLoginRes = userService.loginUser(SNSLogin.getUserIdFromKakao(token));
            return new BaseResponse<>(SUCCESS, postLoginRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 회원가입 API
     * [POST] /app/users
     *
     * @return BaseResponse<PostSignUpRes>
     * @RequestHeader X-ACCESS-TOKEN
     * @RequestBody PostSignUpReq parameters
     * @Auther shine
     */
    @ApiOperation(value = "회원가입", notes = "X-ACCESS-TOKEN 구글 혹은 카카오 토큰 필요")
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

        if(Objects.isNull(parameters.getSocialType()) || parameters.getSocialType().length() == 0) {
            return new BaseResponse<>(EMPTY_SOCIAL_TYPE);
        }

        try {
            PostSignUpRes postSignUpRes = userService.createUser(parameters, token);
            return new BaseResponse<>(SUCCESS, postSignUpRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 회원정보 수정 API
     * [PATCH] /app/users
     *
     * @RequestBody PatchUserReq parameters
     * @RequestHeader X-ACCESS-TOKEN
     * @return BaseResponse<PatchUserRes>
     * @Auther shine
     */
    @ApiOperation(value = "회원정보 수정", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PatchMapping("/users")
    public BaseResponse<PatchUserRes> updateUser(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                 @RequestBody(required = false) PatchUserReq parameters) {
        if (Objects.isNull(parameters.getName()) || parameters.getName().length() == 0) {
            return new BaseResponse<>(EMPTY_NAME);
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

        if(Objects.nonNull(parameters.getStrPrivate())
                || Objects.nonNull(parameters.getStrCorporal())
                || Objects.nonNull(parameters.getStrSergeant())) {
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
        }

        if(Objects.nonNull(parameters.getProDate())) {
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
            PatchUserRes userRes = userService.updateUser(parameters);
            return new BaseResponse<>(SUCCESS, userRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 회원탈퇴 API
     * [DELETE] /app/users
     *
     * @RequestHeader X-ACCESS-TOKEN
     * @return BaseResponse<Void>
     * @Auther shine
     */
    @ApiOperation(value = "회원탈퇴", notes = "X-ACCESS-TOKEN jwt 필요")
    @DeleteMapping("/users")
    public BaseResponse<Void> deleteUser(@RequestHeader("X-ACCESS-TOKEN") String token) {
        try {
            userService.deleteUser();
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 정기휴가 생성 API
     * [POST] /app/users/leaves
     *
     * @RequestBody PostLeaveReq parameters
     * @RequestHeader X-ACCESS-TOKEN
     * @return BaseResponse<PostLeaveRes>
     * @Auther shine
     */
    @ApiOperation(value = "휴가 생성", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PostMapping("/users/leaves")
    public BaseResponse<PostLeaveRes> postOrdinaryLeave(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                        @RequestBody(required = false) PostLeaveReq parameters) {
        if(Objects.isNull(parameters.getTitle()) || parameters.getTitle().length() == 0) {
            return new BaseResponse<>(EMPTY_TITLE);
        }
        if(Objects.isNull(parameters.getTotal())) {
            return new BaseResponse<>(EMPTY_TOTAL);
        }

        try {
            PostLeaveRes leaveRes = userService.createLeave(parameters);
            return new BaseResponse<>(SUCCESS, leaveRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 정기휴가 수정 API
     * [PATCH] /app/users/leaves/:leaveId
     *
     * @RequestBody PatchLeaveReq parameters
     * @RequestHeader X-ACCESS-TOKEN
     * @return BaseResponse<PatchLeaveRes>
     * @Auther shine
     */
    @ApiOperation(value = "정기휴가 수정", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PatchMapping("/users/leaves/{leaveId}")
    public BaseResponse<PatchLeaveRes> updateOrdinaryLeave(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                           @RequestBody(required = false) PatchLeaveReq parameters,
                                                           @PathVariable Long leaveId) {
        if(Objects.isNull(parameters.getTitle()) || parameters.getTitle().length() == 0) {
            return new BaseResponse<>(EMPTY_TITLE);
        }
        if(Objects.isNull(parameters.getTotal())) {
            return new BaseResponse<>(EMPTY_TOTAL);
        }

        try {
            PatchLeaveRes ordinaryLeaveRes = userService.updateLeave(parameters, leaveId);
            return new BaseResponse<>(SUCCESS, ordinaryLeaveRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 정기휴가 삭제 API
     * [DELETE] /app/users/leaves/:leaveId
     *
     * @PathVariable Long leaveId
     * @RequestHeader X-ACCESS-TOKEN
     * @return BaseResponse<Void>
     * @Auther shine
     */
    @ApiOperation(value = "정기휴가 삭제", notes = "X-ACCESS-TOKEN jwt 필요")
    @DeleteMapping("/users/leaves/{leaveId}")
    public BaseResponse<Void> deleteOrdinaryLeave(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                  @PathVariable Long leaveId) {
        try {
            userService.deleteLeave(leaveId);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

//    /**
//     *
//     * @param token
//     * @return
//     */
//    @GetMapping("/users/ordinary-leaves")
//    public BaseResponse<GetOrdinaryLeaveRes> getOrdinaryLeave(@RequestHeader("X-ACCESS-TOKEN") String token) {
//        try {
//            GetOrdinaryLeaveRes ordinaryLeaveRes = userProvider.retrieve();
//            return new BaseResponse<>(SUCCESS, ordinaryLeaveRes);
//        } catch (BaseException exception) {
//            return new BaseResponse<>(exception.getStatus());
//        }
//    }



    /**
     * 테스트용 jwt 생성, 나중에 삭제할거
     * [post] /app/jwt/:id
     */
    @ApiOperation(value = "테스트용 jwt 생성", notes = "삭제할 예정")
    @PostMapping("/jwt/{id}")
    public String postJWT(@PathVariable Long id) {
        return jwtService.createJwt(id);
    }
}
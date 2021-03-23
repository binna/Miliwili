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
            PostLoginRes login = userService.loginUser(SNSLogin.getUserIdFromKakao(token));
            return new BaseResponse<>(SUCCESS, login);
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
        if (parameters.getName().length() >= 20) {
            return new BaseResponse<>(EXCEED_MAX20);
        }
        if (Objects.isNull(parameters.getStateIdx())) {
            return new BaseResponse<>(EMPTY_STATEIDX);
        }
        if (1 > parameters.getStateIdx() || parameters.getStateIdx() > 4) {
            return new BaseResponse<>(INVALID_STATEIDX);
        }
        if (Objects.isNull(parameters.getServeType()) || parameters.getServeType().length() == 0) {
            return new BaseResponse<>(EMPTY_SERVE_TYPE);
        }
        if (parameters.getServeType().length() >= 10) {
            return new BaseResponse<>(EXCEED_MAX10);
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

        if(Objects.nonNull(parameters.getGoal())) {
            if(parameters.getGoal().length() >= 25) {
                return new BaseResponse<>(EXCEED_MAX25);
            }
        }
        if (Objects.isNull(parameters.getSocialType()) || parameters.getSocialType().length() == 0) {
            return new BaseResponse<>(EMPTY_SOCIAL_TYPE);
        }
        if(!(parameters.getSocialType().equals("K") && parameters.getSocialType().equals("G"))) {
            return new BaseResponse<>(INVALID_SOCIAL_TYPE);
        }

        try {
            PostSignUpRes signUp = userService.createUser(parameters, token);
            return new BaseResponse<>(SUCCESS, signUp);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 회원정보 수정 API
     * [PATCH] /app/users
     *
     * @return BaseResponse<PatchUserRes>
     * @RequestBody PatchUserReq parameters
     * @RequestHeader X-ACCESS-TOKEN
     * @Auther shine
     */
    @ApiOperation(value = "회원정보 수정", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PatchMapping("/users")
    public BaseResponse<PatchUserRes> updateUser(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                 @RequestBody(required = false) PatchUserReq parameters) {
        if(Objects.nonNull(parameters.getName()) || Objects.nonNull(parameters.getBirthday())) {
            if (Objects.isNull(parameters.getName()) || parameters.getName().length() == 0) {
                return new BaseResponse<>(EMPTY_NAME);
            }
            if (parameters.getServeType().length() >= 10) {
                return new BaseResponse<>(EXCEED_MAX10);
            }
            if (Objects.isNull(parameters.getBirthday()) || parameters.getBirthday().length() == 0) {
                return new BaseResponse<>(EMPTY_BIRTHDAY);
            }
            if (!Validation.isRegexDate(parameters.getBirthday())) {
                return new BaseResponse<>(INVALID_BIRTHDAY);
            }
        }

        else if(Objects.nonNull(parameters.getStrPrivate()) || Objects.nonNull(parameters.getStrCorporal()) || Objects.nonNull(parameters.getStrSergeant())
                || Objects.nonNull(parameters.getProDate())) {
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

            if (Objects.isNull(parameters.getProDate())) {
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
        }

        else if(Objects.nonNull(parameters.getGoal())) {
            if(parameters.getGoal().length() >= 25) {
                return new BaseResponse<>(EXCEED_MAX25);
            }
        }

        else {
            return new BaseResponse<>(EMPTY_ALL);
        }

        try {
            PatchUserRes user = userService.updateUser(parameters);
            return new BaseResponse<>(SUCCESS, user);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 회원탈퇴 API
     * [DELETE] /app/users
     *
     * @return BaseResponse<Void>
     * @RequestHeader X-ACCESS-TOKEN
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
     * 휴가생성 API
     * [POST] /app/users/vacation
     *
     * @return BaseResponse<PostVacationRes>
     * @RequestBody PostVacationReq parameters
     * @RequestHeader X-ACCESS-TOKEN
     * @Auther shine
     */
    @ApiOperation(value = "휴가 생성", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PostMapping("/users/vacation")
    public BaseResponse<PostVacationRes> postVacation(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                      @RequestBody(required = false) PostVacationReq parameters) {
        if (Objects.isNull(parameters.getVacationType()) || parameters.getVacationType().length() == 0) {
            return new BaseResponse<>(EMPTY_VACATION_TYPE);
        }
        if (Objects.isNull(parameters.getTitle()) || parameters.getTitle().length() == 0) {
            return new BaseResponse<>(EMPTY_TITLE);
        }
        if (parameters.getVacationType().length() >= 20 || parameters.getTitle().length() >= 20) {
            return new BaseResponse<>(EXCEED_MAX20);
        }
        if (Objects.isNull(parameters.getTotalDays())) {
            return new BaseResponse<>(EMPTY_TOTAL_DAYS);
        }

        try {
            PostVacationRes vacation = userService.createVacation(parameters);
            return new BaseResponse<>(SUCCESS, vacation);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 휴가수정 API
     * [PATCH] /app/users/vacation/:vacationId
     *
     * @return BaseResponse<PatchVacationRes>
     * @RequestBody PatchVacationReq parameters
     * @RequestHeader X-ACCESS-TOKEN
     * @Auther shine
     */
    @ApiOperation(value = "정기휴가 수정", notes = "X-ACCESS-TOKEN jwt 필요")
    @ResponseBody
    @PatchMapping("/users/vacation/{vacationId}")
    public BaseResponse<PatchVacationRes> updateVacation(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                         @RequestBody(required = false) PatchVacationReq parameters,
                                                         @PathVariable Long vacationId) {
        if (Objects.isNull(parameters.getTitle()) || parameters.getTitle().length() == 0) {
            return new BaseResponse<>(EMPTY_TITLE);
        }
        if (parameters.getTitle().length() < 20) {
            return new BaseResponse<>(EXCEED_MAX20);
        }
        if (Objects.isNull(parameters.getTotalDays())) {
            return new BaseResponse<>(EMPTY_TOTAL_DAYS);
        }
        if (Objects.nonNull(parameters.getUseDays())) {
            if (parameters.getUseDays() > parameters.getTotalDays()) {
                return new BaseResponse<>(NOT_BE_GREATER_THAN_TOTAL_DAYS);
            }
        }

        try {
            PatchVacationRes vacation = userService.updateVacation(parameters, vacationId);
            return new BaseResponse<>(SUCCESS, vacation);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 휴가삭제 API
     * [DELETE] /app/users/vacation/:vacationId
     *
     * @return BaseResponse<Void>
     * @PathVariable Long vacationId
     * @RequestHeader X-ACCESS-TOKEN
     * @Auther shine
     */
    @ApiOperation(value = "정기휴가 삭제", notes = "X-ACCESS-TOKEN jwt 필요")
    @DeleteMapping("/users/vacation/{vacationId}")
    public BaseResponse<Void> deleteVacation(@RequestHeader("X-ACCESS-TOKEN") String token,
                                             @PathVariable Long vacationId) {
        try {
            userService.deleteVacation(vacationId);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

//    /**
//     * @param token
//     * @return
//     */
//    @ApiOperation(value = "정기휴가 조회", notes = "X-ACCESS-TOKEN jwt 필요")
//    @GetMapping("/users/ordinary-leaves")
//    public BaseResponse<List<GetLeaveRes>> getOrdinaryLeave(@RequestHeader("X-ACCESS-TOKEN") String token) {
//        try {
//            List<GetLeaveRes> LeaveRes = userProvider.retrieve();
//            return new BaseResponse<>(SUCCESS, LeaveRes);
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
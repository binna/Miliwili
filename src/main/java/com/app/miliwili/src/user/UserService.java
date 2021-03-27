package com.app.miliwili.src.user;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.calendar.CalendarProvider;
import com.app.miliwili.src.user.dto.*;
import com.app.miliwili.src.user.models.AbnormalPromotionState;
import com.app.miliwili.src.user.models.NormalPromotionState;
import com.app.miliwili.src.user.models.UserInfo;
import com.app.miliwili.src.user.models.Vacation;
import com.app.miliwili.utils.JwtService;
import com.app.miliwili.utils.SNSLogin;
import com.app.miliwili.utils.Validation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserProvider userProvider;
    private final CalendarProvider calendarProvider;
    private final SNSLogin snsLogin;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final VacationRepository vacationRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * [로그인 - 구글 ]
     */
    public PostLoginRes createGoogleJwtToken(String googleSocialId) throws BaseException {

        String socialId = googleSocialId;

        PostLoginRes postLoginRes;
        String jwtToken = "";
        boolean isMemeber = true;
        long id = 0;

        List<Long> userIdList;
        try {
            userIdList = userProvider.isGoogleUser(socialId);
        } catch (Exception e) {
            throw new BaseException(FAILED_TO_GET_USER);
        }


        //id 추출
        if (userIdList == null || userIdList.size() == 0) {
            isMemeber = false;
        } else {
            id = userIdList.get(0);
        }


        if (isMemeber == true) { //회원일 떄
            jwtToken = jwtService.createJwt(id);
        } else { //회원이 아닐 때
            jwtToken = "";
        }

        postLoginRes = PostLoginRes.builder()
                .isMember(isMemeber)
                .jwt(jwtToken)
                .build();

        return postLoginRes;

    }

    /**
     * 로그인
     *
     * @param socialId
     * @return PostLoginRes
     * @throws BaseException
     * @Auther shine
     */
    public PostLoginRes loginUser(String socialId) throws BaseException {
        UserInfo user = null;

        try {
            user = userProvider.retrieveUserBySocialIdAndStatusY(socialId);
        } catch (BaseException exception) {
            if (exception.getStatus() == NOT_FOUND_USER) {
                return new PostLoginRes(false, null);
            }
            logger.warn(exception.getStatus().toString());
            throw new BaseException(FAILED_TO_GET_USER);
        } catch (Exception exception) {
            logger.warn(Validation.getPrintStackTrace(exception));
            throw new BaseException(FAILED_TO_GET_USER);
        }

        return new PostLoginRes(true, jwtService.createJwt(user.getId()));
    }

    /**
     * 회원가입
     *
     * @param parameters
     * @param token
     * @return PostSignUpRes
     * @throws BaseException
     * @Auther shine
     */
    public PostSignUpRes createUser(PostSignUpReq parameters, String token) throws BaseException {
        UserInfo newUser = UserInfo.builder()
                .name(parameters.getName())
                .serveType(parameters.getServeType())
                .stateIdx(parameters.getStateIdx())
                .goal(parameters.getGoal())
                .startDate(LocalDate.parse(parameters.getStartDate(), DateTimeFormatter.ISO_DATE))
                .endDate(LocalDate.parse(parameters.getEndDate(), DateTimeFormatter.ISO_DATE))
                .build();
        setSocial(parameters.getSocialType(), token, newUser);
        setUserPromotionState(parameters.getStrPrivate(), parameters.getStrCorporal(), parameters.getStrSergeant(), parameters.getProDate(), newUser);
        setProfileImg(newUser.getSocialType(), token, newUser);

        if (userProvider.isUserBySocialId(newUser.getSocialId())) {
            logger.warn(new BaseException(DUPLICATED_USER).toString());
            throw new BaseException(DUPLICATED_USER);
        }

        try {
            newUser = userRepository.save(newUser);
            setVacationData(newUser);
            return new PostSignUpRes(newUser.getId(), jwtService.createJwt(newUser.getId()));
        } catch (BaseException exception) {
            if (exception.getStatus() == SET_VACATION_PLAN) {
                userRepository.delete(newUser);
            }
            logger.warn(exception.getStatus().toString());
            throw new BaseException(FAILED_TO_SIGNUP_USER);
        } catch (Exception exception) {
            logger.warn(Validation.getPrintStackTrace(exception));
            throw new BaseException(FAILED_TO_SIGNUP_USER);
        }
    }

    /**
     * 사용자 정보 수정
     *
     * @param parameters
     * @return PatchUserRes
     * @throws BaseException
     * @Auther shine
     */
    public UserRes updateUser(PatchUserReq parameters) throws BaseException {
        UserInfo user = userProvider.retrieveUserByIdAndStatusY(jwtService.getUserId());

        setNameOrBirthdayOrProfileImg(parameters, user);
        setNormalOrAbnormal(parameters, user);
        getGoal(parameters, user);

        try {
            UserInfo savedUser = userRepository.save(user);
            return userProvider.changeUserInfoToUserRes(savedUser);
        } catch (Exception exception) {
            logger.warn(Validation.getPrintStackTrace(exception));
            throw new BaseException(FAILED_TO_PATCH_USER);
        }
    }

    /**
     * 회원삭제
     *
     * @return void
     * @throws BaseException
     * @Auther shine
     */
    public void deleteUser() throws BaseException {
        UserInfo user = userProvider.retrieveUserByIdAndStatusY(jwtService.getUserId());
        user.setStatus("N");

        try {
            userRepository.save(user);
        } catch (Exception exception) {
            logger.warn(Validation.getPrintStackTrace(exception));
            throw new BaseException(FAILED_TO_DELETE_USER);
        }
    }

    /**
     * 휴가 수정
     *
     * @param parameters
     * @param vacationId
     * @return PatchVacationRes
     * @throws BaseException
     * @Auther shine
     */
    public VacationRes updateVacation(VacationReq parameters, Long vacationId) throws BaseException {
        Vacation vacation = userProvider.retrieveVacationById(vacationId);
        int count = getPlanVacationCount(vacationId);

        setTotalDays(parameters.getTotalDays(), vacation);
        setUseDays(parameters.getUseDays(), vacation);

        if (vacation.getUserInfo().getId() != jwtService.getUserId()) {
            logger.warn(new BaseException(DO_NOT_AUTH_USER).toString());
            throw new BaseException(DO_NOT_AUTH_USER);
        }

        try {
            Vacation savedVacation = vacationRepository.save(vacation);
            return VacationRes.builder()
                    .vacationId(savedVacation.getId())
                    .title(savedVacation.getTitle())
                    .useDays(savedVacation.getUseDays() + count)
                    .totalDays(savedVacation.getTotalDays())
                    .build();
        } catch (Exception exception) {
            logger.warn(Validation.getPrintStackTrace(exception));
            throw new BaseException(FAILED_TO_PATCH_VACATION);
        }
    }




    /**
     * stateIdx 계산
     *
     * @param strPrivate
     * @param strCorporal
     * @param strSergeant
     * @param normalPromotionState
     * @return void
     * @Auther shine
     */
    public void setStateIdx(String strPrivate, String strCorporal, String strSergeant, NormalPromotionState normalPromotionState) {
        LocalDate nowDay = LocalDate.now();
        LocalDate strPrivateDate = LocalDate.parse(strPrivate, DateTimeFormatter.ISO_DATE);
        LocalDate strCorporalDate = LocalDate.parse(strCorporal, DateTimeFormatter.ISO_DATE);
        LocalDate strSergeantDate = LocalDate.parse(strSergeant, DateTimeFormatter.ISO_DATE);

        if (nowDay.isBefore(strPrivateDate)) {
            normalPromotionState.setStateIdx(0);
            return;
        }
        if (nowDay.isAfter(strPrivateDate) && nowDay.isBefore(strCorporalDate)) {
            normalPromotionState.setStateIdx(1);
            return;
        }
        if (nowDay.isAfter(strCorporalDate) && nowDay.isBefore(strSergeantDate)) {
            normalPromotionState.setStateIdx(2);
            return;
        }
        normalPromotionState.setStateIdx(3);
    }

    /**
     * 호봉 계산기
     *
     * @param stateIdx
     * @param startDate
     * @param strPrivate
     * @param strCorporal
     * @param strSergeant
     * @param normalPromotionState
     * @return void
     * @Auther shine
     */
    public void setHobong(Integer stateIdx,
                          String startDate, String strPrivate, String strCorporal, String strSergeant,
                          NormalPromotionState normalPromotionState) {
        LocalDate nowDay = LocalDate.now();
        Long hobong = Long.valueOf(0);

        if (stateIdx == 0) {
            LocalDate settingDay = setSettingDay(LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE), normalPromotionState);
            hobong = ChronoUnit.MONTHS.between(settingDay, nowDay);
            normalPromotionState.setHobong(hobong.intValue() + normalPromotionState.getHobong());
            return;
        }
        if (stateIdx == 1) {
            LocalDate settingDay = setSettingDay(LocalDate.parse(strPrivate, DateTimeFormatter.ISO_DATE), normalPromotionState);
            hobong = ChronoUnit.MONTHS.between(settingDay, nowDay);
            normalPromotionState.setHobong(hobong.intValue() + normalPromotionState.getHobong());
            return;
        }
        if (stateIdx == 2) {
            LocalDate settingDay = setSettingDay(LocalDate.parse(strCorporal, DateTimeFormatter.ISO_DATE), normalPromotionState);
            hobong = ChronoUnit.MONTHS.between(settingDay, nowDay);
            normalPromotionState.setHobong(hobong.intValue() + normalPromotionState.getHobong());
            return;
        }
        LocalDate settingDay = setSettingDay(LocalDate.parse(strSergeant, DateTimeFormatter.ISO_DATE), normalPromotionState);
        hobong = ChronoUnit.MONTHS.between(settingDay, nowDay);
        normalPromotionState.setHobong(hobong.intValue() + normalPromotionState.getHobong());
    }




    private void setNormalOrAbnormal(PatchUserReq parameters, UserInfo user) {
        if (Objects.nonNull(parameters.getServeType())) {
            user.setServeType(parameters.getServeType());
            user.setStartDate(LocalDate.parse(parameters.getStartDate(), DateTimeFormatter.ISO_DATE));
            user.setEndDate(LocalDate.parse(parameters.getEndDate(), DateTimeFormatter.ISO_DATE));

            if (user.getStateIdx() == 1) {
                user.getNormalPromotionState().setFirstDate(LocalDate.parse(parameters.getStrPrivate(), DateTimeFormatter.ISO_DATE));
                user.getNormalPromotionState().setSecondDate(LocalDate.parse(parameters.getStrCorporal(), DateTimeFormatter.ISO_DATE));
                user.getNormalPromotionState().setThirdDate(LocalDate.parse(parameters.getStrSergeant(), DateTimeFormatter.ISO_DATE));

                String startDate = parameters.getStartDate();
                String strPrivate = parameters.getStrPrivate();
                String strCorporal = parameters.getStrCorporal();
                String strSergeant = parameters.getStrSergeant();
                setStateIdx(strPrivate, strCorporal, strSergeant, user.getNormalPromotionState());
                setHobong(user.getNormalPromotionState().getStateIdx(), startDate, strPrivate, strCorporal, strSergeant, user.getNormalPromotionState());
            }
            if (!(user.getStateIdx() == 1)) {
                user.getAbnormalPromotionState().setProDate(LocalDate.parse(parameters.getProDate(), DateTimeFormatter.ISO_DATE));
            }
        }
    }

    private void getGoal(PatchUserReq parameters, UserInfo user) {
        if (Objects.nonNull(parameters.getGoal())) {
            user.setGoal(parameters.getGoal());
        }
    }

    private void setNameOrBirthdayOrProfileImg(PatchUserReq parameters, UserInfo user) {
        if (Objects.nonNull(parameters.getName())) {
            user.setName(parameters.getName());

            if (Objects.nonNull(parameters.getBirthday())) {
                user.setBirthday(LocalDate.parse(parameters.getBirthday(), DateTimeFormatter.ISO_DATE));
            }
            if (Objects.nonNull(parameters.getProfileImg())) {
                user.setProfileImg(parameters.getProfileImg());
            }
        }
    }

    private void setUseDays(Integer useDays, Vacation vacation) {
        if (Objects.nonNull(useDays)) {
            vacation.setUseDays(useDays);
        }
    }

    private void setTotalDays(Integer totalDays, Vacation vacation) {
        if (Objects.nonNull(totalDays)) {
            vacation.setTotalDays(totalDays);
        }
    }

    private int getPlanVacationCount(Long vacationId) throws BaseException {
        try {
            return calendarProvider.retrievePlanVacationByIdAndStatusY(vacationId).getCount();
        } catch (BaseException exception) {
            if (exception.getStatus() == NOT_FOUND_VACATION_PLAN) {
                return 0;
            }
            throw new BaseException(FAILED_TO_GET_VACATION_PLAN);
        }
    }

    private void setVacationData(UserInfo user) throws BaseException {
        Vacation vacation1 = Vacation.builder().title("정기휴가").userInfo(user).totalDays(24).build();
        Vacation vacation2 = Vacation.builder().title("포상휴가").userInfo(user).totalDays(15).build();
        Vacation vacation3 = Vacation.builder().title("기타휴가").userInfo(user).totalDays(0).build();

        try {
            List<Vacation> leaveList = Arrays.asList(vacation1, vacation2, vacation3);
            vacationRepository.saveAll(leaveList);
        } catch (Exception exception) {
            throw new BaseException(SET_VACATION_PLAN);
        }
    }

    private void setSocial(String socialType, String token, UserInfo user) throws BaseException {
        if (socialType.equals("K")) {
            user.setSocialType(socialType);
            user.setSocialId(snsLogin.getUserIdFromKakao(token));
            return;
        }
        user.setSocialType(socialType);
        user.setSocialId(snsLogin.userIdFromGoogle(token.replaceAll("\"", "")));
    }

    private void setProfileImg(String socialType, String token, UserInfo user) throws BaseException {
        if (socialType.equals("K")) {
            String img = snsLogin.getProfileImgFromKakao(token);
            if (!img.isEmpty()) {
                user.setProfileImg(img);
                return;
            }
        }
        user.setProfileImg("https://miliwili-storage.s3.ap-northeast-2.amazonaws.com/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2021-03-10+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+7.21.24.png");
    }

    private void setUserPromotionState(String strPrivate, String strCorporal, String strSergeant,
                                       String proDate,
                                       UserInfo user) {
        if (user.getStateIdx() == 1) {
            NormalPromotionState normalPromotionState = NormalPromotionState.builder()
                    .firstDate(LocalDate.parse(strPrivate, DateTimeFormatter.ISO_DATE))
                    .secondDate(LocalDate.parse(strCorporal, DateTimeFormatter.ISO_DATE))
                    .thirdDate(LocalDate.parse(strSergeant, DateTimeFormatter.ISO_DATE))
                    .userInfo(user)
                    .build();
            setStateIdx(strPrivate, strCorporal, strSergeant, normalPromotionState);
            setHobong(user.getStateIdx(), user.getStartDate().format(DateTimeFormatter.ISO_DATE), strPrivate, strCorporal, strSergeant, normalPromotionState);
            user.setNormalPromotionState(normalPromotionState);
            return;
        }
        AbnormalPromotionState abnormalPromotionState = AbnormalPromotionState.builder()
                .proDate(LocalDate.parse(proDate, DateTimeFormatter.ISO_DATE))
                .userInfo(user)
                .build();
        user.setAbnormalPromotionState(abnormalPromotionState);
    }

    private LocalDate setSettingDay(LocalDate settingDay, NormalPromotionState normalPromotionState) {
        if (settingDay.getDayOfMonth() == 1) {
            return settingDay;
        }

        normalPromotionState.setHobong(1);
        if (settingDay.getMonthValue() == 12) {
            normalPromotionState.setHobong(normalPromotionState.getHobong() + 1);
            return LocalDate.parse((settingDay.getYear() + 1) + "-01-01");
        }
        if (settingDay.getMonthValue() >= 9) {
            return LocalDate.parse(settingDay.getYear() + "-" + (settingDay.getMonthValue() + 1) + "-01");
        }
        return LocalDate.parse(settingDay.getYear() + "-0" + (settingDay.getMonthValue() + 1) + "-01");
    }
}
package com.app.miliwili.src.user;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.calendar.CalendarProvider;
import com.app.miliwili.src.calendar.models.PlanVacation;
import com.app.miliwili.src.user.dto.*;
import com.app.miliwili.src.user.models.AbnormalPromotionState;
import com.app.miliwili.src.user.models.NormalPromotionState;
import com.app.miliwili.src.user.models.UserInfo;
import com.app.miliwili.src.user.models.Vacation;
import com.app.miliwili.utils.JwtService;
import com.app.miliwili.utils.SNSLogin;
import com.app.miliwili.utils.Validation;
import lombok.RequiredArgsConstructor;
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
            throw new BaseException(FAILED_TO_GET_USER);
        } catch (Exception exception) {
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
            throw new BaseException(DUPLICATED_USER);
        }

        try {
            newUser = userRepository.save(newUser);
            setVacationData(newUser);
            return new PostSignUpRes(newUser.getId(), jwtService.createJwt(newUser.getId()));
        } catch (BaseException exception) {
            if (exception.getStatus() == SET_PLAN_VACATION) {
                userRepository.delete(newUser);
            }
            throw new BaseException(FAILED_TO_SIGNUP_USER);
        } catch (Exception exception) {
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

        checkValidationOfInputValuesByServeData(user.getStateIdx(), parameters);

        setNameOrBirthdayOrProfileImg(parameters, user);
        setNormalOrAbnormal(parameters, user);
        getGoal(parameters, user);

        try {
            UserInfo savedUser = userRepository.save(user);
            return userProvider.changeUserInfoToUserRes(savedUser);
        } catch (Exception exception) {
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
        setUseDays(parameters.getUseDays(), count, vacation);

        if (vacation.getUserInfo().getId() != jwtService.getUserId()) {
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




    private void checkValidationOfInputValuesByServeData(Integer stateIdx, PatchUserReq parameters) throws BaseException {
        LocalDate startDate = LocalDate.parse(parameters.getStartDate(), DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate.parse(parameters.getEndDate(), DateTimeFormatter.ISO_DATE);

        if (stateIdx == 1) {
            if (Objects.isNull(parameters.getStrPrivate()) || parameters.getStrPrivate().length() == 0) {
                throw new BaseException(EMPTY_FIRST_DATE);
            }
            if (!Validation.isRegexDate(parameters.getStrPrivate())) {
                throw new BaseException(INVALID_FIRST_DATE);
            }
            LocalDate firstDate = LocalDate.parse(parameters.getStrPrivate(), DateTimeFormatter.ISO_DATE);
            if (startDate.isAfter(firstDate)) {
                throw new BaseException(FASTER_THAN_FIRST_DATE);
            }

            if (Objects.isNull(parameters.getStrCorporal()) || parameters.getStrCorporal().length() == 0) {
                throw new BaseException(EMPTY_SECOND_DATE);
            }
            if (!Validation.isRegexDate(parameters.getStrCorporal())) {
                throw new BaseException(INVALID_SECOND_DATE);
            }
            LocalDate secondDate = LocalDate.parse(parameters.getStrCorporal(), DateTimeFormatter.ISO_DATE);
            if (firstDate.isAfter(secondDate)) {
                throw new BaseException(FASTER_THAN_SECOND_DATE);
            }

            if (Objects.isNull(parameters.getStrSergeant()) || parameters.getStrSergeant().length() == 0) {
                throw new BaseException(EMPTY_THIRD_DATE);
            }
            if (!Validation.isRegexDate(parameters.getStrSergeant())) {
                throw new BaseException(INVALID_THIRD_DATE);
            }
            LocalDate thirdDate = LocalDate.parse(parameters.getStrSergeant(), DateTimeFormatter.ISO_DATE);
            if (secondDate.isAfter(thirdDate)) {
                throw new BaseException(FASTER_THAN_THIRD_DATE);
            }

            if (thirdDate.isAfter(endDate)) {
                throw new BaseException(FASTER_THAN_END_DATE_NOR);
            }
            return;
        }
        if (Objects.isNull(parameters.getProDate()) || parameters.getProDate().length() == 0) {
            throw new BaseException(EMPTY_PRO_DATE);
        }
        if (!Validation.isRegexDate((parameters.getProDate()))) {
            throw new BaseException(INVALID_PRO_DATE);
        }
        LocalDate proDate = LocalDate.parse(parameters.getProDate(), DateTimeFormatter.ISO_DATE);
        if (startDate.isAfter(proDate)) {
            throw new BaseException(FASTER_THAN_PRO_DATE);
        }
        if (proDate.isAfter(endDate)) {
            throw new BaseException(FASTER_THAN_END_DATE_ABN);
        }
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
        if (Objects.nonNull(parameters.getName()) && !parameters.getName().isEmpty()) {
            user.setName(parameters.getName());


            if (Objects.nonNull(parameters.getBirthday()) && !parameters.getBirthday().isEmpty()) {
                user.setBirthday(LocalDate.parse(parameters.getBirthday(), DateTimeFormatter.ISO_DATE));
            }
            if (Objects.nonNull(parameters.getProfileImg()) && !parameters.getProfileImg().isEmpty()) {
                user.setProfileImg(parameters.getProfileImg());
            }
        }
    }

    private void setUseDays(Integer useDays, int count, Vacation vacation) throws BaseException {
        if (Objects.nonNull(useDays)) {
            vacation.setUseDays(vacation.getUseDays() + useDays);

            if ((vacation.getUseDays() + count) > vacation.getTotalDays()) {
                throw new BaseException(NOT_BE_GREATER_THAN_TOTAL_DAYS);
            }
        }
    }

    private void setTotalDays(Integer totalDays, Vacation vacation) {
        if (Objects.nonNull(totalDays)) {
            vacation.setTotalDays(totalDays);
        }
    }

    private int getPlanVacationCount(Long vacationId) throws BaseException {
        List<PlanVacation> planVacationList = calendarProvider.retrievePlanVacationByIdAndStatusY(vacationId);
        if(planVacationList.isEmpty()) return 0;

        int sum = 0;
        for(PlanVacation planVacation : planVacationList) {
            sum += planVacation.getCount();
        }
        return sum;
    }

    private void setVacationData(UserInfo user) throws BaseException {
        Vacation vacation1 = Vacation.builder().title("정기휴가").userInfo(user).totalDays(24).build();
        Vacation vacation2 = Vacation.builder().title("포상휴가").userInfo(user).totalDays(0).build();
        Vacation vacation3 = Vacation.builder().title("기타휴가").userInfo(user).totalDays(0).build();

        try {
            List<Vacation> leaveList = Arrays.asList(vacation1, vacation2, vacation3);
            vacationRepository.saveAll(leaveList);
        } catch (Exception exception) {
            throw new BaseException(SET_PLAN_VACATION);
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
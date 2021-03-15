package com.app.miliwili.src.user;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.user.dto.*;
import com.app.miliwili.src.user.models.AbnormalPromotionState;
import com.app.miliwili.src.user.models.NormalPromotionState;
import com.app.miliwili.src.user.models.OrdinaryLeave;
import com.app.miliwili.src.user.models.User;
import com.app.miliwili.utils.JwtService;
import com.app.miliwili.utils.SNSLogin;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserProvider userProvider;
    private final SNSLogin snsLogin;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final OrdinaryLeaveRepository ordinaryLeaveRepository;

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
     * @param String socialId
     * @return PostLoginRes
     * @throws BaseException
     * @Auther shine
     */
    public PostLoginRes loginUser(String socialId) throws BaseException {
        User user = null;

        try {
            user = userProvider.retrieveUserBySocialIdAndStatusY(socialId);
        } catch (BaseException exception) {
            if (exception.getStatus() != NOT_FOUND_USER) {
                return new PostLoginRes(false, null);
            }
        }

        return new PostLoginRes(true, jwtService.createJwt(user.getId()));
    }

    /**
     * 회원가입
     *
     * @param PostSignUpReq parameters, String token
     * @return PostSignUpRes
     * @throws BaseException
     * @Auther shine
     */
    public PostSignUpRes createUser(PostSignUpReq parameters, String token) throws BaseException {
        User newUser = User.builder()
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
            User savedUser = userRepository.save(newUser);
            return new PostSignUpRes(savedUser.getId(), jwtService.createJwt(savedUser.getId()));
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_SIGNUP_USER);
        }
    }

    /**
     * 사용자 정보 수정
     *
     * @param PatchUserReq parameters
     * @return PatchUserRes
     * @throws BaseException
     * @Auther shine
     */
    public PatchUserRes updateUser(PatchUserReq parameters) throws BaseException {
        User user = User.builder()
                .id(jwtService.getUserId())
                .name(parameters.getName())
                .serveType(parameters.getServeType())
                .goal(parameters.getGoal())
                .profileImg(parameters.getProfileImg())
                .startDate(LocalDate.parse(parameters.getStartDate(), DateTimeFormatter.ISO_DATE))
                .endDate(LocalDate.parse(parameters.getEndDate(), DateTimeFormatter.ISO_DATE))
                .build();
        setUserPromotionState(parameters.getStrPrivate(), parameters.getStrCorporal(), parameters.getStrSergeant(), parameters.getProDate(), user);

        try {
            User savedUser = userRepository.save(user);
            // TODO 리턴은 좀더 고민하기
            return PatchUserRes.builder()
                    .userId(savedUser.getId())
                    .name(savedUser.getName())
                    .serveType(savedUser.getServeType())
                    .stateIdx(savedUser.getStateIdx())
                    .startDate(savedUser.getStartDate().format(DateTimeFormatter.ISO_DATE))
                    .endDate(savedUser.getEndDate().format(DateTimeFormatter.ISO_DATE))
                    .goal(savedUser.getGoal())
                    .profileImg(savedUser.getProfileImg())
                    .build();
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_PATCH_USER);
        }
    }

    /**
     * 회원 삭제
     *
     * @return void
     * @throws BaseException
     * @Auther shine
     */
    public void deleteUser() throws BaseException {
        User user = userProvider.retrieveUserByIdAndStatusY(jwtService.getUserId());
        user.setStatus("N");

        try {
            userRepository.save(user);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_DELETE_USER);
        }
    }

    /**
     * 정기휴가 생성
     *
     * @param PostOrdinaryLeaveReq parameters
     * @return PostOrdinaryLeaveRes
     * @throws BaseException
     * @Auther shine
     */
    public PostOrdinaryLeaveRes createOrdinaryLeave(PostOrdinaryLeaveReq parameters) throws BaseException {
        User user = userProvider.retrieveUserByIdAndStatusY(jwtService.getUserId());

        OrdinaryLeave newOrdinaryLeave = OrdinaryLeave.builder()
                .startDate(LocalDate.parse(parameters.getStartDate(), DateTimeFormatter.ISO_DATE))
                .endDate(LocalDate.parse(parameters.getEndDate(), DateTimeFormatter.ISO_DATE))
                .user(user)
                .build();

        try {
            OrdinaryLeave savedOrdinaryLeave = ordinaryLeaveRepository.save(newOrdinaryLeave);
            return PostOrdinaryLeaveRes.builder()
                    .ordinaryLeaveId(savedOrdinaryLeave.getId())
                    .startDate(savedOrdinaryLeave.getStartDate().format(DateTimeFormatter.ISO_DATE))
                    .endDate(savedOrdinaryLeave.getEndDate().format(DateTimeFormatter.ISO_DATE))
                    .build();
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_ORDINARY_LEAVE);
        }
    }

    /**
     * 정기휴가 수정
     *
     * @param PatchOrdinaryLeaveReq parameters
     * @return PatchOrdinaryLeaveRes
     * @throws BaseException
     * @Auther shine
     */
    public PatchOrdinaryLeaveRes updateOrdinaryLeave(PatchOrdinaryLeaveReq parameters) throws BaseException {
        User user = userProvider.retrieveUserByIdAndStatusY(jwtService.getUserId());

        OrdinaryLeave ordinaryLeave = OrdinaryLeave.builder()
                .id(parameters.getOrdinaryLeaveId())
                .startDate(LocalDate.parse(parameters.getStartDate(), DateTimeFormatter.ISO_DATE))
                .endDate(LocalDate.parse(parameters.getEndDate(), DateTimeFormatter.ISO_DATE))
                .user(user)
                .build();

        try {
            OrdinaryLeave savedOrdinaryLeave = ordinaryLeaveRepository.save(ordinaryLeave);
            return PatchOrdinaryLeaveRes.builder()
                    .ordinaryLeaveId(savedOrdinaryLeave.getId())
                    .startDate(savedOrdinaryLeave.getStartDate().format(DateTimeFormatter.ISO_DATE))
                    .endDate(savedOrdinaryLeave.getEndDate().format(DateTimeFormatter.ISO_DATE))
                    .build();
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_PATCH_ORDINARY_LEAVE);
        }
    }

    /**
     * 정기휴가 삭제
     *
     * @param Long ordinaryLeaveId
     * @return void
     * @throws BaseException
     * @Auther shine
     */
    public void deleteOrdinaryLeave(Long ordinaryLeaveId) throws BaseException {
        OrdinaryLeave ordinaryLeave = ordinaryLeaveRepository.findById(ordinaryLeaveId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_ORDINARY_LEAVE));

        try {
            ordinaryLeaveRepository.delete(ordinaryLeave);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_DELETE_ORDINARY_LEAVE);
        }
    }



    private void setSocial(String socialType, String token, User user) throws BaseException {
        if (socialType.equals("K")) {
            user.setSocialType(socialType);
            user.setSocialId(snsLogin.getUserIdFromKakao(token));
            return;
        }
        user.setSocialType(socialType);
        user.setSocialId(snsLogin.userIdFromGoogle(token.replaceAll("\"", "")));
    }

    private void setProfileImg(String socialType, String token, User user) throws BaseException {
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
                                       User user) {
        if (user.getStateIdx() == 1) {
            NormalPromotionState normalPromotionState = NormalPromotionState.builder()
                    .firstDate(LocalDate.parse(strPrivate, DateTimeFormatter.ISO_DATE))
                    .secondDate(LocalDate.parse(strCorporal, DateTimeFormatter.ISO_DATE))
                    .thirdDate(LocalDate.parse(strSergeant, DateTimeFormatter.ISO_DATE))
                    .user(user)
                    .build();
            setStateIdx(strPrivate, strCorporal, strSergeant, normalPromotionState);
            setHobong(user.getStateIdx(), user.getStartDate().format(DateTimeFormatter.ISO_DATE), strPrivate, strCorporal, strSergeant, normalPromotionState);
            user.setNormalPromotionState(normalPromotionState);
            return;
        }
        AbnormalPromotionState abnormalPromotionState = AbnormalPromotionState.builder()
                .proDate(LocalDate.parse(proDate, DateTimeFormatter.ISO_DATE))
                .user(user)
                .build();
        user.setAbnormalPromotionState(abnormalPromotionState);
    }

    private void setHobong(Integer stateIdx,
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

    private void setStateIdx(String strPrivate, String strCorporal, String strSergeant, NormalPromotionState normalPromotionState) {
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
}
package com.app.miliwili.src.user;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.calendar.CalendarProvider;
import com.app.miliwili.src.calendar.ScheduleSelectRepository;
import com.app.miliwili.src.user.dto.GetAbnormalUserEndDate;
import com.app.miliwili.src.user.dto.GetLeaveRes;
import com.app.miliwili.src.user.models.Leave;
import com.app.miliwili.src.user.models.User;
import com.app.miliwili.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
public class UserProvider {
    private final UserSelectRepository userSelectRepository;
    private final UserRepository userRepository;
    private final LeaveRepository leaveRepository;
    private final ScheduleSelectRepository scheduleSelectRepository;
    private final JwtService jwtService;
    private final CalendarProvider calendarProvider;

    /**
     * 로그인시 존재하는 구글 아이디(socialId) 검사
     */
    public List<Long> isGoogleUser(String gSocialId) throws BaseException {
        List<Long> userList = null;

        try {
            userList = userSelectRepository.findUsersIdByGoogleId(gSocialId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(FAILED_TO_GET_USER);
        }


        return userList;
    }

    /**
     * stateIdx --> 일반병사인지 아닌지 확인
     *
     * @Auther vivi
     */
    public int retrieveUserstateIdx(long id) throws BaseException {
        int userStateIdx = 0;
        try {
            List<Integer> userStateList = userSelectRepository.findUserStateIdxByUserId(id);
            if (userStateList.size() == 0)
                throw new BaseException(FAILED_TO_GET_USER);

            userStateIdx = userStateList.get(0);

        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(FAILED_TO_GET_USER);
        }

        return userStateIdx;
    }

    /**
     * for 전역일 계산기 (MainProvider)
     * [ 부사관, 준사관, 장교 ]들을 위한
     *
     * @return GetAbnormalUserEndDate
     * @Auther vivi
     */
    public GetAbnormalUserEndDate retrieveAbnormalUserEndDate(long id) throws BaseException {
        GetAbnormalUserEndDate abnormalUserEndDate;
        try {
            List<GetAbnormalUserEndDate> userList = userSelectRepository.findEndDateInfoByUserId(id);

            if (userList.size() == 0)
                throw new BaseException(FAILED_TO_GET_USER);

            abnormalUserEndDate = userList.get(0);

        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(FAILED_TO_GET_USER);
        }

        return abnormalUserEndDate;
    }

    /**
     * userId로 유효한 회원만 조회
     *
     * @param userId
     * @return User
     * @throws BaseException
     * @Auther shine
     */
    @Transactional
    public User retrieveUserByIdAndStatusY(Long userId) throws BaseException {
        return userRepository.findByIdAndStatus(userId, "Y")
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
    }

    /**
     * socialId로 유효한 회원 존재여부 체크
     * (존재하면 true, 존재하지 않으면 false)
     *
     * @param socialId
     * @return boolean
     * @Auther shine
     */
    @Transactional
    public boolean isUserBySocialId(String socialId) {
        return userRepository.existsBySocialIdAndStatus(socialId, "Y");
    }

    /**
     * socialId로 유효한 회원조회
     *
     * @param socialId
     * @return User
     * @throws BaseException
     * @Auther shine
     */
    @Transactional
    public User retrieveUserBySocialIdAndStatusY(String socialId) throws BaseException {
        return userRepository.findBySocialIdAndStatus(socialId, "Y")
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
    }

    /**
     * ordinaryLeaveId로 정기휴가 조회
     *
     * @param leaveId
     * @return Leave
     * @throws BaseException
     * @Auther shine
     */
    @Transactional
    public Leave retrieveLeaveById(Long leaveId) throws BaseException {
        return leaveRepository.findById(leaveId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_LEAVE));
    }

    /**
     * 사용자별 휴가 전체 조회
     *
     * @return List<OrdinaryLeave>
     * @throws BaseException
     */
    private List<Leave> retrieveLeaveByUserIdOrderById() throws BaseException {
        try {
            List<Leave> ordinaryLeaves = leaveRepository.findByUser_IdOrderById(jwtService.getUserId());
            return ordinaryLeaves;
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_GET_LEAVE);
        }
    }

    /**
     *
     * @return
     * @throws BaseException
     */
    public List<GetLeaveRes> retrieve() throws BaseException {

        return null;
    }
}
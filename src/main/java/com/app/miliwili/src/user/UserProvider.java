package com.app.miliwili.src.user;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.user.models.GetAbnormalUserEndDate;
import com.app.miliwili.src.user.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.app.miliwili.config.BaseResponseStatus.FAILED_TO_GET_USER;
import static com.app.miliwili.config.BaseResponseStatus.NOT_FOUND_USER;

@RequiredArgsConstructor
@Service
public class UserProvider {
    private final UserSelectRepository userSelectRepository;
    private final UserRepository userRepository;


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
     * @param Long userId
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
     * @param String socialId
     * @return Boolean
     * @Auther shine
     */
    @Transactional
    public boolean isUserBySocialId(String socialId) {
        return userRepository.existsBySocialIdAndStatus(socialId, "Y");
    }

    /**
     * socialId로 유효한 회원조회
     *
     * @param String socialId
     * @return User
     * @throws BaseException
     * @Auther shine
     */
    @Transactional
    public User retrieveUserBySocialIdAndStatusY(String socialId) throws BaseException {
        return userRepository.findBySocialIdAndStatus(socialId, "Y")
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
    }
}
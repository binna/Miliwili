package com.app.miliwili.src.user;

import com.app.miliwili.config.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
public class UserProvider {
    private final UserSelectRepository userSelectRepository;





    /**
     * 로그인시 존재하는 구글 아이디(socialId) 검사
     */
    public List<Long> isGoogleUser(String gSocialId) throws BaseException{
        List<Long> userList = null;

        try{
            userList = userSelectRepository.findUsersIdByGoogleId(gSocialId);
        }catch (Exception e){
            e.printStackTrace();
            throw new BaseException(FAILED_TO_GET_USER);
        }


        return userList;
    }
}
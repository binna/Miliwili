package com.app.miliwili.src.user;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.config.BaseResponseStatus;
import com.app.miliwili.src.user.models.PostLoginRes;
import com.app.miliwili.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserProvider userProvider;
    private final JwtService jwtService;


    /**
     * [로그인 - 구글 ]
     */
    public PostLoginRes createGoogleJwtToken(String googleSocialId) throws BaseException {

        String socialId = googleSocialId;
        PostLoginRes postLoginRes;
        String jwtToken="";
        boolean isMemeber = true;
        long id = 0;

        List<Long> userIdList;
        try{
            userIdList = userProvider.isGoogleUser(socialId);
        }catch (Exception e){
            throw new BaseException(FAILED_TO_GET_USER);
        }


        //id 추출
        if(userIdList == null || userIdList.size() == 0){
            isMemeber = false;
        }else{
            id = userIdList.get(0);
        }


        if (isMemeber == true) { //회원일 떄
            jwtToken = jwtService.createJwt(id);
        }else{ //회원이 아닐 때
            jwtToken = "";
        }


        postLoginRes = new PostLoginRes(isMemeber, jwtToken);

        return postLoginRes;

    }
}
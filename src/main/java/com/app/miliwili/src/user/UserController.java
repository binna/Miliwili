package com.app.miliwili.src.user;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.config.BaseResponse;
import com.app.miliwili.src.user.models.PostLoginRes;
import com.app.miliwili.src.user.models.User;
import com.app.miliwili.utils.GoogleService;
import com.app.miliwili.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/app")
public class UserController {
    private final JwtService jwtService;
    private final GoogleService googleService;
    private final UserService userService;
    private final UserProvider userProvider;


    /**
     * JWT 검증 API
     * [GET]
     * @RequestHeader jwt
     * @return BaseResponse<Void>
     * @Auther shine
     */
    @GetMapping("/users/jwt")
    public BaseResponse<Void> jwt(@RequestHeader(value = "jwt") String jwt) {
        try {
            Long userId = jwtService.getUserId();
            // TODO 중복된 회원조회 필요 -> 회원가입 API 만든 후 만들 예정
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 로그인 --> 구글
     * [POST]
     * @RequestHeader token(google accessToken)
     * @return BaseResponse<PostLoginRes>
     * @Auther vivi
     */
    @ResponseBody
    @PostMapping("/users/login-google")
    public BaseResponse<PostLoginRes> postLoginGoogle(@RequestHeader("X-ACCESS-TOKEN") String token){

        try{
            String socialId = googleService.userIdFromGoogle(token);

            PostLoginRes returnRes = userService.createGoogleJwtToken(socialId);
            return new BaseResponse<>(SUCCESS,returnRes);
        }catch(BaseException e){
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }
    }

}
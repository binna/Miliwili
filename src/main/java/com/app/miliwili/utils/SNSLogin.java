package com.app.miliwili.utils;

import com.app.miliwili.config.BaseException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import static com.app.miliwili.config.BaseResponseStatus.*;

@Service
public class SNSLogin {

    public String userIdFromGoogle(String token) throws BaseException {

        String userId = "";
        BufferedReader in = null;
        InputStream is = null;
        InputStreamReader isr = null;
        JsonParser jsonParser = new JsonParser();
        JsonParser parser = new JsonParser();
        String socialId = token.replaceAll("\"", "");

        try {
            String reqURL = "https://oauth2.googleapis.com/tokeninfo?id_token=" + socialId;
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            System.out.println("?");

            try {
                is = conn.getInputStream();
                isr = new InputStreamReader(is, "UTF-8");
                in = new BufferedReader(isr);
            } catch (Exception e) {
                throw new BaseException(INVALID_TOKEN);
            }
            System.out.println("??");

            int responseCode = conn.getResponseCode();
            JsonObject jsonObj = (JsonObject) jsonParser.parse(in);

            userId = (jsonObj.get("sub").toString());
            if (responseCode != 200) {
                throw new BaseException(INVALID_TOKEN);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return userId;
    }

    /**
     * 카카오 토큰을 이용한 회원아이디 추출
     *
     * @param String token
     * @return String
     * @throws BaseException
     * @Auther shine
     */
    public String getUserIdFromKakao(String token) throws BaseException {
        JsonParser jsonParser = new JsonParser();
        JsonObject responseJsonObj = null;
        String kakaoUserId = "";

        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://kapi.kakao.com/v1/user/access_token_info")
                    .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
                    .build();
            Response response = client.newCall(request).execute();
            responseJsonObj = (JsonObject) jsonParser.parse(response.body().string());
        } catch (Exception exception) {
            throw new BaseException(KAKAO_CONNECTION);
        }

        try {
            kakaoUserId = responseJsonObj.get("id").toString();
        } catch (Exception exception1) {
            checkKakaoResError(responseJsonObj);
        }
        return kakaoUserId;
    }

    /**
     * 카카오 토큰을 프로필 사진 가져오기
     *
     * @param String token
     * @return String
     * @throws BaseException
     * @Auther shine
     */
    public String getProfileImgFromKakao(String token) throws BaseException {
        JsonParser jsonParser = new JsonParser();
        JsonObject responseJsonObj = null;
        String profileImageURL = "";

        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://kapi.kakao.com/v1/api/talk/profile")
                    .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
                    .build();
            Response response = client.newCall(request).execute();
            responseJsonObj = (JsonObject) jsonParser.parse(Objects.requireNonNull(response.body()).string());
        } catch (Exception exception) {
            throw new BaseException(KAKAO_CONNECTION);
        }

        try {
            profileImageURL = responseJsonObj.get("profileImageURL").toString();
        } catch (Exception exception1) {
            checkKakaoResError(responseJsonObj);
        }
        return profileImageURL.replaceAll("\"", "");
    }

    private void checkKakaoResError(JsonObject responseJsonObj) throws BaseException {
        if (responseJsonObj.get("code").toString().equals("-1")) {
            throw new BaseException(KAKAO_CONNECTION_1);
        } else if (responseJsonObj.get("code").toString().equals("-2")) {
            throw new BaseException(KAKAO_CONNECTION_2);
        } else if (responseJsonObj.get("code").toString().equals("-401")) {
            throw new BaseException(KAKAO_CONNECTION_401);
        } else {
            throw new BaseException(KAKAO_CONNECTION_ETC);
        }
    }
}
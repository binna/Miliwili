package com.app.miliwili.utils;

import com.app.miliwili.src.calendar.models.FcmMessageReq;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FirebaseCloudMessage {
    private final String API_URL = "https://fcm.googleapis.com/v1/projects/milliewillie-dev/messages:send";
    private final ObjectMapper objectMapper;

    /**
     * FCM 메시지 보내기
     * @param  String targetToken, String title, String body
     * @return IOException
     * @Auther shine
     */
    public void sendMessageTo(String targetToken, String title, String body) throws IOException {
        String message = makeMessage(targetToken, title, body);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody
                .create(message, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; utf-8")
                .build();

        Response response = client.newCall(request).execute();

        System.out.println(request.toString());
        System.out.println(message);
    }

    /**
     * 파이어베이스 FCM 메시지 생성
     * @param String targetToken, String title, String body
     * @return String
     * @throws JsonProcessingException
     * @Auther shine
     */
    private String makeMessage(String targetToken, String title, String body) throws JsonProcessingException {
        FcmMessageReq fcmMessage = FcmMessageReq.builder()
                .validate_only(false)
                .message(FcmMessageReq.Message.builder()
                        .token(targetToken)
                        .data(FcmMessageReq.Data.builder()
                                .title(title)
                                .body(body)
                                .build()
                        ).build()
                ).build();

        return objectMapper.writeValueAsString(fcmMessage);
    }

    /**
     * 파이어베이스 접근 토큰 생성
     * @return String
     * @throws IOException
     * @Auther shine
     */
    public String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/firebaseServiceKey.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }
}
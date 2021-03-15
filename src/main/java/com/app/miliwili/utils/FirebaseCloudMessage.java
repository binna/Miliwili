package com.app.miliwili.utils;

import com.app.miliwili.src.calendar.dto.FcmMessageReq;
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

@RequiredArgsConstructor
@Component
public class FirebaseCloudMessage {
    private final String API_URL = "https://fcm.googleapis.com/v1/projects/milliewillie-dev/messages:send";
    private final ObjectMapper objectMapper;

    /**
     * FCM 메시지 보내기
     * @param  String deviceToken, String title, String body
     * @return IOException
     * @Auther shine
     */
    public void sendMessageTo(String deviceToken, String title, String body) throws IOException {
        String message = makeMessage(deviceToken, title, body);

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
    }

    private String makeMessage(String deviceToken, String title, String body) throws JsonProcessingException {
        FcmMessageReq fcmMessage = FcmMessageReq.builder()
                .validate_only(false)
                .message(FcmMessageReq.Message.builder()
                        .token(deviceToken)
                        .notification(FcmMessageReq.Notification.builder()
                                .title(title)
                                .body(body)
                                .build()
                        ).build()
                ).build();

        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/firebaseServiceKey.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }
}
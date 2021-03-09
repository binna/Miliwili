package com.app.miliwili.utils;

import com.app.miliwili.config.BaseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;



@Service
public class GoogleService {


    public long userIdFromGoogle(String token) throws BaseException{
        String access_Token = token;

        long userId = 0;

        String reqURL = "https://www.googleapis.com/oauth2/v2/userinfo";


            HttpClient client = HttpClientBuilder.create().build();
            HttpGet get = new HttpGet(reqURL);

            JsonNode returnNode = null;

            get.addHeader("Authorization", "Bearer "+token);

            try{
                final HttpResponse response = client.execute(get);
                final int responseCode = response.getStatusLine().getStatusCode();

                ObjectMapper mapper = new ObjectMapper();
                returnNode = mapper.readTree(response.getEntity().getContent());

                System.out.println("\nSending 'GET' request to URL : " + reqURL);
                System.out.println("Response Code : " + responseCode);
           // BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            System.out.println(returnNode);

        }catch (IOException e){
            e.printStackTrace();
        }

        return userId;
    }

}

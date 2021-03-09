package com.app.miliwili.utils;

import com.app.miliwili.config.BaseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.stereotype.Service;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


@Service
public class GoogleService {


    public String userIdFromGoogle(String token) throws BaseException{

        String userId="";
        BufferedReader in  = null;
        InputStream is = null;
        InputStreamReader isr = null;
       JsonParser jsonParser = new JsonParser();

        String reqURL = "https://oauth2.googleapis.com/tokeninfo?id_token="+token;
        try{
            URL url = new URL(reqURL);
            JsonParser parser = new JsonParser();


            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
             is = conn.getInputStream();
             isr = new InputStreamReader(is, "UTF-8");
             in = new BufferedReader(isr);
             int responseCode = conn.getResponseCode();
             JsonObject jsonObj = (JsonObject)jsonParser.parse(in);

             userId = (jsonObj.get("sub").toString());


             System.out.println(responseCode);
             System.out.println(userId);

        }catch (IOException e){
            e.printStackTrace();
        }

        return userId;
    }

}

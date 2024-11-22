package com.karoldm.shortUrl;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main  implements RequestHandler<Map<String, Object>, Map<String, String>> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final S3Client s3Client = S3Client.builder().build();

    @Override
    public Map<String, String> handleRequest(Map<String, Object> stringObjectMap, Context context) {

        Map<String, String> bodyMap;
        try {
            String body = stringObjectMap.get("body").toString();
            bodyMap = objectMapper.readValue(body, Map.class);
        } catch(JsonProcessingException ex){
            throw new RuntimeException("Error parsing JSON body: " + ex);
        }

        String originalUrl = bodyMap.get("originalUrl");
        String expirationTime = bodyMap.get("expirationTime");
        long expirationTimeSeconds = Long.parseLong(expirationTime);

        String shortUrlCode = UUID.randomUUID().toString().substring(0,8);

        UrlData urlData = new UrlData(originalUrl, expirationTimeSeconds);

        try {
            String urlDataJson = objectMapper.writeValueAsString(urlData);
            PutObjectRequest request = PutObjectRequest.
                    builder().
                    bucket("karoldm-short-url-project").
                    key(shortUrlCode+".json").
                    build();
            s3Client.putObject(request, RequestBody.fromString(urlDataJson));
        }catch(Exception ex){
            throw new RuntimeException("Error saving URL to S3: " + ex.getMessage(), ex);
        }

        Map<String, String> response = new HashMap<>();
        response.put("code", shortUrlCode);

        return response;
    }
}
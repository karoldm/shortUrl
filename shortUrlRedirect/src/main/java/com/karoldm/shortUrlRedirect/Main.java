package com.karoldm.shortUrlRedirect;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Main implements RequestHandler<Map<String, Object>, Map<String, Object>> {
    private final S3Client s3Client = S3Client.builder().build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        // rawPath return: /path-of-url
        String pathParameters = input.get("rawPath").toString();
        String shortUrlCode = pathParameters.replace("/", "");

        if(shortUrlCode == null || shortUrlCode.isEmpty()){
            throw new IllegalArgumentException("Invalid input: 'shortUrlCode' is required!");
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.
                builder().
                bucket("karoldm-short-url-project").
                key(shortUrlCode+".json").
                build();

        InputStream s3ObjectStream;

        try {
            s3ObjectStream = s3Client.getObject(getObjectRequest);
        }catch(Exception exception){
            throw new RuntimeException("Error fetching data from S3: " + exception.getMessage());
        }

        UrlData urlData;

        try {
            urlData = objectMapper.readValue(s3ObjectStream, UrlData.class);
        } catch(Exception exception) {
            throw new RuntimeException("Error deserializing data: " + exception.getMessage());
        }

        Map<String, Object> response = new HashMap<>();

        // URL expired
        long currentTimeInSeconds = System.currentTimeMillis() / 1000;
        if(currentTimeInSeconds > urlData.getExpirationTime()){
            response.put("statusCode", 410);
            response.put("body", "This URL has expired!");
            return response;
        }

        response.put("statusCode", 302);
        Map<String, String> headers = new HashMap<>();
        headers.put("Location", urlData.getOriginalUrl());
        response.put("headers", headers);

        return response;
    }
}
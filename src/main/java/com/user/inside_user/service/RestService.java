package com.user.inside_user.service;

import com.user.inside_user.Request.JournalRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestService {

    @Value("${journal.service.url}")
    private String journalServiceUrl;

    @Autowired
    private RestTemplate restTemplate;

    public String getDataFromOtherService(String param) {
        String url = journalServiceUrl;
        // Create a new JournalRequest with "Login" as the event
        JournalRequest journalRequest = new JournalRequest(param);

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<JournalRequest> requestEntity = new HttpEntity<>(journalRequest, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        // Process and return the response body
        return response.getBody();
    }
}

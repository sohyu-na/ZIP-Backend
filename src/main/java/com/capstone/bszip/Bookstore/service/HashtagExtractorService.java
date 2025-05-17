package com.capstone.bszip.Bookstore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class HashtagExtractorService {

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final RestTemplate restTemplate = new RestTemplate();
    @Value("${openai.api.key}")
    private String API_KEY;

    public String extractHashtag(String description) {
        System.out.println(description);
        String prompt = String.format(
                "아래 서점 설명을 참고해서, 이전에 추출한 해시태그와 중복되지 않는, 새로운 해시태그 한 개만 추출해줘. " +
                        "북스테이,북카페,서적 은 따로 저장하고 있으니 제외해줘  " +
                        "설명: \"%s\" 결과는 해시태그 한 개만 반환해줘. 예시: #고양이", description);

        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("model", "gpt-4");

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt); // 이스케이프 자동 처리

            messages.add(message);
            requestMap.put("messages", messages);

            String requestBody = mapper.writeValueAsString(requestMap);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(API_KEY);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(OPENAI_API_URL, HttpMethod.POST, entity, String.class);
            String content = response.getBody();
            System.out.println(content);
            Pattern pattern = Pattern.compile("#[\\w가-힣]+");
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                return matcher.group();
            }
            return null;
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}


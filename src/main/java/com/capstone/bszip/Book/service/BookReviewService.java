package com.capstone.bszip.Book.service;

import com.capstone.bszip.Book.dto.BookSearchResponse;
import com.capstone.bszip.Book.repository.BookRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;


@Service
public class BookReviewService {
    private final BookRepository bookRepository;
    private final ObjectMapper objectMapper;

    @Value("${kakao.client.id}")
    private String kakaoApiKey;

    public BookReviewService(BookRepository bookRepository, ObjectMapper objectMapper) {
        this.bookRepository = bookRepository;
        this.objectMapper = objectMapper;
    }

    // kakao book api로 검색된 책 정보 json 가져오기
    public String searchBooksByTitle(String title) throws Exception {
        try{
            String kakaoUri = "https://dapi.kakao.com/v3/search/book";
            // http 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "KakaoAK " + kakaoApiKey);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            // 쿼리 파라미터 설정
            UriComponents uri = UriComponentsBuilder.fromHttpUrl(kakaoUri)
                    .queryParam("query", title)
                    .queryParam("target", "title")
                    .build();

            // kakao api 책 검색
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    uri.toString(),
                    HttpMethod.GET,
                    entity,
                    String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            // 4xx 클라이언트 오류
            throw new Exception("클라이언트 오류: " + e.getStatusCode() + " - " + e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            // 5xx 서버 오류
            throw new Exception("서버 오류: " + e.getStatusCode() + " - " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            // 네트워크 오류 (타임아웃, 연결 불가 등)
            throw new Exception("네트워크 오류: " + e.getMessage(), e);
        } catch (RestClientException e) {
            // 기타 RestTemplate 관련 예외
            throw new Exception("API 요청 실패: " + e.getMessage(), e);
        }
    }
    // 역직렬화 - gson으로 간단하게 구현했으나 성능 향상을 위해 추후에 Jackson으로 바꾸기
    public List<BookSearchResponse> convertToBookSearchResponse(String bookJson){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(bookJson);
            JsonNode documents = rootNode.path("documents");

            List<BookSearchResponse> bookSearchResponses = new ArrayList<>();

            for (JsonNode document : documents) {
                String title = document.path("title").asText();
                List<String> authorsList = new ArrayList<>();

                for (JsonNode authorNode : document.path("authors")) {
                    authorsList.add(authorNode.asText());
                }

                String publisher = document.path("publisher").asText();
                String [] isbns = document.path("isbn").asText().split(" ");
                String isbn = isbns[1];
                String thumbnail = document.path("thumbnail").asText();

                bookSearchResponses.add(new BookSearchResponse(title, authorsList, publisher, isbn, thumbnail));
            }
            return bookSearchResponses;
        } catch (Exception e) {
            throw new RuntimeException("JSON 변환 오류: " + e.getMessage(), e);
        }
    }


}

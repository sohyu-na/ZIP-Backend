package com.capstone.bszip.Book.service;

import com.capstone.bszip.Book.dto.AddIsEndBookResponse;
import com.capstone.bszip.Book.dto.BookSearchResponse;
import com.capstone.bszip.Book.repository.BookRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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


@Slf4j
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

    // kakao book api에서 책 제목로 검색된 책 정보 json 가져오기 -> 책제목 검색이랑 작가 검색이랑 너무 공통되는 부분이 많아서 걍 통일시켜야 될 거 같으다...
    public String searchBooksByTitle(String title, int page) throws Exception {
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
                    .queryParam("page", page)
                    .queryParam("size", 12)
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

    public String searchBooksByAuthor(String author, int page) throws Exception {
        try{
            String kakaoUri = "https://dapi.kakao.com/v3/search/book";
            // http 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "KakaoAK " + kakaoApiKey);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            // 쿼리 파라미터 설정
            UriComponents uri = UriComponentsBuilder.fromHttpUrl(kakaoUri)
                    .queryParam("query", author)
                    .queryParam("target", "person")
                    .queryParam("page", page)
                    .queryParam("size", 12)
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

    /*
    * 지금 문제 query만 생각해서 제목이나 작가로 검색할 때 query가 같아도 증가됨...
    * 그리고 지금은 유저가 한명이지만,, 여러명의 유저가 같은 걸 검색하면 어카냐...
    * 그냥 url을 [더보기]랑 / [처음 검색]이랑 분리하는 게 나을 듯....
    * */

    // 역직렬화 - Jackson
    public AddIsEndBookResponse convertToBookSearchResponse(String bookJson){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(bookJson);
            // 페이지가 끝인지 아닌지 확인하기 위해서...
            JsonNode meta = rootNode.get("meta");
            boolean is_end = meta.get("is_end").asBoolean(); // is_end가 true면 더 이상받을 검색 결과가 없닷
            JsonNode documents = rootNode.path("documents");

            List<BookSearchResponse> bookSearchResponses = new ArrayList<>();
            String title = "";
            for (JsonNode document : documents) {
                title = document.path("title").asText();
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
            AddIsEndBookResponse addIsEndBookResponse = new AddIsEndBookResponse();
            addIsEndBookResponse.setBookData(bookSearchResponses);
            addIsEndBookResponse.setIsEnd(is_end);
            return addIsEndBookResponse;
        } catch (Exception e) {
            throw new RuntimeException("JSON 변환 오류: " + e.getMessage(), e);
        }
    }



}

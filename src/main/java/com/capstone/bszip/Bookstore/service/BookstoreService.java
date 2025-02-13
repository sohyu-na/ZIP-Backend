package com.capstone.bszip.Bookstore.service;

import com.capstone.bszip.Bookstore.service.dto.BookstoreResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookstoreService {
    private final RestTemplate restTemplate= new RestTemplate();

    private static final String API_URL = "http://api.kcisa.kr/openapi/API_CIA_090/request"; //open api url
    @Value("${bookstore.cafe.key}")
    private String BOOKSTORE_CAFE_KEY;

    public List<BookstoreResponse> searchBookstores(String keyword) {
        String url = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("serviceKey", BOOKSTORE_CAFE_KEY)
                .queryParam("numOfRows", 10)
                .queryParam("pageNo", 1)
                .queryParam("keyword", keyword)
                .build()
                .toString();
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        List<BookstoreResponse> bookstoreList = new ArrayList<>();

        if (response != null ) {
            Map<String, Object> responseData = (Map<String, Object>) response.get("response");
            Map<String, Object> body = (Map<String, Object>) responseData.get("body");
            Map<String, Object> items = (Map<String, Object>) body.get("items");
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) items.get("item");

            for (Map<String, Object> item : dataList) {
                BookstoreResponse bookstoreResponse = new BookstoreResponse();
                bookstoreResponse.setName((String) item.get("TITLE"));
                // bookstoreResponse.setRating(); // 별점은 따로 계산해야함
                bookstoreResponse.setCategory("카페가 있는 서점");
                bookstoreResponse.setPhone((String) item.get("CONTACT_POINT"));
                bookstoreResponse.setHours((String) item.get("DESCRIPTION"));
                bookstoreResponse.setAddress((String) item.get("ADDRESS"));
                bookstoreResponse.setDescription((String) item.get("SUB_DESCRIPTION"));
                bookstoreList.add(bookstoreResponse);
            }
        }
        return bookstoreList; //서점 검색 결과 리스트 반환
    }
}

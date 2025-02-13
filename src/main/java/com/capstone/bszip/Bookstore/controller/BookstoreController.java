package com.capstone.bszip.Bookstore.controller;

import com.capstone.bszip.Bookstore.service.BookstoreService;
import com.capstone.bszip.Bookstore.service.dto.BookstoreResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/* 프론트
const response = await fetch(`/api/bookstores/search?keyword=${searchWord}`);
    const data = await response.json();

    setSearchResults(data); // 검색 결과 업데이트
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookstores")
@Tag(name = "Bookstore", description = "서점 관리 API")
public class BookstoreController {
    private final BookstoreService bookstoreService;
    @GetMapping("/search")
    public List<BookstoreResponse>  searchBookstores(@RequestParam String keyword) {
        return bookstoreService.searchBookstores(keyword);
    }
}

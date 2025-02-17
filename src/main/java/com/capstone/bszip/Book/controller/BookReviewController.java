package com.capstone.bszip.Book.controller;

import com.capstone.bszip.Book.domain.Book;
import com.capstone.bszip.Book.domain.BookReview;
import com.capstone.bszip.Book.dto.AddIsEndBookResponse;
import com.capstone.bszip.Book.dto.BookReviewRequest;
import com.capstone.bszip.Book.dto.BookSearchResponse;
import com.capstone.bszip.Book.service.BookReviewService;
import com.capstone.bszip.Member.domain.Member;
import com.capstone.bszip.commonDto.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/booksnap")
@RequiredArgsConstructor
@Tag(name="Book Review", description = "Booksnap 기능에서 한 줄 리뷰 관련 api")
public class BookReviewController {

    private final BookReviewService bookReviewService;
    /*
    * 제목으로 도서 검색 api
    * - 책 ID, 이미지 url, 책 제목, 작가,출판사 제공*/
    @GetMapping("/book-search")
    @Operation(summary = "책 검색", description = "책 제목을 입력하여 책제목, 작가, 출판사, isbn, 책 표지 url을 검색하여 볼러옵니다.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "검색 성공", content = @Content(schema = @Schema(implementation = BookSearchResponse.class),
        examples = {@ExampleObject(
                name = "Success example : 모순 검색 시",
                value = "{\n" +
                        "    \"result\": true,\n" +
                        "    \"status\": 200,\n" +
                        "    \"message\": \"검색 성공\",\n" +
                        "    \"data\": [\n" +
                        "        {\n" +
                        "            \"title\": \"모순\",\n" +
                        "            \"authors\": [\n" +
                        "                \"양귀자\"\n" +
                        "            ],\n" +
                        "            \"publisher\": \"쓰다\",\n" +
                        "            \"isbn\": \"8998441012\",\n" +
                        "            \"bookImageUrl\": \"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1500252%3Ftimestamp%3D20240802114042\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"title\": \"모순(리커버:K)\",\n" +
                        "            \"authors\": [\n" +
                        "                \"양귀자\"\n" +
                        "            ],\n" +
                        "            \"publisher\": \"쓰다\",\n" +
                        "            \"isbn\": \"8998441101\",\n" +
                        "            \"bookImageUrl\": \"https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F5697894%3Ftimestamp%3D20220410173043\"\n" +
                        "        }" +
                        "]" +
                        "}"
        )})),})
    public ResponseEntity<?> searchBookByTitle(@RequestParam String query, @RequestParam(required = false, defaultValue = "1")int page) {
        try{
            String bookJson = bookReviewService.searchBooksByTitle(query, page);
            AddIsEndBookResponse addIsEndBookResponse = bookReviewService.convertToBookSearchResponse(bookJson);
            return ResponseEntity.ok(
                    SuccessResponse.builder()
                            .result(true)
                            .status(HttpServletResponse.SC_OK)
                            .data(addIsEndBookResponse) // 현재 페이지가 끝인지 확인할 수 있는 것도 추가해야 될듯..!
                            .message("검색 성공")
                            .build()
            );
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /*
     * 작가로 도서 검색 api
     * - 책 ID, 이미지 url, 책 제목, 작가,출판사 제공*/
    @GetMapping("/book-search-by-author")
    public ResponseEntity<?> searchBookByAuthor(@RequestParam String query, @RequestParam(required = false, defaultValue = "1")int page) {
        try{
            String bookJson = bookReviewService.searchBooksByAuthor(query, page);
            AddIsEndBookResponse addIsEndBookResponse = bookReviewService.convertToBookSearchResponse(bookJson);
            return ResponseEntity.ok(
                    SuccessResponse.builder()
                            .result(true)
                            .status(HttpServletResponse.SC_OK)
                            .data(addIsEndBookResponse)
                            .message("검색 성공")
                            .build()
            );
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /*
    * 리뷰 작성 api
    * - 책 ID, 별점, 리뷰 받아서 저장*/
    @PostMapping("/new-review")
    public ResponseEntity<?> writeBookReview(Authentication authentication, @RequestBody BookReviewRequest bookReviewRequest) {
        try{
            Member member = (Member) authentication.getPrincipal(); // 맴버객체 가져옴
            // 지금 책이 디비에 없으면 저장하기

            Long isbn = bookReviewRequest.getIsbn();
            log.info(isbn.toString());
            if(!bookReviewService.existsByIsbn(isbn)){ // db에 해당 책이 저장되어 있지 않은 경우
                String bookJson = bookReviewService.searchBookByIsbn(isbn); //isbn으로 카카오에서 전체 json 받아옴
                Book book = bookReviewService.makeBook(bookJson); // 데이터 가공해서 book 객체로 얻어옴
                bookReviewService.saveBook(book); // 북에 저장
            }
            // 책 리뷰 저장
            Book book = bookReviewService.getBookByIsbn(isbn); // 책 객체 가져오기
            bookReviewService.saveBookReview(new BookReview(bookReviewRequest.getReviewText(), bookReviewRequest.getRating(), book, member));
            return ResponseEntity.ok(
                    SuccessResponse.builder()
                            .result(true)
                            .status(HttpServletResponse.SC_OK)
                            .data(null)
                            .message("리뷰 저장 성공")
                            .build()
            );
        }catch (Exception e){
            throw new RuntimeException("Internal Error: " + e);
        }
    }

    /*
    * 리뷰 보이기 api
    * - 최신순, 좋아요, 요즘 인기 있는
    * 추후 추가 */

    /*
    * 책 담기 api
    * 추후 추가*/

    /*
    * 책 좋아요 api
    * 추후 추가*/
}

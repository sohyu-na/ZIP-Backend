package com.capstone.bszip.Book.controller;

import com.capstone.bszip.Book.domain.Book;
import com.capstone.bszip.Book.dto.MyPickBooksResponse;
import com.capstone.bszip.Book.dto.PickedBookRequest;
import com.capstone.bszip.Book.service.BookReviewService;
import com.capstone.bszip.Book.service.PickedBookService;
import com.capstone.bszip.Member.domain.Member;
import com.capstone.bszip.commonDto.ErrorResponse;
import com.capstone.bszip.commonDto.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name="책 담기", description = "책 담기 관련 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pick-book")
public class PickedBookController {
    private final PickedBookService pickedBookService;
    private final BookReviewService bookReviewService;

    @Operation(summary = "책 담기 ", description = """
            [로그인 필수] 책 담기 API
            """)
    @PostMapping
    public ResponseEntity<?> createPickedBook(@AuthenticationPrincipal Member member,
                                           @RequestBody PickedBookRequest pickedBookRequest) {
        try{
            if(member == null){
                return ResponseEntity.status(401).body(
                        ErrorResponse.builder()
                                .message("로그인 후 이용해주세요.")
                                .build()
                );
            }
            Long bookId = Long.parseLong(pickedBookRequest.getBookId());
            Book book = bookReviewService.getBookById(bookId);
            if (book == null) {
                return ResponseEntity.status(404).body(
                        ErrorResponse.builder()
                                .message("책을 찾을 수 없어요!🥲")
                                .build()
                );
            }

            if(pickedBookService.existsPickedBook(book, member)){
                return ResponseEntity.status(409).body(
                        ErrorResponse.builder()
                                .message("이미 담은 책이에요!🥲")
                                .build()
                );
            }
            pickedBookService.savePickedBook(book, member);
            return ResponseEntity.status(201).body(
                    SuccessResponse.builder()
                            .result(true)
                            .status(200)
                            .message("책 담기 완료!")
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
    @Operation(summary = "책 담기 취소 ", description = """
            [로그인 필수] 책 담기 취소 API
            """)
    @DeleteMapping
    public ResponseEntity<?> deletePickedBook(@AuthenticationPrincipal Member member,
                                              @RequestBody PickedBookRequest pickedBookRequest) {
        try{
            Book book = bookReviewService.getBookById(Long.parseLong(pickedBookRequest.getBookId()));
            if (book == null) {
                return ResponseEntity.status(404).body(
                        ErrorResponse.builder()
                                .message("담은 적이 없는 책입니다!😅")
                );
            }
            pickedBookService.deletePickedBook(book, member);
            return ResponseEntity.ok(
                    SuccessResponse.builder()
                            .message("책 담기가 취소되었습니다!")
                            .build()
            );
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @GetMapping
    @Operation(summary = "책 담기 조회 ", description = """
            [로그인 필수] 책 담기 조회 API
            """)
    public ResponseEntity<MyPickBooksResponse> getMyPickedBooks(@AuthenticationPrincipal Member member) {
        MyPickBooksResponse pickBooks = pickedBookService.getMyPickedBooks(member);
        return ResponseEntity.ok(pickBooks);
    }
}

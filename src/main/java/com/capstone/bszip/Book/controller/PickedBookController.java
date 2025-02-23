package com.capstone.bszip.Book.controller;

import com.capstone.bszip.Book.domain.Book;
import com.capstone.bszip.Book.dto.PickedBookRequest;
import com.capstone.bszip.Book.service.BookReviewService;
import com.capstone.bszip.Book.service.PickedBookService;
import com.capstone.bszip.Member.domain.Member;
import com.capstone.bszip.commonDto.ErrorResponse;
import com.capstone.bszip.commonDto.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pick-book")
public class PickedBookController {
    private final PickedBookService pickedBookService;
    private final BookReviewService bookReviewService;

    public PickedBookController(PickedBookService pickedBookService, BookReviewService bookReviewService) {
        this.pickedBookService = pickedBookService;
        this.bookReviewService = bookReviewService;
    }

    @PostMapping
    public ResponseEntity<?> createPickedBook(@AuthenticationPrincipal Member member,
                                           @RequestBody PickedBookRequest pickedBookRequest) {
        try{
            Long isbn = pickedBookRequest.getIsbn();
            Book book = bookReviewService.getBookByIsbn(isbn);
            if (book == null) {
                return ResponseEntity.status(404).body(
                        ErrorResponse.builder()
                                .message("책을 찾을 수 없어요!🥲")
                                .build()
                );
            }

            if(!pickedBookService.existsPikedBook(book, member)){
                return ResponseEntity.status(409).body(
                        ErrorResponse.builder()
                                .message("이미 담은 책이에요!🥲")
                                .build()
                );
            }
            pickedBookService.savePikedBook(book, member);
            return ResponseEntity.status(201).body(
                    SuccessResponse.builder()
                            .message("책 담기 완료!")
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    @DeleteMapping
    public ResponseEntity<?> deletePickedBook(@AuthenticationPrincipal Member member,
                                              @RequestBody PickedBookRequest pickedBookRequest) {
        try{
            Book book = bookReviewService.getBookByIsbn(pickedBookRequest.getIsbn());
            if (book == null) {
                return ResponseEntity.status(404).body(
                        ErrorResponse.builder()
                                .message("담은 적이 없는 책입니다!😅")
                );
            }
            pickedBookService.deletePikedBook(book, member);
            return ResponseEntity.ok(
                    SuccessResponse.builder()
                            .message("책 담기가 취소되었습니다!")
                            .build()
            );
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}

package com.capstone.bszip.Book.controller;

import com.capstone.bszip.Book.domain.BookReview;
import com.capstone.bszip.Book.domain.BookReviewLikes;
import com.capstone.bszip.Book.dto.BookReviewLikeRequest;
import com.capstone.bszip.Book.service.BookReviewLikeService;
import com.capstone.bszip.Book.service.BookReviewService;
import com.capstone.bszip.Member.domain.Member;
import com.capstone.bszip.commonDto.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name="Book Review Like", description = "책 한 줄 리뷰의 좋아요 관련 기능 api")
@RequestMapping("/api")
public class BookReviewLikeController {
    private final BookReviewLikeService bookReviewLikeService;
    private final BookReviewService bookReviewService;

    public BookReviewLikeController(BookReviewLikeService bookReviewLikeService, BookReviewService bookReviewService) {
        this.bookReviewLikeService = bookReviewLikeService;
        this.bookReviewService = bookReviewService;
    }

    /*
    * 책 리뷰에 좋아요 누르기 (로그인 필수)
    * */
    @Operation(summary = "책 한 줄 리뷰에 좋아요", description = "[로그인 필수] 책 리뷰 아이디를 넘겨주어야 합니다!")
    @PostMapping("/booksnap/like")
    public ResponseEntity<?> likeBookReview(Authentication authentication, @RequestBody BookReviewLikeRequest bookReviewLikeRequest){

        try{
            // 멤버 가지고 오기
            Member member = (Member) authentication.getPrincipal();
            // 해당 책 한 줄 리뷰 가지고 오기
            Long bookReviewId = bookReviewLikeRequest.getBookReviewId();
            BookReview bookReview = bookReviewService.getBookReviewById(bookReviewId);
            if(bookReviewLikeService.isAleadyLiked(bookReview, member)){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        member.getNickname()+"님이 이미 좋아요하셨습니다...😅"
                );
            }
            // 좋아요 객체 만들기
            BookReviewLikes bookReviewLikes = BookReviewLikes.create(bookReview, member);
            // 리뷰 저장하기
            bookReviewLikeService.saveLike(bookReviewLikes);
            return ResponseEntity.ok(
                    SuccessResponse.builder()
                            .result(true)
                            .status(HttpServletResponse.SC_OK)
                            .data(null)
                            .message(member.getNickname() + "의 좋아요 완료")
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

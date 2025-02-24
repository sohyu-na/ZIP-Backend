package com.capstone.bszip.Bookstore.controller;

import com.capstone.bszip.Bookstore.domain.Bookstore;
import com.capstone.bszip.Bookstore.domain.BookstoreCategory;
import com.capstone.bszip.Bookstore.service.BookstoreService;
import com.capstone.bszip.Bookstore.service.dto.BookstoreResponse;
import com.capstone.bszip.Member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookstores")
@Tag(name = "Bookstore", description = "서점 관리 API")
public class BookstoreController {

    private final BookstoreService bookstoreService;

    @Operation(summary = "서점 검색", description = "검색창에서 서점을 이름,주소로 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공",
                    content = @Content(schema = @Schema(implementation = Bookstore.class))),
            @ApiResponse(responseCode = "404", description = "검색 결과 없음",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<?> searchBookstores(@RequestParam String keyword, @AuthenticationPrincipal Member member) {
        try {
            List<BookstoreResponse> bookstores = bookstoreService.searchBookstores(keyword,member);
            if (bookstores.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("검색어에 해당하는 서점이 없습니다.");
            }
            return ResponseEntity.ok(bookstores);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("잘못된 검색어입니다: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류가 발생했습니다.");
        }
    }

    @Operation(
            summary = "카테고리별 서점 조회",
            description = "지정된 카테고리에 해당하는 서점 목록을 반환합니다. 카테고리가 지정되지 않으면 모든 서점을 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 서점 목록을 반환"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping
    public ResponseEntity<?> getBookstoresByCategory(
            @Parameter(description = "조회할 서점 카테고리 (선택사항)") @RequestParam(required = false) BookstoreCategory category,@AuthenticationPrincipal Member member){
        try{
            List<BookstoreResponse> bookstores = bookstoreService.getBookstoresByCategory(category,member);
            return ResponseEntity.ok(bookstores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류가 발생했습니다.");
        }
    }

    @Operation(
            summary = "서점 찜하기/찜 취소",
            description = "특정 서점에 대해 찜하기 또는 찜 취소를 수행합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 찜하기/찜 취소 수행"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/{bookstoreId}/toggle-like")
    public ResponseEntity<?> toggleLikeBookstore(@PathVariable Long bookstoreId, @AuthenticationPrincipal Member member){
        try {
            if (member == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("인증되지 않은 사용자입니다.");
            }
            Long memberId = member.getMemberId();
            bookstoreService.toggleLikeBookstore(memberId, bookstoreId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류가 발생했습니다.");
        }
    }

    @Operation(
            summary = "찜한 서점 목록 조회",
            description = "현재 로그인한 사용자가 찜한 서점 목록을 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 찜한 서점 목록 반환"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/liked")
    public ResponseEntity<?> getLikedBookstores(
            @Parameter(hidden = true) @AuthenticationPrincipal Member member
    ) {
        try {
            if (member == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("인증되지 않은 사용자입니다.");
            }
            List<BookstoreResponse> likedBookstores = bookstoreService.getLikedBookstores(member);
            return ResponseEntity.ok(likedBookstores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류가 발생했습니다.");
        }
    }
}

package com.capstone.bszip.Bookstore.controller;

import com.capstone.bszip.Book.dto.BookSearchResponse;
import com.capstone.bszip.Book.service.IndepBookService;
import com.capstone.bszip.Bookstore.domain.Bookstore;
import com.capstone.bszip.Bookstore.domain.BookstoreCategory;
import com.capstone.bszip.Bookstore.service.BookstoreReviewService;
import com.capstone.bszip.Bookstore.service.BookstoreService;
import com.capstone.bszip.Bookstore.service.dto.*;
import com.capstone.bszip.Member.domain.Member;
import com.capstone.bszip.commonDto.ErrorResponse;
import com.capstone.bszip.commonDto.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookstores")
@Tag(name = "Bookstore", description = "서점 관리 API")
public class BookstoreController {

    private final BookstoreService bookstoreService;
    private final BookstoreReviewService bookstoreReviewService;
    private final IndepBookService indepBookService;

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
    public ResponseEntity<?> searchBookstores(@RequestParam String keyword,
                                              @AuthenticationPrincipal Member member,
                                              @RequestParam double lat, @RequestParam double lng) {
        try {
            List<BookstoreResponse> bookstores = bookstoreService.searchBookstores(keyword, member, lat, lng);
            /*if (bookstores.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ErrorResponse.builder()
                                .result(false)
                                .status(HttpStatus.NOT_FOUND.value())
                                .message("검색 결과가 없습니다.")
                                .detail(keyword+" 에 해당하는 서점이 없습니다.")
                                .build());
            }*/
            return ResponseEntity.ok(SuccessResponse.<List<BookstoreResponse>>builder()
                    .result(true)
                    .status(HttpStatus.OK.value())
                    .message(keyword + " - 서점 검색 성공")
                    .data(bookstores)
                    .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.builder()
                            .result(false)
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("잘못된 검색어입니다.")
                            .detail(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.builder()
                            .result(false)
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("서버 오류가 발생했습니다.")
                            .detail(e.getMessage())
                            .build());
        }
    }

    @Operation(
            summary = "카테고리별 서점 조회",
            description = "지정된 카테고리에 해당하는 서점 목록을 반환합니다. 카테고리가 지정되지 않으면 모든 서점을 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 서점 목록을 반환"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping
    public ResponseEntity<?> getBookstoresByCategory(
            @Parameter(description = "조회할 서점 카테고리") @RequestParam(required = false) BookstoreCategory category,
            @AuthenticationPrincipal Member member,
            @RequestParam double lat, @RequestParam double lng) {
        try {
            List<BookstoreResponse> bookstores = bookstoreService.getBookstoresByCategory(category, member, lat, lng);
            return ResponseEntity.ok(SuccessResponse.<List<BookstoreResponse>>builder()
                    .result(true)
                    .status(HttpStatus.OK.value())
                    .message("카테고리별 서점 조회 성공")
                    .data(bookstores)
                    .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.builder()
                            .result(false)
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("잘못된 요청입니다.")
                            .detail(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.builder()
                            .result(false)
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("서버 오류가 발생했습니다.")
                            .detail(e.getMessage())
                            .build());
        }
    }

    @Operation(
            summary = "서점 찜하기/찜 취소",
            description = "[로그인 필수] 특정 서점에 대해 찜하기 또는 찜 취소를 수행합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 찜하기/찜 취소 수행"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/{bookstoreId}/toggle-like")
    public ResponseEntity<?> toggleLikeBookstore(@PathVariable Long bookstoreId, @AuthenticationPrincipal Member member) {
        try {
            if (member == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ErrorResponse.builder()
                                .result(false)
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .message("인증되지 않은 사용자입니다.")
                                .detail("로그인이 필요한 서비스입니다.")
                                .build());
            }
            Long memberId = member.getMemberId();
            bookstoreService.toggleLikeBookstore(memberId, bookstoreId);
            return ResponseEntity.ok(SuccessResponse.<Void>builder()
                    .result(true)
                    .status(HttpStatus.OK.value())
                    .message("서점 찜하기/찜 취소 성공 - 사용자: " + memberId + " 서점: " + bookstoreId)
                    .build());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.builder()
                            .result(false)
                            .status(HttpStatus.NOT_FOUND.value())
                            .message("해당 서점을 찾을 수 없습니다.")
                            .detail(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.builder()
                            .result(false)
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("서버 오류가 발생했습니다.")
                            .detail(e.getMessage())
                            .build());
        }
    }

    @Operation(
            summary = "찜한 서점 목록 조회",
            description = "[로그인 필수] 사용자가 찜한 서점 목록을 반환합니다."
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
            @RequestParam(required = false) BookstoreCategory category,
            @Parameter(hidden = true) @AuthenticationPrincipal Member member,
            @RequestParam double lat, @RequestParam double lng
    ) {
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ErrorResponse.builder()
                            .result(false)
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .message("인증되지 않은 사용자입니다.")
                            .detail("로그인이 필요한 서비스입니다.")
                            .build());
        }
        try {
            List<BookstoreResponse> likedBookstores;
            likedBookstores = bookstoreService.getLikedBookstoresByCategory(member, category, lat, lng);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("totalCnt", likedBookstores.size());
            responseData.put("bookstores", likedBookstores);

            return ResponseEntity.ok(SuccessResponse.<Map<String, Object>>builder()
                    .result(true)
                    .status(HttpStatus.OK.value())
                    .message("찜한 서점 목록 조회 성공")
                    .data(responseData)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.builder()
                            .result(false)
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("서버 오류가 발생했습니다.")
                            .detail(e.getMessage())
                            .build());
        }
    }

    @Operation(
            summary = "서점 상세 조회",
            description = "특정 서점의 상세 정보 및 리뷰 목록/보유 서적을 조회합니다."
    )
    @Parameters({
            @Parameter(name = "bookstoreId", description = "조회할 서점의 ID", required = true),
            @Parameter(name = "type", description = "조회할 데이터 타입 (reviews: 리뷰 목록, books: 보유 서적 목록)",
                    required = true, schema = @Schema(type = "string", allowableValues = {"reviews", "books"})),
            @Parameter(name = "sortField", description = "정렬 타입 (createdAt: 최신순, rating: 별점순)",
                    required = false, schema = @Schema(type = "string", allowableValues = {"createdAt", "rating"}))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "서점 상세 정보 및 리뷰/보유 서적 조회 성공",
                    content = {@Content(schema = @Schema(oneOf = {
                            BookstoreDetailWithReviews.class,
                            BookstoreDetailWithBooks.class
                    }))
                    }),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "서점을 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{bookstoreId}/details")
    public ResponseEntity<?> getBookstoreDetails(@PathVariable Long bookstoreId,
                                                 @RequestParam String type,
                                                 @RequestParam(required = false, defaultValue = "createdAt") String sortField,
                                                 @AuthenticationPrincipal Member member) {
        try {
            BookstoreDetailResponse bookstoreDetail = bookstoreService.getBookstoreDetail(member, bookstoreId);

            if("reviews".equals(type)) {
                Sort sort = Sort.by(Sort.Direction.DESC, sortField);
                List<BookstoreReviewResponse> reviewList = bookstoreReviewService.getReviewsByBookstoreId(bookstoreId,sort);
                return ResponseEntity.ok(SuccessResponse.<BookstoreDetailWithReviews>builder()
                        .result(true)
                        .status(HttpStatus.OK.value())
                        .message("서점 상세 정보 및 리뷰 조회 성공")
                        .data(new BookstoreDetailWithReviews(bookstoreDetail, reviewList))
                        .build());
            }else if("books".equals(type)) {
                List<BookSearchResponse.IndepBook> bookList = indepBookService.getIndepBookByBookstore(bookstoreId);
                return ResponseEntity.ok(SuccessResponse.<BookstoreDetailWithBooks>builder()
                        .result(true)
                        .status(HttpStatus.OK.value())
                        .message("서점 상세 정보 및 보유 서적 조회 성공")
                        .data(new BookstoreDetailWithBooks(bookstoreDetail, bookList))
                        .build());
            }else {
                throw new IllegalArgumentException("Invalid type parameter");
            }
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.builder()
                            .result(false)
                            .status(HttpStatus.NOT_FOUND.value())
                            .message("서점 id 오류")
                            .detail(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.builder()
                            .result(false)
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("서버 오류가 발생했습니다.")
                            .detail(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/trending")
    public ResponseEntity<?> getTrendingBookstores(){
        try {
            List<String> trendingBookstores = bookstoreService.getTrendingBookstoresNames();
            return ResponseEntity.ok(SuccessResponse.<List<String>>builder()
                    .result(true)
                    .status(HttpStatus.OK.value())
                    .message("급상승 서점 목록 조회 성공")
                    .data(trendingBookstores)
                    .build());

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.builder()
                            .result(false)
                            .status(HttpStatus.NOT_FOUND.value())
                            .message("급상승 서점 정보가 존재하지 않습니다")
                            .detail(e.getMessage())
                            .build());

        } catch (RedisConnectionFailureException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ErrorResponse.builder()
                            .result(false)
                            .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                            .message("서비스 일시적으로 이용할 수 없습니다")
                            .detail("Redis 연결 실패: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.builder()
                            .result(false)
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("서버 오류가 발생했습니다")
                            .detail(e.getMessage())
                            .build());
        }

    }
}

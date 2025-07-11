package com.capstone.bszip.Bookstore.controller;

import com.capstone.bszip.Book.dto.BookSearchResponse;
import com.capstone.bszip.Book.service.IndepBookService;
import com.capstone.bszip.Bookstore.service.BookstoreReviewService;
import com.capstone.bszip.Bookstore.service.BookstoreService;
import com.capstone.bszip.Bookstore.service.HashtagService;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookstores")
@Tag(name = "Bookstore", description = "서점 관리 API")
public class BookstoreController {

    private final BookstoreService bookstoreService;
    private final BookstoreReviewService bookstoreReviewService;
    private final HashtagService hashtagService;
    private final IndepBookService indepBookService;

    @Operation(
            summary = "서점 검색",
            description = "키워드, 서점 ID 목록, 지역, 정렬 방식, 위치 정보(위도/경도)를 기반으로 서점을 검색합니다."
    )
    @Parameters({
            @Parameter(name = "searchK", description = "검색 키워드(서점명,주소)",
                    required = false, schema = @Schema(type = "string")),
            @Parameter(name = "region", description = "필터 - 지역명",
                    required = false, schema = @Schema(type = "string")),
            @Parameter(name = "sortField", description = "정렬 기준 (distance: 거리순, rating: 평점순 ,likes : 찜한순)",
                    required = false, schema = @Schema(type = "string", allowableValues = {"distance", "rating","likes"})),
            @Parameter(name = "lat", description = "검색 기준 위도",
                    required = true, schema = @Schema(type = "number", format = "double")),
            @Parameter(name = "lng", description = "검색 기준 경도",
                    required = true, schema = @Schema(type = "number", format = "double"))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "서점 검색 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(
                    responseCode = "404", description = "검색 결과 없음",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<?> searchBookstores(@RequestParam(required = false) String searchK,
                                              @RequestParam(required = false) List<String> bookstoreK,
                                              @RequestParam(required = false) String region,
                                              @RequestParam(required = false, defaultValue = "distance") String sortField,
                                              @AuthenticationPrincipal Member member,
                                              @RequestParam double lat, @RequestParam double lng) {
        try {
            List<BookstoreResponse> bookstores = bookstoreService.searchBookstores(searchK,bookstoreK,region,sortField, member, lat, lng);
            return ResponseEntity.ok(SuccessResponse.<List<BookstoreResponse>>builder()
                    .result(true)
                    .status(HttpStatus.OK.value())
                    .message(searchK + bookstoreK + region + " - 서점 검색 성공")
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
    public ResponseEntity<?> toggleLikeBookstore(@PathVariable Long bookstoreId,
                                                 @AuthenticationPrincipal Member member) {
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
            bookstoreService.toggleLikeBookstore(member.getMemberId(), bookstoreId);
            return ResponseEntity.ok(SuccessResponse.<Void>builder()
                    .result(true)
                    .status(HttpStatus.OK.value())
                    .message("서점 찜하기/찜 취소 성공 - 사용자: " + member.getMemberId() + " 서점: " + bookstoreId)
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
    public ResponseEntity<?> getLikedBookstores(@Parameter(hidden = true) @AuthenticationPrincipal Member member,
                                                @RequestParam double lat, @RequestParam double lng) {
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
            List<BookstoreResponse> likedBookstores = bookstoreService.getLikedBookstoresByCategory(member, lat, lng);
            return ResponseEntity.ok(SuccessResponse.<List<BookstoreResponse>>builder()
                    .result(true)
                    .status(HttpStatus.OK.value())
                    .message("찜한 서점 목록 조회 성공")
                    .data(likedBookstores)
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
            @Parameter(name = "sortField", description = "[type-reviews] 정렬 타입 (createdAt: 최신순, rating: 별점순)",
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

    @Operation(
            summary = "급상승 독립서점 목록 조회",
            description = "관심 급상승(weekly 인기순) 독립서점 이름 리스트를 반환합니다. " +
                    "데이터 10개를 제공하며 부족할 경우 지난 주 랭킹/전체 서점에서 순차적으로 채워집니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "급상승 서점 목록 조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "급상승 서점 정보가 존재하지 않음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "Redis 연결 실패 등 서비스 일시적 장애",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
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
    @Operation(
            summary = "서점 해시태그 목록 조회",
            description = "서점 해시태그 중 랜덤으로 10개를 반환합니다. 각 해시태그는 태그명과 연결된 서점 ID를 포함합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추천 해시태그 목록 조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "추천 해시태그 정보가 존재하지 않음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/hashtag")
    public ResponseEntity<?> getRandomHashtags(@RequestParam(defaultValue = "10") int count) {
        try {
            List<HashtagResponse> hashtags = hashtagService.getRandomHashtagsWithBookstoreId(count);
            if (hashtags == null || hashtags.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ErrorResponse.builder()
                                .result(false)
                                .status(HttpStatus.NOT_FOUND.value())
                                .message("추천 해시태그 정보가 존재하지 않습니다")
                                .detail("DB에 추천 해시태그가 없습니다.")
                                .build());
            }
            return ResponseEntity.ok(
                    SuccessResponse.<List<HashtagResponse>>builder()
                            .result(true)
                            .status(HttpStatus.OK.value())
                            .message("추천 해시태그 목록 조회 성공")
                            .data(hashtags)
                            .build()
            );
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

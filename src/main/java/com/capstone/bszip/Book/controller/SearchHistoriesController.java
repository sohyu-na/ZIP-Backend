package com.capstone.bszip.Book.controller;

import com.capstone.bszip.Book.dto.SearchDto;
import com.capstone.bszip.Book.service.SearchHistoriesSevice;
import com.capstone.bszip.Member.domain.Member;
import com.capstone.bszip.commonDto.ErrorResponse;
import com.capstone.bszip.commonDto.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "검색 기록", description = "검색 기록 관련 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SearchHistoriesController {

    private final SearchHistoriesSevice searchHistoriesSevice;

    @Operation(
            summary = "검색어 저장",
            description = "검색 성공 후 검색어와 검색 타입을 저장합니다. 책리뷰 검색기록은 searchtype을 'BOOKTITLE'로 해주세요."
    )
    @ApiResponse(responseCode = "200", description = "검색어 저장 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "500", description = "서버 에러 발생")
    @PostMapping("/search-history")
    public ResponseEntity<?> storeSearchHistories(@AuthenticationPrincipal Member member,
                                                  @Valid @RequestBody SearchDto searchDto) {
        try{
            if(member == null){
                return ResponseEntity.status(401).body(
                        ErrorResponse.builder().status(401).result(false).message("로그인 후 이용해주세요.").build()
                );
            }

            String searchWord = searchDto.getSearchWord();
            String searchType = searchDto.getSearchType();

            if(searchType == null || searchWord == null){
                return ResponseEntity.status(400).body(
                        ErrorResponse.builder().status(400).result(false).message("검색어와 검색타입을 모두 포함해주세요.").build()
                );
            }

            searchHistoriesSevice.storeSearchHistories(searchType, searchWord, member);
            return ResponseEntity.status(200).body(
                    SuccessResponse.builder().status(200).result(true)
                                    .message("성공적으로 검색어가 저장되었습니다🪱").build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    ErrorResponse.builder().result(false).status(500).message("검색어 저장 실패!").build()
            );
        }
    }

}

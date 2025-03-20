package com.capstone.bszip.Book.service;

import com.capstone.bszip.Book.domain.SearchHistories;
import com.capstone.bszip.Book.domain.SearchType;
import com.capstone.bszip.Book.repository.SearchHistoriesRepository;
import com.capstone.bszip.Member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class SearchHistoriesSevice {

    private final SearchHistoriesRepository searchHistoriesRepository;


    public void storeSearchHistories(String searchType, String searchWord, Member member) {
        SearchType type = SearchType.valueOf(searchType.toUpperCase());
        SearchHistories searchHistories = SearchHistories.builder()
                .member(member)
                .searchWord(searchWord)
                .searchType(type)
                .build();
        searchHistoriesRepository.save(searchHistories);
    }



}

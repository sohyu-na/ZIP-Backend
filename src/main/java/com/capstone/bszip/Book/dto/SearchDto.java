package com.capstone.bszip.Book.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SearchDto {
    @NotNull
    String searchType;
    @NotNull
    String searchWord;
}

package com.capstone.bszip.commonDto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IOException extends RuntimeException {
    public IOException(String message) {
        super(message);
    }

}

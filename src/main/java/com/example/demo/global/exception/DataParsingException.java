package com.example.demo.global.exception;

import com.example.demo.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class DataParsingException extends RuntimeException {
    private final ErrorCode errorCode;

    public DataParsingException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
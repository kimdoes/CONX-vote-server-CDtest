package com.ceos23.spring_boot.global.security.handler;

import com.ceos23.spring_boot.global.exception.ErrorCode;

public record ErrorResponseDTO(
        String errCode, String errMessage
){
    public static ErrorResponseDTO create(ErrorCode e){
        return new ErrorResponseDTO(
                e.getErrorCode(), e.getMessage()
        );
    }
}
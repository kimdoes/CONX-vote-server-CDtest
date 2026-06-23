package com.ceos23.spring_boot.global.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "에러 응답 형식")
public record ErrorResponse(
        @Schema(description = "HTTP 상태 코드", example = "400")
        int status,

        @Schema(description = "에러 코드", example = "E001")
        String code,

        @Schema(description = "에러 메시지", example = "본인의 파트에 해당하는 파트장 후보에게만 투표할 수 있습니다.")
        String message
) {
    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(
                errorCode.getHttpStatus().value(),
                errorCode.getErrorCode(),
                errorCode.getMessage()
        );
    }
}
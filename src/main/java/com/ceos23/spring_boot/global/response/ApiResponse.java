package com.ceos23.spring_boot.global.response;

import com.ceos23.spring_boot.global.exception.ErrorCode;
import com.ceos23.spring_boot.global.security.handler.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공통 API 응답 형식")
public class ApiResponse<T> {

    @Schema(description = "HTTP 상태 코드", example = "200")
    private final int status;

    @Schema(description = "응답 메시지", example = "요청이 성공했습니다.")
    private final String message;

    @Schema(description = "응답 데이터")
    private final T payload;

    private ApiResponse(int status, String message, T payload) {
        this.status = status;
        this.message = message;
        this.payload = payload;
    }

    public static ApiResponse<?> ok(String message) {
        return new ApiResponse<>(200, message, null);
    }

    public static <T> ApiResponse<T> ok(String message, T payload) {
        return new ApiResponse<>(200, message, payload);
    }

    public static ApiResponse<ErrorResponseDTO> fail(ErrorCode code){
        return new ApiResponse<>(code.getHttpStatus().value(), code.getMessage(), ErrorResponseDTO.create(code));
    }

    public static <T> ApiResponse<T> created(String message, T payload) {
        return new ApiResponse<>(201, message, payload);
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getPayload() {
        return payload;
    }
}
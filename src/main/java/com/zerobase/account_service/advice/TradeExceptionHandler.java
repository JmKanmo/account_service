package com.zerobase.account_service.advice;


import com.zerobase.account_service.dto.FailResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice(basePackages = "com.zerobase.account_service")
public class TradeExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<FailResponse> exceptionHandler(Exception e, HttpServletRequest httpServletRequest) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FailResponse.builder()
                .message(e.getMessage())
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .requestUrl(httpServletRequest.getRequestURI())
                .resultCode("FAIL")
                .build());
    }
}

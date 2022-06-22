package com.zerobase.account_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FailResponse {
    private String message;
    private String statusCode;
    private String requestUrl;
    private String resultCode;
}

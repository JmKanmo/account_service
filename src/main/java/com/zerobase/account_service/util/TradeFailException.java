package com.zerobase.account_service.util;

public class TradeFailException extends RuntimeException {
    public TradeFailException(final String message) {
        super(message + " ");
    }
}

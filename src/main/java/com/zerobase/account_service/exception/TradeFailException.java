package com.zerobase.account_service.exception;

public class TradeFailException extends RuntimeException {
    public TradeFailException(final String message) {
        super(message + " ");
    }
}

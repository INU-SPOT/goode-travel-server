package com.spot.good2travel.common.exception;

public class FailedTokenCreateException extends RuntimeException {

    public FailedTokenCreateException(ExceptionMessage message) {
        super(message.getMessage());
    }
}

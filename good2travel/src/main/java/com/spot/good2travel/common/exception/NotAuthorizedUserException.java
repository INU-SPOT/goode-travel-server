package com.spot.good2travel.common.exception;

public class NotAuthorizedUserException extends RuntimeException {
    public NotAuthorizedUserException(ExceptionMessage m){
        super(m.getMessage());
    }
}

package com.spot.good2travel.common.exception;

public class UserNotAuthorizedException extends RuntimeException{
    public UserNotAuthorizedException(ExceptionMessage m){
        super(m.getMessage());
    }
}

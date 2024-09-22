package com.spot.good2travel.common.exception;

public class ItemAccessException extends RuntimeException{
    public ItemAccessException(ExceptionMessage m){
        super(m.getMessage());
    }
}

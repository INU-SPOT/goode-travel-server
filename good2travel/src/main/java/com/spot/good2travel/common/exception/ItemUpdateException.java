package com.spot.good2travel.common.exception;

public class ItemUpdateException extends RuntimeException{
    public ItemUpdateException(ExceptionMessage m){
        super(m.getMessage());
    }
}

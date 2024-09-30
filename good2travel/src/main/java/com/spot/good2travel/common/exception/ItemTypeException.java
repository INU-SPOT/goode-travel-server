package com.spot.good2travel.common.exception;

public class ItemTypeException extends RuntimeException{

    public ItemTypeException(ExceptionMessage m){
        super(m.getMessage());
    }
}

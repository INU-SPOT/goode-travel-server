package com.spot.good2travel.common.exception;

public class ItemIsExistException extends RuntimeException{

    public ItemIsExistException(ExceptionMessage e){
        super(e.getMessage());
    }
}

package com.spot.good2travel.common.exception;

public class ItemCourseIsExistException extends RuntimeException{
    public ItemCourseIsExistException(ExceptionMessage e){
        super(e.getMessage());
    }
}

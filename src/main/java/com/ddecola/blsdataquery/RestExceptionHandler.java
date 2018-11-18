package com.ddecola.blsdataquery;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Exception handlers for commonly expected exceptions related to invalid request data or other errors
 */

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler({ YearNotFoundException.class })
    public ResponseEntity handleYearNotFoundException(YearNotFoundException e, WebRequest request) {
        return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ IllegalArgumentException.class })
    public ResponseEntity handleIllegalArgumentException(IllegalArgumentException e, WebRequest request) {
        return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ RuntimeException.class })
    public ResponseEntity handleRuntimeException(RuntimeException e, WebRequest request) {
        return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

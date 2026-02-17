package com.example.demo.exception;

import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo.model.ErrorRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorRequest> handleDuplicateEmail(DuplicateEmailException ex){
        return new ResponseEntity<>(
            new ErrorRequest(ex.getMessage(),400, new Date().toString()),HttpStatus.BAD_REQUEST);
    }

     @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorRequest> handleUserNotFound(UserNotFoundException ex){
        return new ResponseEntity<>(new ErrorRequest(ex.getMessage(), 404, new Date().toString()),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleAuth(RuntimeException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorRequest> handleBadRequest(MethodArgumentNotValidException ex){

        
        String message = ex.getBindingResult()
                           .getFieldErrors()
                           .get(0)
                           .getDefaultMessage();

        ErrorRequest errorRequest = new ErrorRequest(
                message,
                HttpStatus.BAD_REQUEST.value(),
                new Date().toString()
        );
        return new ResponseEntity<>(errorRequest,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRequest> handleUnkown(Exception ex){

        ErrorRequest errorRequest = new ErrorRequest(ex.getMessage(),500, new Date().toString());

        return new ResponseEntity<>(errorRequest,HttpStatus.INTERNAL_SERVER_ERROR);


    }

}

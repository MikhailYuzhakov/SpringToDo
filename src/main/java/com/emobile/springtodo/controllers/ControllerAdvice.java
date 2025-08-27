package com.emobile.springtodo.controllers;

import com.emobile.springtodo.exceptions.TaskNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.emobile.springtodo.dto.ErrorResponse;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> TaskNotFoundExceptionHandler(TaskNotFoundException exception) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("ACCESS_ERROR", exception.getMessage()));
    }
}

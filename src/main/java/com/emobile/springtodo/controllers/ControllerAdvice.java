package com.emobile.springtodo.controllers;

import com.emobile.springtodo.dto.ApiResponse;
import com.emobile.springtodo.exceptions.TaskNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.emobile.springtodo.dto.ErrorResponse;
import org.springframework.web.bind.annotation.ResponseStatus;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TaskNotFoundException.class)
    public ApiResponse<ErrorResponse> TaskNotFoundExceptionHandler(TaskNotFoundException exception) {
        return ApiResponse.error(new ErrorResponse("ACCESS_ERROR", exception.getMessage()));
    }
}

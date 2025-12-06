package com.emobile.springtodo.controllers;

import com.emobile.springtodo.dto.ApiResponse;
import com.emobile.springtodo.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.*;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ApiResponse<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {

        List<Map<String, Object>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("field", fe.getField());
                    m.put("message", fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value");
                    Object rejected = fe.getRejectedValue();
                    if (rejected != null) {
                        m.put("rejectedValue", rejected);
                    }
                    return m;
                })
                .toList();

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        pd.setDetail("Invalid request body");
        pd.setProperty("errors", errors);
        return ApiResponse.error(new ErrorResponse("VALIDATION_ERROR", pd.toString()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        List<Map<String, Object>> errors = ex.getConstraintViolations().stream()
                .map(cv -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("field", cv.getPropertyPath().toString());
                    m.put("message", cv.getMessage() != null ? cv.getMessage() : "Invalid value");
                    Object invalid = cv.getInvalidValue();
                    if (invalid != null) {
                        m.put("rejectedValue", invalid);
                    }
                    return m;
                })
                .toList();

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        pd.setDetail("Invalid request parameters");
        pd.setProperty("errors", errors);
        return ApiResponse.error(new ErrorResponse("VALIDATION_ERROR", pd.toString()));
    }

    @ExceptionHandler(BindException.class)
    protected ApiResponse<ErrorResponse> handleBindException(BindException ex) {

        List<Map<String, Object>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("field", fe.getField());
                    m.put("message", fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value");
                    Object rejected = fe.getRejectedValue();
                    if (rejected != null) {
                        m.put("rejectedValue", rejected);
                    }
                    return m;
                })
                .toList();

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        pd.setDetail("Binding error");
        pd.setProperty("errors", errors);
        return ApiResponse.ok(new ErrorResponse("VALIDATION_ERROR", pd.toString()));
    }
}

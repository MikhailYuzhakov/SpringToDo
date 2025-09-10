package com.emobile.springtodo.controllers;

import com.emobile.springtodo.dto.ApiResponse;
import com.emobile.springtodo.dto.TaskCreateRequest;
import com.emobile.springtodo.dto.TaskResponse;
import com.emobile.springtodo.dto.TaskUpdateRequest;
import com.emobile.springtodo.services.TaskServiceInterface;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskServiceInterface service;

    @GetMapping
    public ApiResponse<List<TaskResponse>> getAllTasks(
            @RequestParam(defaultValue = "10") @Positive int limit,
            @RequestParam(defaultValue = "0") @PositiveOrZero int offset) {
        log.warn("getAllTask() controller");
        List<TaskResponse> taskResponses = service.getAllTasks(limit, offset);
        return ApiResponse.ok(taskResponses);
    }

    @GetMapping("/{id}")
    public ApiResponse<TaskResponse> getTaskById(@Positive @PathVariable Long id) {
        return ApiResponse.ok(service.get(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TaskResponse> createTask(@Valid @RequestBody TaskCreateRequest taskCreateRequest) {
        TaskResponse taskResponse = service.create(taskCreateRequest);
        return ApiResponse.created(taskResponse);
    }

    @PutMapping("/{id}")
    public ApiResponse<TaskResponse> updateTask(@Positive @PathVariable Long id,
                                                   @Valid @RequestBody TaskUpdateRequest taskUpdateRequest) {
        return ApiResponse.ok(service.updateTask(id, taskUpdateRequest));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteTask(@Positive @PathVariable Long id) {
        service.deleteTask(id);
        return ApiResponse.ok("Task delete successfully");
    }
}

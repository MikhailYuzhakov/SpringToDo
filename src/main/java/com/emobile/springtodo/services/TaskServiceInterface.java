package com.emobile.springtodo.services;

import com.emobile.springtodo.dto.PaginatedResult;
import com.emobile.springtodo.dto.TaskCreateRequest;
import com.emobile.springtodo.dto.TaskResponse;
import com.emobile.springtodo.dto.TaskUpdateRequest;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.util.List;

public interface TaskServiceInterface {
    PaginatedResult<TaskResponse> getAllTasks(int limit, int offset);
    TaskResponse get(Long taskId);

    TaskResponse create(TaskCreateRequest taskCreateRequest);

    @Caching(evict = {
            @CacheEvict(value = "task", key = "#id"),
            @CacheEvict(value = "tasks", allEntries = true),
            @CacheEvict(value = "tasks_completed", allEntries = true),
            @CacheEvict(value = "tasks_active", allEntries = true)
    })
    TaskResponse updateTask(Long id, TaskUpdateRequest taskDetails);

    int countTaskByStatus(boolean isCompleted);

    void deleteTask(Long id);
}

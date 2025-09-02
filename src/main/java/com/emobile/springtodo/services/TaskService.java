package com.emobile.springtodo.services;

import com.emobile.springtodo.dto.TaskCreateRequest;
import com.emobile.springtodo.dto.TaskResponse;
import com.emobile.springtodo.dto.TaskUpdateRequest;
import com.emobile.springtodo.exceptions.TaskNotFoundException;
import com.emobile.springtodo.model.Task;
import com.emobile.springtodo.repositories.TaskDao;
import com.emobile.springtodo.utils.TaskMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService implements TaskServiceInterface {
    private final TaskDao taskRepository;
    private final TaskMapper taskMapper;
    private final MetricsService metricsService;

    @Override
    @Cacheable(value = "tasks", key = "'all'", sync = true)
    public List<TaskResponse> getAllTasks() {
        List<Task> tasks = taskRepository.findAll()
                .orElseThrow(() -> new TaskNotFoundException("Tasks not found"));
        return tasks.stream().map(taskMapper::toResponse).toList();
    }

    @Override
    @Cacheable(value = "task", key = "#taskId", sync = true)
    public TaskResponse get(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id " + taskId));
        return taskMapper.toResponse(task);
    }

    @Transactional
    @Caching(
            put = @CachePut(value = "task", key = "#result.id"),
            evict = {
                    @CacheEvict(value = "tasks", allEntries = true),
                    @CacheEvict(value = "tasks_completed", allEntries = true),
                    @CacheEvict(value = "tasks_active", allEntries = true)
            }
    )
    @Override
    public TaskResponse create(TaskCreateRequest taskCreateRequest) {
        long start = System.currentTimeMillis();
        try {
            log.info("Creating new todo: {}", taskCreateRequest.getTitle());
            Task task = taskMapper.toEntity(taskCreateRequest);
            Task newTask = taskRepository.save(task);

            metricsService.incrementCreatedTasks();
            metricsService.recordTaskMetrics(task);

            return taskMapper.toResponse(newTask);
        } finally {
            metricsService.recordTaskCreationTime(System.currentTimeMillis() - start);
        }
    }

    @Transactional
    @Caching(
            put = @CachePut(value = "task", key = "#id"),
            evict = {
                    @CacheEvict(value = "tasks", allEntries = true),
                    @CacheEvict(value = "tasks_completed", allEntries = true),
                    @CacheEvict(value = "tasks_active", allEntries = true)
            }
    )
    @Override
    public TaskResponse updateTask(Long id, TaskUpdateRequest taskDetails) {
        long start = System.currentTimeMillis();
        try {
            log.info("Updating task with id: {}", id);
            Task task = taskRepository.findById(id)
                    .orElseThrow(() -> new TaskNotFoundException("Task not found with id " + id));

            taskMapper.updateEntityFromRequest(taskDetails, task);
            Task updatedTask = taskRepository.update(id, task);

            log.info("Task updated successfully: {}", id);
            return taskMapper.toResponse(updatedTask);
        } finally {
            metricsService.recordTaskUpdateTime(System.currentTimeMillis() - start);
        }
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "task", key = "#id"),
            @CacheEvict(value = "tasks", allEntries = true),
            @CacheEvict(value = "tasks_completed", allEntries = true),
            @CacheEvict(value = "tasks_active", allEntries = true)
    })
    @Override
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id " + id));
        taskRepository.delete(task);
        metricsService.incrementDeletedTasks();
    }
}

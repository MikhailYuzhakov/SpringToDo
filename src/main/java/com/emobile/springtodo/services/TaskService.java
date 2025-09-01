package com.emobile.springtodo.services;

import com.emobile.springtodo.dto.TaskCreateRequest;
import com.emobile.springtodo.dto.TaskDTO;
import com.emobile.springtodo.dto.TaskResponse;
import com.emobile.springtodo.exceptions.TaskNotFoundException;
import com.emobile.springtodo.model.Task;
import com.emobile.springtodo.repositories.TaskDao;
import com.emobile.springtodo.utils.TaskMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService implements TaskServiceInterface {
    private final TaskDao taskRepository;
    private final TaskMapper taskMapper;
    private final MetricsService metricsService;

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll()
                .orElseThrow(() -> new TaskNotFoundException("Tasks not found"));
    }

    @Override
    public Task get(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id " + taskId));
    }

    @Override
    public TaskResponse create(TaskCreateRequest taskCreateRequest) {
        long startTime = System.currentTimeMillis();

        try {
            log.info("Creating new todo: {}", request.getTitle());
            Task task = taskMapper.toEntity(taskCreateRequest);
            taskRepository.save(task);

            metricsService.incrementCreatedTasks();
            metricsService.recordTaskMetrics(task);

            return taskMapper.toResponse(task);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            metricsService.recordTaskCreationTime(duration);
        }


        Task task = new Task();
        task.setDescription(taskDTO.getDescription());
        task.setTitle(taskDTO.getTitle());
        task.setCompleted(taskDTO.isCompleted());
        task.setCreated_at(LocalDateTime.now());
        task.setUpdated_at(LocalDateTime.now());
        taskRepository.save(task);
        return task;
    }

    @Override
    public Task updateTask(Long id, TaskDTO taskDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id " + id));
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setCompleted(taskDetails.isCompleted());
        taskRepository.update(id, task);
        return task;
    }

    @Override
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id " + id));
        taskRepository.delete(task);
    }
}

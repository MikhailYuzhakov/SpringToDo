package com.emobile.springtodo.services;

import com.emobile.springtodo.dto.TaskCreateRequest;
import com.emobile.springtodo.dto.TaskDTO;
import com.emobile.springtodo.dto.TaskResponse;
import com.emobile.springtodo.model.Task;

import java.util.List;

public interface TaskServiceInterface {
    public List<Task> getAllTasks();
    public Task get(Long taskId);
    public Task create(TaskDTO taskDTO);

    TaskResponse create(TaskCreateRequest taskCreateRequest);

    public Task updateTask(Long id, TaskDTO taskDetails);
    public void deleteTask(Long id);

}

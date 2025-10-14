package com.emobile.springtodo.repositories;

import com.emobile.springtodo.model.Task;

import java.util.List;
import java.util.Optional;


public interface TaskDao {
    Optional<List<Task>> findAllWithPagination(int limit, int offset);
    Optional<Task> findById(Long id);
    Task save(Task task);
    void delete(Task task);
    Task update(Long id, Task task);
    Optional<List<Task>> findAllByStatus(boolean isCompleted);
    Integer countTasks();
}

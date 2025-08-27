package com.emobile.springtodo.repositories;

import com.emobile.springtodo.model.Task;

import java.util.List;
import java.util.Optional;


public interface TaskDao {
    Optional<List<Task>> findAll();
    Optional<Task> findById(Long id);
    void save(Task task);
    void delete(Task task);
    void update(Long id, Task task);
}

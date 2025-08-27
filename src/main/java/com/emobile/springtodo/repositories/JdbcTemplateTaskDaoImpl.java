package com.emobile.springtodo.repositories;

import com.emobile.springtodo.model.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcTemplateTaskDaoImpl implements TaskDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<List<Task>> findAll() {
        String SQL = "SELECT * FROM tasks";
        try {
            List<Task> task = jdbcTemplate.query(SQL, new TaskMapper());
            return Optional.of(task);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Task> findById(Long id) {
        String SQL = "SELECT * FROM tasks WHERE id = ?";
        try {
            Task task = jdbcTemplate.queryForObject(SQL, new TaskMapper(), id);
            return Optional.ofNullable(task);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void save(Task task) {
        String SQL = "INSERT INTO tasks (title, description, completed, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(SQL, task.getTitle(), task.getDescription(), task.isCompleted(), task.getCreated_at(), task.getUpdated_at());
    }

    @Override
    public void delete(Task task) {
        String SQL = "DELETE FROM tasks WHERE id = ?";
        jdbcTemplate.update(SQL, task.getId());
    }

    @Override
    public void update(Long id, Task task) {
        String SQL = "UPDATE tasks SET title = ?, description = ?, completed = ? created_at = ? updated_at = ? WHERE id = ?";
        jdbcTemplate.update(SQL, task.getTitle(), task.getDescription(), task.isCompleted(), task.getCreated_at(), task.getUpdated_at(), task.getId());
    }
}
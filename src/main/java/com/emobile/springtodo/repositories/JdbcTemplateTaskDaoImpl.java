package com.emobile.springtodo.repositories;

import com.emobile.springtodo.exceptions.TaskNotFoundException;
import com.emobile.springtodo.model.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JdbcTemplateTaskDaoImpl implements TaskDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<List<Task>> findAllWithPagination(int limit, int offset) {
        String SQL = "SELECT * FROM tasks ORDER BY id LIMIT ? OFFSET ?";
        try {
            List<Task> tasks = jdbcTemplate.query(SQL, new RowMapperImpl(), limit, offset);
            log.info("repo task = {}", tasks);
            return Optional.of(tasks);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Task> findById(Long id) {
        String SQL = "SELECT * FROM tasks WHERE id = ?";
        try {
            Task task = jdbcTemplate.queryForObject(SQL, new RowMapperImpl(), id);
            return Optional.ofNullable(task);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<Task>> findAllByStatus(boolean isCompleted) {
        String SQL = "SELECT * FROM tasks WHERE completed = ?";
        try {
            List<Task> tasks = jdbcTemplate.query(SQL, new RowMapperImpl(), isCompleted);
            log.info("tasks with status {} : {}", isCompleted, tasks);
            return Optional.of(tasks);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Integer countTasks() {
        String SQL = "SELECT COUNT(*) FROM tasks";
        Integer count = jdbcTemplate.queryForObject(SQL, Integer.class);
        return (count != null) ? count : 0;
    }

    @Override
    public Task save(Task task) {
        String sql = """
            INSERT INTO tasks (title, description, completed, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?) RETURNING id
        """;
        Long id = jdbcTemplate.queryForObject(
                sql, Long.class,
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                java.sql.Timestamp.valueOf(task.getCreated_at()),
                java.sql.Timestamp.valueOf(task.getUpdated_at())
        );
        task.setId(id);
        return task;
    }

    @Override
    public void delete(Task task) {
        String SQL = "DELETE FROM tasks WHERE id = ?";
        jdbcTemplate.update(SQL, task.getId());
    }

    @Override
    public Task update(Long id, Task task) {
        String sql = "UPDATE tasks " +
                "SET title = ?, description = ?, completed = ?, updated_at = ? " +
                "WHERE id = ?";
        int updated = jdbcTemplate.update(
                sql,
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                java.sql.Timestamp.valueOf(task.getUpdated_at()),
                id
        );

        if (updated == 0) {
            throw new TaskNotFoundException("Task not found with id " + id);
        }

        task.setId(id);
        return task;
    }
}
package com.emobile.springtodo.repositories;

import com.emobile.springtodo.model.Task;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RowMapperImpl implements RowMapper<Task> {

    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setCreated_at(rs.getTimestamp("created_at").toLocalDateTime());
        task.setUpdated_at(rs.getTimestamp("updated_at").toLocalDateTime());
        task.setCompleted(rs.getBoolean("completed"));
        return task;
    }
}

package com.emobile.springtodo.dao;

import com.emobile.springtodo.DatabaseTestBase;
import com.emobile.springtodo.model.Task;
import com.emobile.springtodo.repositories.JdbcTemplateTaskDaoImpl;
import com.emobile.springtodo.repositories.TaskDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TaskDaoTest extends DatabaseTestBase {

    @Autowired
    private TaskDao taskDao;

    @Test
    @DisplayName("findById должен найти и вернуть существующую задачу")
    @Sql("/sql/add-tasks.sql")
    void findById_shouldReturnTask_whenTaskExists() {
        // Act
        Optional<Task> foundTask = taskDao.findById(1L);

        // Assert
        assertThat(foundTask).isPresent();
        assertThat(foundTask.get().getTitle()).isEqualTo("First Task");
        assertThat(foundTask.get().isCompleted()).isFalse();
    }

    @Test
    @DisplayName("findAllWithPagination должен вернуть правильную страницу задач")
    @Sql("/sql/add-tasks.sql")
    void findAllWithPagination_shouldReturnCorrectPage() {
        // Act
        var tasks = taskDao.findAllWithPagination(2, 0).orElseThrow();

        // Assert
        assertThat(tasks).hasSize(2);
        assertThat(tasks.get(0).getId()).isEqualTo(1L);
        assertThat(tasks.get(1).getId()).isEqualTo(2L);
    }
}

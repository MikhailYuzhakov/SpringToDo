package com.emobile.springtodo.service;

import com.emobile.springtodo.dto.TaskCreateRequest;
import com.emobile.springtodo.dto.TaskResponse;
import com.emobile.springtodo.exceptions.TaskNotFoundException;
import com.emobile.springtodo.model.Task;
import com.emobile.springtodo.repositories.TaskDao;
import com.emobile.springtodo.services.TaskService;
import com.emobile.springtodo.utils.TaskMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Инициализирует моки
class TaskServiceTest {

    @Mock
    private TaskDao taskRepository; // Мокаем зависимость

    @Mock
    private TaskMapper taskMapper; // Мокаем зависимость

    @InjectMocks
    private TaskService taskService; // Создаем экземпляр сервиса и внедряем в него моки

    @Test
    @DisplayName("get() должен вернуть TaskResponse, если задача существует")
    void get_shouldReturnTaskResponse_whenTaskExists() {
        // Arrange (Подготовка)
        long taskId = 1L;
        Task task = new Task(taskId, "Test Task", "Description", false);
        TaskResponse expectedResponse = new TaskResponse(taskId, "Test Task", "Description", false);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toResponse(task)).thenReturn(expectedResponse);

        // Act (Действие)
        TaskResponse actualResponse = taskService.get(taskId);

        // Assert (Проверка)
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getId()).isEqualTo(expectedResponse.getId());
        assertThat(actualResponse.getTitle()).isEqualTo(expectedResponse.getTitle());

        verify(taskRepository).findById(taskId); // Проверяем, что метод был вызван
        verify(taskMapper).toResponse(task);
    }

    @Test
    @DisplayName("get() должен выбросить TaskNotFoundException, если задача не найдена")
    void get_shouldThrowException_whenTaskNotFound() {
        // Arrange
        long taskId = 99L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.get(taskId);
        }, "Должно быть выброшено исключение TaskNotFoundException");

        verify(taskRepository).findById(taskId);
        verifyNoInteractions(taskMapper); // Маппер не должен был вызываться
    }

    @Test
    @DisplayName("create() должен сохранить и вернуть новую задачу")
    void create_shouldSaveAndReturnNewTask() {
        // Arrange
        TaskCreateRequest createRequest = new TaskCreateRequest("New Task", "New Desc");
        Task taskToSave = new Task(null, "New Task", "New Desc", false);
        Task savedTask = new Task(1L, "New Task", "New Desc", false);
        TaskResponse expectedResponse = new TaskResponse(1L, "New Task", "New Desc", false);

        when(taskMapper.toEntity(any(TaskCreateRequest.class))).thenReturn(taskToSave);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        when(taskMapper.toResponse(any(Task.class))).thenReturn(expectedResponse);

        // Act
        TaskResponse actualResponse = taskService.create(createRequest);

        // Assert
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getId()).isEqualTo(1L);

        verify(taskRepository).save(taskToSave);
    }
}

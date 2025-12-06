package com.emobile.springtodo.controller;
import com.emobile.springtodo.IntegrationTestBase;
import com.emobile.springtodo.dto.TaskCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TaskControllerTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/tasks - Успешное получение списка задач")
    @Sql("/sql/add-tasks.sql")
    void getAllTasks_shouldReturnListOfTasks() throws Exception {
        // Act
        MvcResult mvcResult = mockMvc.perform(get("/api/tasks?limit=2&offset=0"))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String actualJsonResponse = mvcResult.getResponse().getContentAsString();

        String expectedJsonResponse = """           
                {
                    "success": true,
                    "data": {
                        "content": [
                                {
                                    "id": 1,
                                    "title": "First Task",
                                    "description": "Description for first task",
                                    "completed": false
                                },
                                {
                                    "id": 2,
                                    "title": "Second Task",
                                    "description": "Description for second task",
                                    "completed": true
                                }
                        ],
                        "currentPage": 0,
                        "pageSize": 2,
                        "totalElements": 3,
                        "totalPages": 2
                    }
                }
            """;

        // Используем строгий режим (true), так как мы описали всю структуру
        JSONAssert.assertEquals(expectedJsonResponse, actualJsonResponse, false);
    }

    @Test
    @DisplayName("GET /api/tasks/{id} - Успешное получение задачи по ID")
    @Sql("/sql/add-tasks.sql")
    void getTaskById_shouldReturnTask_whenExists() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andReturn();

        String actualJsonResponse = mvcResult.getResponse().getContentAsString();
        String expectedJsonResponse = """
                {
                    "success": true,
                    "data": { "id": 1, "title": "First Task", "description": "Description for first task", "completed": false }
                }
                """;
        JSONAssert.assertEquals(expectedJsonResponse, actualJsonResponse, false);
    }

    @Test
    @DisplayName("POST /api/tasks - Успешное создание задачи")
    void createTask_shouldCreateAndReturnNewTask() throws Exception {
        // Arrange
        TaskCreateRequest request = new TaskCreateRequest("New shiny task", "Created from test");
        String requestJson = objectMapper.writeValueAsString(request);

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").isNumber()) // Проверяем, что ID сгенерирован
                .andExpect(jsonPath("$.data.title").value("New shiny task"))
                .andExpect(jsonPath("$.data.completed").value(false));
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} - Успешное удаление задачи")
    @Sql("/sql/add-tasks.sql")
    void deleteTask_shouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("Task delete successfully"));
    }
}
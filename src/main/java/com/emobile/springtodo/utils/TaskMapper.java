package com.emobile.springtodo.utils;
import com.emobile.springtodo.dto.TaskCreateRequest;
import com.emobile.springtodo.dto.TaskResponse;
import com.emobile.springtodo.dto.TaskUpdateRequest;
import com.emobile.springtodo.model.Task;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TaskMapper {
    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    // Маппинг из Entity в ResponseDTO
    @Mapping(source = "created_at", target = "createdAt")
    @Mapping(source = "updated_at", target = "updatedAt")
    TaskResponse toResponse(Task task);

    List<TaskResponse> toResponseList(List<Task> tasks);

    // Маппинг из CreateRequest DTO в Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "completed", constant = "false")
    @Mapping(target = "created_at", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updated_at", expression = "java(java.time.LocalDateTime.now())")
    Task toEntity(TaskCreateRequest request);

    // Маппинг из UpdateRequest DTO в Entity (обновление существующей сущности)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created_at", ignore = true)
    @Mapping(target = "updated_at", expression = "java(java.time.LocalDateTime.now())")
    void updateEntityFromRequest(TaskUpdateRequest request, @MappingTarget Task task);

    // Маппинг для пагинации
    default Page<TaskResponse> toResponsePage(Page<Task> taskPage) {
        return taskPage.map(this::toResponse);
    }
}

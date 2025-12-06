package com.emobile.springtodo.utils;
import com.emobile.springtodo.dto.TaskCreateRequest;
import com.emobile.springtodo.dto.TaskResponse;
import com.emobile.springtodo.dto.TaskUpdateRequest;
import com.emobile.springtodo.model.Task;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {

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
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created_at", ignore = true)
    @Mapping(target = "updated_at", expression = "java(java.time.LocalDateTime.now())")
    void updateEntityFromRequest(TaskUpdateRequest request, @MappingTarget Task task);

    // Маппинг для пагинации
    default Page<TaskResponse> toResponsePage(Page<Task> taskPage) {
        return taskPage.map(this::toResponse);
    }
}

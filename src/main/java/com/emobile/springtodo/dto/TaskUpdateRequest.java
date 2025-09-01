package com.emobile.springtodo.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskUpdateRequest {

    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    private Boolean completed;
}

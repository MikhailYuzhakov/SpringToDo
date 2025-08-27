package com.emobile.springtodo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class TaskDTO {
    private String title;
    private String description;
    private boolean isCompleted;
}

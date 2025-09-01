package com.emobile.springtodo.model;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}

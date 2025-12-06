package com.emobile.springtodo.services;

import com.emobile.springtodo.model.Task;
import io.micrometer.core.instrument.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsService {
    private final MeterRegistry meterRegistry;
    private final TaskServiceInterface taskService;

    @PostConstruct
    public void init() {
        initializeGauges();
    }

    private void initializeGauges() {
        Gauge.builder("todo.tasks.active.count", taskService, service -> service.countTaskByStatus(false))
                .description("Current number of active tasks")
                .tag("application", "todo-service")
                .register(meterRegistry);

        Gauge.builder("todo.tasks.completed.total", taskService, service -> service.countTaskByStatus(true))
                .description("Total number of completed tasks (gauge)")
                .tag("application", "todo-service")
                .register(meterRegistry);
    }
}

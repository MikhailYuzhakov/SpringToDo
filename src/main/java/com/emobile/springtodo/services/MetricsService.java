package com.emobile.springtodo.services;

import com.emobile.springtodo.model.Task;
import io.micrometer.core.instrument.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@RequiredArgsConstructor
@Slf4j
public class MetricsService implements MetricsServiceInterface {
    private final MeterRegistry meterRegistry;

    // Счетчики
    private Counter createdTasksCounter;
    private Counter completedTasksCounter;
    private Counter deletedTasksCounter;

    // Таймеры
    private Timer taskCreationTimer;
    private Timer taskCompletionTimer;

    // Гейджи
    private final AtomicLong activeTasksCount = new AtomicLong(0);
    private final AtomicLong completedTasksTotal = new AtomicLong(0);

    // Distribution summaries
    private DistributionSummary taskTitleLengthSummary;

    @PostConstruct
    public void init() {
        initializeCounters();
        initializeTimers();
        initializeGauges();
        initializeDistributionSummaries();
    }
    private void initializeCounters() {
        createdTasksCounter = Counter.builder("todo.tasks.created")
                .description("Total number of created tasks")
                .tag("application", "todo-service")
                .register(meterRegistry);

        completedTasksCounter = Counter.builder("todo.tasks.completed")
                .description("Total number of completed tasks")
                .tag("application", "todo-service")
                .register(meterRegistry);

        deletedTasksCounter = Counter.builder("todo.tasks.deleted")
                .description("Total number of deleted tasks")
                .tag("application", "todo-service")
                .register(meterRegistry);
    }

    private void initializeTimers() {
        taskCreationTimer = Timer.builder("todo.tasks.creation.time")
                .description("Time taken to create a task")
                .tag("application", "todo-service")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);

        taskCompletionTimer = Timer.builder("todo.tasks.completion.time")
                .description("Time taken to complete a task")
                .tag("application", "todo-service")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);
    }

    private void initializeGauges() {
        Gauge.builder("todo.tasks.active.count", activeTasksCount, AtomicLong::get)
                .description("Current number of active tasks")
                .tag("application", "todo-service")
                .register(meterRegistry);

        Gauge.builder("todo.tasks.completed.total", completedTasksTotal, AtomicLong::get)
                .description("Total number of completed tasks (gauge)")
                .tag("application", "todo-service")
                .register(meterRegistry);
    }

    // Методы для работы с конкретными задачами
    public void recordTaskMetrics(Task task) {
        if (task.getTitle() != null) {
            recordTitleLength(task.getTitle().length());
        }

        if (Boolean.TRUE.equals(task.getCompleted())) {
            incrementCompletedTasks();
        }
    }

    // Метод для сброса метрик (для тестов)
    public void reset() {
        activeTasksCount.set(0);
        completedTasksTotal.set(0);
        log.info("Metrics reset performed");
    }


    private void initializeDistributionSummaries() {
        taskTitleLengthSummary = DistributionSummary.builder("todo.tasks.title.length")
                .description("Distribution of task title lengths")
                .tag("application", "todo-service")
                .register(meterRegistry);
    }

    public void recordTitleLength(int length) {
        taskTitleLengthSummary.record(length);
        log.debug("Title length recorded: {}", length);
    }

    @Override
    public void incrementCreatedTasks() {
        createdTasksCounter.increment();
        activeTasksCount.incrementAndGet();
        log.debug("Created tasks counter incremented");
    }

    @Override
    public void incrementCompletedTasks() {
        completedTasksCounter.increment();
        completedTasksTotal.incrementAndGet();
        activeTasksCount.decrementAndGet();
        log.debug("Completed tasks counter incremented");
    }

    @Override
    public void incrementDeletedTasks() {
        deletedTasksCounter.increment();
        activeTasksCount.decrementAndGet();
        log.debug("Deleted tasks counter incremented");
    }

    @Override
    public void recordTaskCompletionTime(long milliseconds) {
        taskCompletionTimer.record(milliseconds, TimeUnit.MILLISECONDS);
        log.debug("Task completion time recorded: {} ms", milliseconds);
    }

    @Override
    public void recordTaskCreationTime(long milliseconds) {
        taskCreationTimer.record(milliseconds, TimeUnit.MILLISECONDS);
        log.debug("Task creation time recorded: {} ms", milliseconds);
    }

    @Override
    public Map<String, Object> getMetricsSnapshot() {
        Map<String, Object> snapshot = new HashMap<>();

        snapshot.put("createdTasks", createdTasksCounter.count());
        snapshot.put("completedTasks", completedTasksCounter.count());
        snapshot.put("deletedTasks", deletedTasksCounter.count());
        snapshot.put("activeTasks", activeTasksCount.get());
        snapshot.put("totalCompleted", completedTasksTotal.get());

        // Добавляем перцентили из таймеров
        snapshot.put("creationTimeP50", taskCreationTimer.percentile(0.5, TimeUnit.MILLISECONDS));
        snapshot.put("creationTimeP95", taskCreationTimer.percentile(0.95, TimeUnit.MILLISECONDS));
        snapshot.put("completionTimeP50", taskCompletionTimer.percentile(0.5, TimeUnit.MILLISECONDS));
        snapshot.put("completionTimeP95", taskCompletionTimer.percentile(0.95, TimeUnit.MILLISECONDS));

        return snapshot;
    }
}

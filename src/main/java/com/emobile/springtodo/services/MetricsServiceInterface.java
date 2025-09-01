package com.emobile.springtodo.services;

import java.util.Map;

public interface MetricsServiceInterface {
    void incrementCreatedTasks();
    void incrementCompletedTasks();
    void incrementDeletedTasks();
    void recordTaskCompletionTime(long milliseconds);
    void recordTaskCreationTime(long milliseconds);
    Map<String, Object> getMetricsSnapshot();
}
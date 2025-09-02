package com.emobile.springtodo.services;

public interface MetricsServiceInterface {
    void incrementCreatedTasks();
    void incrementCompletedTasks();
    void incrementDeletedTasks();
    void recordTaskCreationTime(long milliseconds);
    void recordTaskUpdateTime(long milliseconds);
}
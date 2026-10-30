package com.workqueue.producer.service;

import com.workqueue.common.config.WorkQueueProperties;
import com.workqueue.common.handler.TaskHandler;
import com.workqueue.common.handler.TaskHandlerRegistry;
import com.workqueue.common.queue.QueueClient;
import com.workqueue.common.task.Task;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EnqueueService {

    private final TaskHandlerRegistry registry;
    private final QueueClient queue;
    private final WorkQueueProperties properties;

    public EnqueueService(TaskHandlerRegistry registry,
                          QueueClient queue,
                          WorkQueueProperties properties) {
        this.registry = registry;
        this.queue = queue;
        this.properties = properties;
    }

    public Task enqueue(String type, Map<String, Object> payload, Integer retries) {
        TaskHandler handler = registry.find(type)
                .orElseThrow(() -> new UnknownTaskTypeException(type, registry.types()));

        Map<String, Object> safePayload = payload == null ? Map.of() : payload;
        handler.validate(safePayload);

        int maxAttempts = retries == null ? properties.defaultMaxAttempts() : retries;
        Task task = Task.create(type, safePayload, maxAttempts);

        queue.push(task);
        return task;
    }
}
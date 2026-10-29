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
        // 1. Is this a task type we know?
        TaskHandler handler = registry.find(type)
                .orElseThrow(() -> new UnknownTaskTypeException(type, registry.types()));

        // 2. Is the payload valid for that type?
        Map<String, Object> safePayload = payload == null ? Map.of() : payload;
        handler.validate(safePayload);

        // 3. Build the envelope: fresh id, zero failures.
        int maxAttempts = retries == null ? properties.defaultMaxAttempts() : retries;
        Task task = Task.create(type, safePayload, maxAttempts);

        // 4. Store it. If Redis fails this throws, so the caller knows the task was NOT queued.
        queue.push(task);
        return task;
    }
}
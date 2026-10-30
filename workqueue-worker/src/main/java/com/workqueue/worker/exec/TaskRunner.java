package com.workqueue.worker.exec;

import com.workqueue.common.handler.TaskHandler;
import com.workqueue.common.handler.TaskHandlerRegistry;
import com.workqueue.common.task.Claimed;
import com.workqueue.common.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TaskRunner {

    private static final Logger log = LoggerFactory.getLogger(TaskRunner.class);

    private final TaskHandlerRegistry registry;

    public TaskRunner(TaskHandlerRegistry registry) {
        this.registry = registry;
    }

    public void execute(Claimed claimed) {
        Task task = claimed.task();
        try {
            TaskHandler handler = registry.find(task.type())
                    .orElseThrow(() -> new IllegalStateException(
                            "No handler for task type: " + task.type()));

            log.info("[{}] Running {} task (attempt {} of {})",
                    task.id(), task.type(), task.attempt() + 1, task.maxAttempts());
            long started = System.nanoTime();

            handler.handle(task);

            log.info("[{}] Finished in {} ms", task.id(), (System.nanoTime() - started) / 1_000_000);
        } catch (Exception e) {
            // F3 turns this into "retry or dead-letter". For now we only record it.
            log.error("[{}] Task failed: {}", task.id(), e.toString());
        }
    }
}
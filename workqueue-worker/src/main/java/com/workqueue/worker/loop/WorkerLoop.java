package com.workqueue.worker.loop;

import com.workqueue.common.queue.QueueClient;
import com.workqueue.worker.exec.TaskRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class WorkerLoop implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(WorkerLoop.class);
    private static final long ERROR_PAUSE_MILLIS = 2_000;

    private final QueueClient queue;
    private final TaskRunner runner;
    private final String workerId;
    private final Duration pollTimeout;

    private volatile boolean running = true;

    public WorkerLoop(QueueClient queue, TaskRunner runner, String workerId, Duration pollTimeout) {
        this.queue = queue;
        this.runner = runner;
        this.workerId = workerId;
        this.pollTimeout = pollTimeout;
    }

    @Override
    public void run() {
        log.info("Worker loop started ({})", workerId);
        while (running) {
            try {
                queue.claim(pollTimeout, workerId).ifPresent(runner::execute);
            } catch (Exception e) {
                log.error("Could not claim a task, retrying shortly: {}", e.toString());
                pauseAfterError();
            }
        }
        log.info("Worker loop stopped ({})", workerId);
    }

    public void stop() {
        running = false;
    }

    private void pauseAfterError() {
        try {
            Thread.sleep(ERROR_PAUSE_MILLIS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            running = false;
        }
    }
}
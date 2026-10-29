package com.workqueue.common.handler;

import com.workqueue.common.task.Task;

import java.util.Map;

public interface TaskHandler {
    String type();
    void validate(Map<String, Object> payload);
    void handle(Task task) throws Exception;
}

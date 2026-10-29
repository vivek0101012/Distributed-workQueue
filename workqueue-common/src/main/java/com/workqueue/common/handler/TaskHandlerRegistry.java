package com.workqueue.common.handler;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class TaskHandlerRegistry {

    private final Map<String, TaskHandler> handlers;

    public TaskHandlerRegistry(List<TaskHandler> allHandlers) {
        Map<String, TaskHandler> map = new HashMap<>();
        for (TaskHandler task : allHandlers) {
            String type = task.type();

            if (map.containsKey(type)) {
                throw new IllegalStateException("Two handlers claim task type: " + type);
            }
            map.put(type, task);
        }

        this.handlers = Map.copyOf(map);
    }

    public Optional<TaskHandler> find(String task) {
        return Optional.ofNullable(handlers.get(task));
    }

    public Set<String> types() {
        return handlers.keySet();
    }
}

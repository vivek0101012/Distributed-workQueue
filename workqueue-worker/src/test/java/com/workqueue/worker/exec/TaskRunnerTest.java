package com.workqueue.worker.exec;

import com.workqueue.common.handler.TaskHandler;
import com.workqueue.common.handler.TaskHandlerRegistry;
import com.workqueue.common.task.Claimed;
import com.workqueue.common.task.Task;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskRunnerTest {

    private final AtomicInteger calls = new AtomicInteger();

    private TaskHandler handler(String type, boolean fails) {
        return new TaskHandler() {
            @Override
            public String type() {
                return type;
            }

            @Override
            public void validate(Map<String, Object> payload) {
            }

            @Override
            public void handle(Task task) {
                calls.incrementAndGet();
                if (fails) {
                    throw new IllegalStateException("boom");
                }
            }
        };
    }

    private TaskRunner runnerWith(TaskHandler handler) {
        return new TaskRunner(new TaskHandlerRegistry(List.of(handler)));
    }

    private Claimed claimedOfType(String type) {
        return new Claimed("raw-text-not-used-here", Task.create(type, Map.of(), 3));
    }

    @Test
    void runsTheHandlerForAKnownType() {
        runnerWith(handler("demo", false)).execute(claimedOfType("demo"));
        assertEquals(1, calls.get());
    }

    @Test
    void aFailingHandlerDoesNotEscape() {
        TaskRunner runner = runnerWith(handler("demo", true));
        assertDoesNotThrow(() -> runner.execute(claimedOfType("demo")));
        assertEquals(1, calls.get());
    }

    @Test
    void anUnknownTypeDoesNotEscapeAndRunsNothing() {
        TaskRunner runner = runnerWith(handler("demo", false));
        assertDoesNotThrow(() -> runner.execute(claimedOfType("other")));
        assertEquals(0, calls.get());
    }
}

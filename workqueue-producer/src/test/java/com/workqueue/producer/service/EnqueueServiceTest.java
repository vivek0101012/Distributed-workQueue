package com.workqueue.producer.service;

import com.workqueue.common.config.WorkQueueProperties;
import com.workqueue.common.handler.InvalidPayloadException;
import com.workqueue.common.handler.TaskHandlerRegistry;
import com.workqueue.common.queue.QueueClient;
import com.workqueue.common.task.Task;
import com.workqueue.handlers.SendEmailHandler;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EnqueueServiceTest {

    private static final Map<String, Object> GOOD_EMAIL =
            Map.of("to", "a@b.com", "subject", "Hi", "body", "Hello");

    private final List<Task> pushed = new ArrayList<>();

    private final QueueClient fakeQueue = new QueueClient() {
        @Override public void push(Task task) { pushed.add(task); }
        @Override public long mainLength() { return pushed.size(); }
    };

    private final EnqueueService service = new EnqueueService(
            new TaskHandlerRegistry(List.of(new SendEmailHandler())),
            fakeQueue,
            new WorkQueueProperties(3));

    @Test
    void validTaskIsPushedWithDefaults() {
        Task task = service.enqueue("send_email", GOOD_EMAIL, null);

        assertEquals(1, pushed.size());
        assertEquals(task, pushed.get(0));
        assertEquals(0, task.attempt());
        assertEquals(3, task.maxAttempts());
        assertNotNull(task.id());
    }

    @Test
    void clientRetriesOverrideTheDefault() {
        Task task = service.enqueue("send_email", GOOD_EMAIL, 5);
        assertEquals(5, task.maxAttempts());
    }

    @Test
    void unknownTypeIsRejectedAndNothingIsPushed() {
        assertThrows(UnknownTaskTypeException.class,
                () -> service.enqueue("nope", Map.of(), null));
        assertTrue(pushed.isEmpty());
    }

    @Test
    void badPayloadIsRejectedAndNothingIsPushed() {
        assertThrows(InvalidPayloadException.class,
                () -> service.enqueue("send_email", Map.of("to", "a@b.com"), null));
        assertTrue(pushed.isEmpty());
    }
}
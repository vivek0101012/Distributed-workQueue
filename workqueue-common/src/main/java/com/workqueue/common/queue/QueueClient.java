package com.workqueue.common.queue;

import com.workqueue.common.task.Claimed;
import com.workqueue.common.task.Task;

import java.time.Duration;
import java.util.Optional;

public interface QueueClient {

    void push (Task task);
    long mainLength();
    Optional<Claimed> claim(Duration timeout , String workerId);

}

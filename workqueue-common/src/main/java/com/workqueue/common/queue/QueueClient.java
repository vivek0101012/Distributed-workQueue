package com.workqueue.common.queue;

import com.workqueue.common.task.Task;

public interface QueueClient {

    void push (Task task);
    long mainLength();

}

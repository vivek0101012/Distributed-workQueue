package com.workqueue.common.task;


// correlating the task claimed and raw text stored in redis
public record Claimed(String raw,Task task) {
}

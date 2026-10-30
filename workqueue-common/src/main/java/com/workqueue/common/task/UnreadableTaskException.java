package com.workqueue.common.task;

public class UnreadableTaskException extends RuntimeException {

    private final String raw;

    public UnreadableTaskException(String raw, Throwable cause) {
        super("Cannot read task from Redis", cause);
        this.raw = raw;
    }

    public String raw() {
        return raw;
    }
}
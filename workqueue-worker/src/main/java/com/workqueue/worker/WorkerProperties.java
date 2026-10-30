package com.workqueue.worker;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "workqueue.worker")
public record WorkerProperties(
        @DefaultValue("3") int workers,
        @DefaultValue("5") int pollTimeoutSeconds,
        @DefaultValue("30") int shutdownTimeoutSeconds) {
}
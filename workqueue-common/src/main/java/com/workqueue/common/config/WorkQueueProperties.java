package com.workqueue.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "workqueue")
public record WorkQueueProperties(
        @DefaultValue("3") int defaultMaxAttempts) {

}
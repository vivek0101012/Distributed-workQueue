package com.workqueue.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(WorkQueueProperties.class)
public class WorkQueueCommonConfig {
}

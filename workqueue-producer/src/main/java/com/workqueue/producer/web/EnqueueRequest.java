
package com.workqueue.producer.web;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;
public record EnqueueRequest(
        @NotBlank String type,
        Map<String, Object> payload,
        @Min(1) @Max(10) Integer retries) {
}
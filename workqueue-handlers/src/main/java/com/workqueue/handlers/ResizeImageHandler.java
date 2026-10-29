package com.workqueue.handlers;

import com.workqueue.common.handler.Payloads;
import com.workqueue.common.handler.TaskHandler;
import com.workqueue.common.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ResizeImageHandler implements TaskHandler {

    private static final Logger log = LoggerFactory.getLogger(ResizeImageHandler.class);

    @Override
    public String type() {
        return "resize_image";
    }

    @Override
    public void validate(Map<String, Object> payload) {
        Payloads.requireText(payload, "imageUrl");
        Payloads.requirePositiveInt(payload, "width");
    }

    @Override
    public void handle(Task task) throws Exception {
        log.info("Resizing {} to width {} (task {})",
                task.payload().get("imageUrl"), task.payload().get("width"), task.id());
        Thread.sleep(800);
    }
}
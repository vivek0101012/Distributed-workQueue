package com.workqueue.handlers;
import com.workqueue.common.handler.Payloads;
import com.workqueue.common.handler.TaskHandler;
import com.workqueue.common.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GeneratePdfHandler implements TaskHandler {

    private static final Logger log = LoggerFactory.getLogger(GeneratePdfHandler.class);

    @Override
    public String type() {
        return "generate_pdf";
    }

    @Override
    public void validate(Map<String, Object> payload) {
        Payloads.requireText(payload, "title");
        Payloads.requireText(payload, "content");
    }

    @Override
    public void handle(Task task) throws Exception {
        log.info("Generating PDF '{}' (task {})", task.payload().get("title"), task.id());
        Thread.sleep(1000);
    }
}
package com.workqueue.handlers;
import com.workqueue.common.handler.Payloads;
import com.workqueue.common.handler.TaskHandler;
import com.workqueue.common.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SendEmailHandler implements  TaskHandler{

    private static final Logger log = LoggerFactory.getLogger(SendEmailHandler.class);

    @Override
    public String type() {
        return "send_email";
    }

    @Override
    public void validate(Map<String, Object> payload) {

        Payloads.requireText(payload,"to");
        Payloads.requireText(payload, "subject");
        Payloads.requireText(payload, "body");
    }

    @Override
    public void handle(Task task) throws Exception {
        log.info("Sending email to {} (task {})", task.payload().get("to"), task.id());
        Thread.sleep(500);

    }
}

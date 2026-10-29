package com.workqueue.producer.web;




import com.workqueue.common.task.Task;
import com.workqueue.producer.service.EnqueueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnqueueController {
    private  final EnqueueService enqueueService;

    public EnqueueController(EnqueueService enqueueService) {
        this.enqueueService = enqueueService;
    }


    @PostMapping("/task")
    public ResponseEntity<EnqueueResponse>enqueue(@Valid @RequestBody EnqueueRequest request){
        Task task=  enqueueService.enqueue (request.type(),request.payload(),request.retries());

        return  ResponseEntity.status(HttpStatus.ACCEPTED).body(new EnqueueResponse(task.id(), task.type(), "ENQUEUED"));

    }
}

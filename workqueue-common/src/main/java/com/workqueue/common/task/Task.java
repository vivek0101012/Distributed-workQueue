package com.workqueue.common.task;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
public record Task(String id,
                   String type,
                   Map<String,Object> payload,
                   int attempt,
                   int maxAttempts,
                   long enqueuedAt,
                   String lastError
                     ) {


    public static Task create(String type, Map<String, Object> payload, int maxAttempts){

        return new Task(
                UUID.randomUUID().toString(),
                type,
                payload==null?Map.of():payload,
                0,
                maxAttempts,
                Instant.now().toEpochMilli(),
                null

        );


    }

    public Task failed(String err){
        return new Task(id,
                type,
                payload,
                attempt + 1,
                maxAttempts,
                enqueuedAt,
                err);

    }

    public boolean hasAttempLeft(){
       return attempt < maxAttempts;
    }

}

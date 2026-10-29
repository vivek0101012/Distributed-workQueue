package com.workqueue.common.queue;

import com.workqueue.common.task.Task;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
@Component
public class RedisQueueClient implements  QueueClient {

    private  final StringRedisTemplate redis;
    private final ObjectMapper json;

    public RedisQueueClient(StringRedisTemplate redis, ObjectMapper json) {
        this.redis = redis;
        this.json = json;
    }


    @Override
    public void push(Task task) {

        try {
            String text = json.writeValueAsString(task);
            redis.opsForList().rightPush(Keys.MAIN,text);
        }
        catch (Exception e){
            throw new RuntimeException("Failed to push task to queue", e);
        }
    }

    @Override
    public long mainLength() {
        Long len=redis.opsForList().size(Keys.MAIN);
        return len==null?0:len;
    }
}

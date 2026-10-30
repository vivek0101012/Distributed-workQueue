package com.workqueue.common.queue;

import com.fasterxml.jackson.core.JacksonException;
import com.workqueue.common.task.Claimed;
import com.workqueue.common.task.Task;
import com.workqueue.common.task.UnreadableTaskException;
import org.springframework.data.redis.connection.RedisListCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Optional;

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
//during claim move the task from main->processing
    @Override
    public Optional<Claimed> claim(Duration timeout, String workerId) {
        String raw= redis.opsForList().move(
                Keys.MAIN, RedisListCommands.Direction.LEFT,
                Keys.PROCESSING, RedisListCommands.Direction.RIGHT
        );

        if (raw==null) return Optional.empty();
        Task task= parse(raw);
        //setting the hash for the claimed task in inflight queue
        redis.opsForHash().put(Keys.INFLIGHT, task.id(), workerId + "|" + System.currentTimeMillis());
        return Optional.of(new Claimed(raw,task));
    }

    private Task parse(String raw) {
        try {
            return json.readValue(raw, Task.class);
        } catch (JacksonException e) {
            throw new UnreadableTaskException(raw, e);
        }
    }
}

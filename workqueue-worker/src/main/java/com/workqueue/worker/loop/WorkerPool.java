package com.workqueue.worker.loop;

import com.workqueue.common.queue.QueueClient;
import com.workqueue.worker.WorkerProperties;
import com.workqueue.worker.exec.TaskRunner;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Scheduler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class WorkerPool {

    private static final Logger log = LoggerFactory.getLogger(WorkerPool.class);

    private final List<WorkerLoop>loops;
    private final QueueClient queue;
    private final TaskRunner runner;
    private final WorkerProperties workerProperties;

    public WorkerPool(List<WorkerLoop> loops, QueueClient queue, TaskRunner runner, WorkerProperties workerProperties) {
        this.loops = loops;
        this.queue = queue;
        this.runner = runner;
        this.workerProperties = workerProperties;
    }
    private  ExecutorService threads;

    //Dont touch
    private static String newWorkerId() {
        String host;
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            host = "worker";
        }
        return host + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    @EventListener(ApplicationReadyEvent.class)
    public  void start(){
        String workerId= newWorkerId();
        Duration pollTimeout= Duration.ofSeconds(workerProperties.pollTimeoutSeconds());
        threads= Executors.newFixedThreadPool(workerProperties.workers());

        for(int i=0;i<workerProperties.workers();i++){
            WorkerLoop loop= new WorkerLoop(queue,runner,workerId,pollTimeout);
            loops.add(loop);
            threads.submit(loop);
        }
        log.info("Worker {} started with {} loops", workerId, workerProperties.workers());
    }
    @PreDestroy
    public  void stop(){

        if (threads==null){
            return;
        }
        log.info("Worker stopping");
        loops.forEach(WorkerLoop::stop);
        threads.shutdown();

        try {
            if (!threads.awaitTermination(workerProperties.shutdownTimeoutSeconds(), TimeUnit.SECONDS)) {
                log.warn("Some tasks were still running; they stay in queue:processing");
                threads.shutdownNow();
            }
        } catch (InterruptedException e) {
            threads.shutdownNow();
            Thread.currentThread().interrupt();
        }


        }



}
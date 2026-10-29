package com.workqueue.common.queue;


public final class Keys {

    private Keys (){}
    public static final String MAIN = "queue:main";
    public static final String PROCESSING = "queue:processing";
    public static final String DELAYED = "queue:delayed";
    public static final String DEAD = "queue:dead";
    public static final String INFLIGHT = "queue:inflight";

    public static final String PROCESSED_PREFIX = "processed:";
    public static final String STATS_DONE = "stats:done";
    public static final String STATS_FAILED = "stats:failed";


    public static String processed(String taskId){
        return PROCESSED_PREFIX +taskId;
    }
}


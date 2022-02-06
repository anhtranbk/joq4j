package org.joq4j.examples;

import org.joq4j.AsyncResult;
import org.joq4j.JobQueue;
import org.joq4j.backend.StorageBackend;
import org.joq4j.broker.Broker;
import org.joq4j.common.utils.Threads;
import org.joq4j.redis.backend.RedisBackend;
import org.joq4j.redis.broker.RedisBroker;

public class RedisSampleApp {

    public static void main(String[] args) {
        Broker broker = new RedisBroker("redis://localhost:6379/0");
        StorageBackend backend = new RedisBackend("redis://localhost:6379/0");
        JobQueue queue = JobQueue.builder()
                .broker(broker)
                .backend(backend)
                .build();
        AsyncResult result = queue.enqueue(() -> "Hello, World!");

        while (!result.isDone()) {
            Threads.sleep(1000);
            System.out.println("Job State:" + backend.getState(result.getJobId()));
        }
        System.out.println(result.get());
    }
}

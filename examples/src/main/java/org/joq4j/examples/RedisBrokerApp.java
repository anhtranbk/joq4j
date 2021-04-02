package org.joq4j.examples;

import org.joq4j.AsyncResult;
import org.joq4j.JobQueue;
import org.joq4j.Task;
import org.joq4j.broker.Broker;
import org.joq4j.redis.broker.RedisBroker;

public class RedisBrokerApp {

    public static void main(String[] args) throws Exception {
        Broker broker = new RedisBroker();
        JobQueue queue = JobQueue.builder()
                .broker(broker)
                .build();
        Task task = (Task) () -> "Hello, World!";
        AsyncResult result = queue.enqueue(task);
        while (!result.isDone()) {
            Thread.sleep(1000);
            System.out.println("Remote job is running");
        }
        System.out.println(result.get());
    }
}

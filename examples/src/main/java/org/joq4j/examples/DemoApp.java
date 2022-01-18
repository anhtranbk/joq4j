package org.joq4j.examples;

import org.joq4j.AsyncResult;
import org.joq4j.JobQueue;
import org.joq4j.broker.Broker;
import org.joq4j.broker.MemoryBroker;
import org.joq4j.common.utils.Threads;

public class DemoApp {

    public static void main(String[] args) {
        Broker broker = new MemoryBroker();
        JobQueue queue = JobQueue.builder()
                .broker(broker)
                .build();
        AsyncResult result = queue.enqueue(() -> "Hello, World!");

        while (!result.isDone()) {
            Threads.sleep(1000);
            System.out.println("Remote job is running");
        }
        System.out.println(result.get());
    }
}

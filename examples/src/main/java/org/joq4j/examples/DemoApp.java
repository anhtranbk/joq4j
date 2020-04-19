package org.joq4j.examples;

import org.joq4j.AsyncResult;
import org.joq4j.Broker;
import org.joq4j.JobQueue;
import org.joq4j.Task;
import org.joq4j.broker.RedisBroker;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class DemoApp {

    public static void main(String[] args) throws Exception {
        Broker broker = new RedisBroker("localhost");
        JobQueue queue = JobQueue.builder().broker(broker).build();
        Task task = new Task() {
            @Override
            public boolean isCancelable() {
                return false;
            }

            @Override
            public Object call() throws Exception {
                return "Hello, World!";
            }
        };
        AsyncResult result = queue.enqueue(task);
        while (!result.isDone()) {
            Thread.sleep(1000);
            System.out.println("Remote job is running");
        }
        System.out.println(result.get());
    }
}

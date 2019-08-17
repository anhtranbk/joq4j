package org.joq4j.examples;

import org.joq4j.AsyncResult;
import org.joq4j.AsyncTask;
import org.joq4j.Broker;
import org.joq4j.JobQueue;
import org.joq4j.QueueOptions;
import org.joq4j.broker.RedisBroker;
import org.joq4j.core.JobQueueImpl;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class DemoApp {

    public static void main(String[] args) throws Exception {
        Broker broker = new RedisBroker("localhost");
        JobQueue queue = new JobQueueImpl("default", new QueueOptions(), broker);
        AsyncTask task = new AsyncTask() {
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

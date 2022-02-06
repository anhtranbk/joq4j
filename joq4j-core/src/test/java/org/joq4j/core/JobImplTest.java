package org.joq4j.core;

import org.joq4j.Job;
import org.joq4j.JobQueue;
import org.joq4j.Task;
import org.joq4j.backend.MemoryBackend;
import org.joq4j.broker.MemoryBroker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class JobImplTest {

    private static class SimpleTask implements Task {

        private final int a;
        private final int b;

        SimpleTask(int a, int b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public Object call() throws Exception {
            return a + b;
        }
    }

    private JobQueueImpl queue;
    private Task task = new SimpleTask(4, 9);

    @Before
    public void setUp() throws Exception {
        task = new SimpleTask(4, 9);
        queue = (JobQueueImpl) JobQueue.builder()
                .name("test")
                .broker(new MemoryBroker())
                .backend(new MemoryBackend())
                .build();
    }

    @After
    public void tearDown() throws Exception {
        queue.backend().close();
        queue.broker().close();
    }

    @Test
    public void getId() {
        Job job1 = Job.builder().task(task).build();
        Job job2 = Job.builder().task(task).build();
        assertNotEquals(job1.id(), job2.id());
        assertNotNull(UUID.fromString(job1.id()));
    }

    @Test
    public void getWorker() {
    }

    @Test
    public void perform() {
    }
}
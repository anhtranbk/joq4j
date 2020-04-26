package org.joq4j.core;

import org.joq4j.JobQueue;
import org.joq4j.Task;
import org.joq4j.TaskOptions;
import org.joq4j.backend.MemoryBackend;
import org.joq4j.broker.MemoryBroker;
import org.joq4j.encoding.JavaTaskSerializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
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
                .defaultTimeout(500)
                .taskSerializer(new JavaTaskSerializer())
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
        JobImpl job1 = new JobImpl(queue.name(), task);
        JobImpl job2 = new JobImpl(queue.name(), task);
        assertNotEquals(job1.id(), job2.id());
        assertNotNull(UUID.fromString(job1.id()));
    }

    @Test
    public void getOptions() {
        TaskOptions options = new TaskOptions()
                .name("test")
                .description("demo job");
        JobImpl job = new JobImpl(queue.name(), task, options);
        assertEquals(options.name(), job.options().name());
        assertEquals(options.description(), job.options().description());
    }

    @Test
    public void getQueueName() {
        JobImpl job = new JobImpl(queue.name(), task);
        assertEquals(queue.name(), job.queueName());
    }

    @Test
    public void getWorker() {
    }

    @Test
    public void perform() {
    }
}
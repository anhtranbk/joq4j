package org.joq4j.core;

import org.joq4j.Job;
import org.joq4j.TaskOptions;
import org.joq4j.JobQueue;
import org.joq4j.Task;
import org.joq4j.backend.MemoryBackend;
import org.joq4j.broker.MemoryBroker;
import org.joq4j.encoding.JavaTaskSerializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

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
    private Task task;

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
        queue.getBackend().close();
        queue.getBroker().close();
    }

    @Test
    public void getId() {
        JobImpl job1 = new JobImpl(queue);
        JobImpl job2 = new JobImpl(queue);
        assertNotEquals(job1.id(), job2.id());
        assertNotNull(UUID.fromString(job1.id()));
    }

    @Test
    public void getOptions() {
        TaskOptions options = new TaskOptions()
                .name("test")
                .description("demo job");
        JobImpl job = new JobImpl(queue, options);
        assertEquals(options.name(), job.options().name());
        assertEquals(options.description(), job.options().description());
    }

    @Test
    public void getQueueName() {
        JobImpl job = new JobImpl(queue);
        assertEquals(queue.getName(), job.queueName());
    }

    @Test
    public void getWorker() {
    }

    @Test
    public void dumps() {
        TaskOptions options = new TaskOptions()
                .name("test")
                .description("demo job");
        JobImpl job = new JobImpl(queue, options);
        Map<String, String> map = job.dumps();

        assertEquals(job.id(), map.get(Job.FIELD_ID));
        assertEquals(options.name(), map.get(Job.FIELD_NAME));
        assertEquals(options.description(), map.get(Job.FIELD_DESCRIPTION));
        assertEquals(
                options.timeout(),
                Long.parseLong(map.get(Job.FIELD_TIMEOUT))
        );
        assertEquals(
                options.maxRetries(),
                Integer.parseInt(map.get(Job.FIELD_MAX_RETRIES))
        );
        assertEquals(
                options.retryDelay(),
                Long.parseLong(map.get(Job.FIELD_RETRY_DELAY))
        );
        assertEquals(
                options.priority(),
                Integer.parseInt(map.get(Job.FIELD_PRIORITY))
        );
        assertEquals(
                queue.getTaskSerializer().writeAsBase64(task, Object.class),
                map.get(Job.FIELD_TASK)
        );
    }

    @Test
    public void loads() {
        String id = UUID.randomUUID().toString();
        String data = queue.getTaskSerializer().writeAsBase64(task, Object.class);

        Map<String, String> map = new HashMap<>();
        map.put(Job.FIELD_ID, id);
        map.put(Job.FIELD_NAME, "test");
        map.put(Job.FIELD_DESCRIPTION, "demo job");
        map.put(Job.FIELD_TIMEOUT, "500");
        map.put(Job.FIELD_MAX_RETRIES, "5");
        map.put(Job.FIELD_RETRY_DELAY, "100");
        map.put(Job.FIELD_PRIORITY, "2");
        map.put(Job.FIELD_TASK, data);

        JobImpl job = new JobImpl(queue);
        job.loads(map);

        assertEquals(id, job.id());
        assertEquals("test", job.options().name());
        assertEquals("demo job", job.options().description());
        assertEquals(500, job.options().timeout());
        assertEquals(5, job.options().maxRetries());
        assertEquals(100, job.options().retryDelay());
        assertEquals(2, job.options().priority());
        assertEquals(task, job.task());
    }

    @Test
    public void perform() {
    }
}
package org.joq4j.core;

import org.joq4j.AsyncResult;
import org.joq4j.Job;
import org.joq4j.JobOptions;
import org.joq4j.JobQueue;
import org.joq4j.Task;
import org.joq4j.backend.MemoryBackend;
import org.joq4j.broker.MemoryBroker;
import org.joq4j.encoding.JavaSerializer;
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
        public boolean isCancelable() {
            return false;
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
                .serializer(new JavaSerializer())
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
        assertNotEquals(job1.getId(), job2.getId());
        assertNotNull(UUID.fromString(job1.getId()));
    }

    @Test
    public void getOptions() {
        JobOptions options = new JobOptions()
                .setName("test")
                .setDescription("demo job");
        JobImpl job = new JobImpl(queue, options);
        assertEquals(options.getName(), job.getOptions().getName());
        assertEquals(options.getDescription(), job.getOptions().getDescription());
    }

    @Test
    public void getQueueName() {
        JobImpl job = new JobImpl(queue);
        assertEquals(queue.getName(), job.getQueueName());
    }

    @Test
    public void getWorker() {
    }

    @Test
    public void dumps() {
        JobOptions options = new JobOptions()
                .setName("test")
                .setDescription("demo job");
        JobImpl job = new JobImpl(queue, options);
        Map<String, String> map = job.dumps();

        assertEquals(job.getId(), map.get(Job.FIELD_ID));
        assertEquals(options.getName(), map.get(Job.FIELD_NAME));
        assertEquals(options.getDescription(), map.get(Job.FIELD_DESCRIPTION));
        assertEquals(
                options.getJobTimeout(),
                Long.parseLong(map.get(Job.FIELD_TIMEOUT))
        );
        assertEquals(
                options.getMaxRetries(),
                Integer.parseInt(map.get(Job.FIELD_MAX_RETRIES))
        );
        assertEquals(
                options.getRetryDelay(),
                Long.parseLong(map.get(Job.FIELD_RETRY_DELAY))
        );
        assertEquals(
                options.getPriority(),
                Integer.parseInt(map.get(Job.FIELD_PRIORITY))
        );
        assertEquals(
                queue.getSerializer().writeAsBase64(task, Object.class),
                map.get(Job.FIELD_DATA)
        );
    }

    @Test
    public void loads() {
        String id = UUID.randomUUID().toString();
        String data = queue.getSerializer().writeAsBase64(task, Object.class);

        Map<String, String> map = new HashMap<>();
        map.put(Job.FIELD_ID, id);
        map.put(Job.FIELD_NAME, "test");
        map.put(Job.FIELD_DESCRIPTION, "demo job");
        map.put(Job.FIELD_TIMEOUT, "500");
        map.put(Job.FIELD_MAX_RETRIES, "5");
        map.put(Job.FIELD_RETRY_DELAY, "100");
        map.put(Job.FIELD_PRIORITY, "2");
        map.put(Job.FIELD_DATA, data);

        JobImpl job = new JobImpl(queue);
        job.loads(map);

        assertEquals(id, job.getId());
        assertEquals("test", job.getOptions().getName());
        assertEquals("demo job", job.getOptions().getDescription());
        assertEquals(500, job.getOptions().getJobTimeout());
        assertEquals(5, job.getOptions().getMaxRetries());
        assertEquals(100, job.getOptions().getRetryDelay());
        assertEquals(2, job.getOptions().getPriority());
        assertEquals(task, job.getTask());
    }

    @Test
    public void perform() {
    }
}
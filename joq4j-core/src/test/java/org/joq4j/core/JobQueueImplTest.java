package org.joq4j.core;

import org.joq4j.AsyncResult;
import org.joq4j.Task;
import org.joq4j.Job;
import org.joq4j.TaskOptions;
import org.joq4j.JobQueue;
import org.joq4j.backend.MemoryBackend;
import org.joq4j.broker.MemoryBroker;
import org.joq4j.encoding.JavaTaskSerializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JobQueueImplTest {

    private JobQueueImpl queue;
    private Task task;

    @Before
    public void setUp() throws Exception {
        task = Task.doNothing();
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
        queue.getBroker().close();
        queue.getBackend().close();
    }

    @Test
    public void getName() {
        assertEquals("test", queue.getName());
    }

    @Test
    public void getPendingJobs() {
        int numJobs = 5;
        List<String> jobIds = new ArrayList<>(numJobs);
        for (int i = 0; i < numJobs; i++) {
            AsyncResult ar = queue.enqueue(task, new TaskOptions()
                    .name("job_" + i)
                    .description("job_description_" + i)
                    .timeout(100 * i));
            jobIds.add(ar.getJobId());
        }
        List<Job> jobs = queue.getPendingJobs();
        for (int i = 0; i < numJobs; i++) {
            Job job = jobs.get(i);
            assertEquals(jobIds.get(i), job.id());
            assertEquals("job_" + i, job.options().name());
            assertEquals("job_description_" + i, job.options().description());
            assertEquals(100 * i, job.options().timeout());
        }
        assertEquals(numJobs, jobs.size());
    }

    @Test
    public void getTotalJob() {
        int numJobs = 5;
        for (int i = 0; i < numJobs; i++) {
            queue.enqueue(task, new TaskOptions().name("job_" + i));
        }
        assertEquals(numJobs, queue.getTotalJob());
    }

    @Test
    public void isEmpty() {
        assertTrue(queue.isEmpty());
        queue.enqueue(task, new TaskOptions());
        assertFalse(queue.isEmpty());
    }

    @Test
    public void enqueue() {
        int numJobs = 5;
        for (int i = 0; i < numJobs; i++) {
            AsyncResult ar = queue.enqueue(task, new TaskOptions()
                    .name("job_" + i)
                    .description("job_description_" + i));

            List<Job> jobs = queue.getPendingJobs();
            Job job = queue.nextJob("test");
            assertEquals(job.id(), jobs.get(0).id());
            assertEquals(job.id(), ar.getJobId());
        }
    }

    @Test
    public void nextJob() {
        int numJobs = 5;
        for (int i = 0; i < numJobs; i++) {
            queue.enqueue(task, new TaskOptions()
                    .name("job_" + i)
                    .description("job_description_" + i));
        }
        queue.enqueue(task, new TaskOptions().name("job_test"));

        for (int i = 0; i < numJobs; i++) {
            Job job = queue.nextJob("test");
            assertEquals("job_" + i, job.options().name());
            assertEquals("job_description_" + i, job.options().description());
        }
        Job job = queue.nextJob("test");
        assertEquals("job_test", job.options().name());
        assertTrue(job.options().description().isEmpty());
    }

    @Test
    public void clear() {
        int numJobs = 7;
        for (int i = 0; i < numJobs; i++) {
            queue.enqueue(task, new TaskOptions()
                    .name("job_" + i)
                    .description("job_description_" + i));
        }
        queue.clear();
        assertTrue(queue.isEmpty());
    }
}
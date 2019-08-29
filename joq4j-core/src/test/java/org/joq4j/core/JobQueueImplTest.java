package org.joq4j.core;

import org.joq4j.AsyncResult;
import org.joq4j.Task;
import org.joq4j.Broker;
import org.joq4j.Job;
import org.joq4j.JobOptions;
import org.joq4j.JobQueue;
import org.joq4j.backend.MemoryBackend;
import org.joq4j.broker.MemoryBroker;
import org.joq4j.common.utils.Utils;
import org.joq4j.encoding.JavaSerializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JobQueueImplTest {

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
    private SimpleTask simpleTask;

    @Before
    public void setUp() throws Exception {
        simpleTask = new SimpleTask(4, 9);
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
        queue.getBroker().close();
        queue.getBackend().close();
    }

    @Test
    public void getName() {
        assertEquals("test", queue.getName());
    }

    @Test
    public void getAllJobIds() {
        int numJobs = 5;
        List<String> originJobIds = new ArrayList<>(numJobs);
        for (int i = 0; i < numJobs; i++) {
            AsyncResult ar = queue.enqueue(simpleTask);
            originJobIds.add(ar.getJobId());
        }

        List<String> jobIds = queue.getAllJobIds();
        assertEquals(numJobs, jobIds.size());
        for (int i = 0; i < numJobs; i++) {
            assertEquals(originJobIds.get(i), jobIds.get(i));
        }
    }

    @Test
    public void getAllJobs() {
        int numJobs = 5;
        List<String> jobIds = new ArrayList<>(numJobs);
        for (int i = 0; i < numJobs; i++) {
            AsyncResult ar = queue.enqueue(simpleTask, new JobOptions()
                    .setName("job_" + i)
                    .setDescription("job_description_" + i)
                    .setJobTimeout(100 * i));
            jobIds.add(ar.getJobId());
        }
        List<Job> jobs = queue.getAllJobs();
        for (int i = 0; i < numJobs; i++) {
            Job job = jobs.get(i);
            assertEquals(jobIds.get(i), job.getId());
            assertEquals("job_" + i, job.getOptions().getName());
            assertEquals("job_description_" + i, job.getOptions().getDescription());
            assertEquals(100 * i, job.getOptions().getJobTimeout());
        }
        assertEquals(numJobs, jobs.size());
    }

    @Test
    public void getTotalJob() {
        int numJobs = 5;
        for (int i = 0; i < numJobs; i++) {
            queue.enqueue(simpleTask, new JobOptions().setName("job_" + i));
        }
        assertEquals(numJobs, queue.getTotalJob());
    }

    @Test
    public void isEmpty() {
        assertTrue(queue.isEmpty());
        queue.enqueue(simpleTask, new JobOptions());
        assertFalse(queue.isEmpty());
    }

    @Test
    public void enqueue() {
        int numJobs = 5;
        for (int i = 0; i < numJobs; i++) {
            AsyncResult ar = queue.enqueue(simpleTask, new JobOptions()
                    .setName("job_" + i)
                    .setDescription("job_description_" + i));

            List<String> jobIds = queue.getAllJobIds();
            Job job = queue.nextJob("test");
            assertEquals(job.getId(), jobIds.get(0));
            assertEquals(job.getId(), ar.getJobId());
        }
    }

    @Test
    public void nextJob() {
        int numJobs = 5;
        for (int i = 0; i < numJobs; i++) {
            queue.enqueue(simpleTask, new JobOptions()
                    .setName("job_" + i)
                    .setDescription("job_description_" + i));
        }
        queue.enqueue(simpleTask, new JobOptions().setName("job_test"));

        for (int i = 0; i < numJobs; i++) {
            Job job = queue.nextJob("test");
            assertEquals("job_" + i, job.getOptions().getName());
            assertEquals("job_description_" + i, job.getOptions().getDescription());
        }
        Job job = queue.nextJob("test");
        assertEquals("job_test", job.getOptions().getName());
        assertTrue(job.getOptions().getDescription().isEmpty());
    }

    @Test
    public void clear() {
        int numJobs = 7;
        for (int i = 0; i < numJobs; i++) {
            queue.enqueue(simpleTask, new JobOptions()
                    .setName("job_" + i)
                    .setDescription("job_description_" + i));
        }
        queue.clear();
        assertTrue(queue.isEmpty());
    }
}
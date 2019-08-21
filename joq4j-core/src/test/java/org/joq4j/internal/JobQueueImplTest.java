package org.joq4j.internal;

import org.joq4j.AsyncTask;
import org.joq4j.Broker;
import org.joq4j.Job;
import org.joq4j.JobOptions;
import org.joq4j.JobQueue;
import org.joq4j.JobStatus;
import org.joq4j.broker.MemoryBroker;
import org.joq4j.common.utils.Utils;
import org.joq4j.serde.JavaSerdeFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JobQueueImplTest {

    private static class SimpleTask implements AsyncTask {

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
        Broker broker = new MemoryBroker();
        simpleTask = new SimpleTask(4, 9);

        queue = (JobQueueImpl) JobQueue.builder()
                .name("test")
                .async(true)
                .defaultTimeout(500)
                .serializationFactory(new JavaSerdeFactory())
                .broker(broker)
                .build();
    }

    @After
    public void tearDown() throws Exception {
        queue.getBroker().close();
    }

    @Test
    public void getName() {
        assertEquals("test", queue.getName());
    }

    @Test
    public void getAllJobIds() {
        int numJobs = 5;
        for (int i = 0; i < numJobs; i++) {
            queue.enqueue(simpleTask, new JobOptions().setJobId("job_" + i));
        }
        List<String> jobIds = queue.getAllJobIds();
        for (int i = 0; i < numJobs; i++) {
            assertEquals("job_" + i, jobIds.get(i));
        }
        assertEquals(numJobs, jobIds.size());
    }

    @Test
    public void getAllJobs() {
        int numJobs = 5;
        for (int i = 0; i < numJobs; i++) {
            queue.enqueue(simpleTask, new JobOptions().setJobId("job_" + i)
                    .setDescription("job_description_" + i)
                    .setJobTimeout(100 * i));
        }
        List<Job> jobs = queue.getAllJobs();
        for (int i = 0; i < numJobs; i++) {
            Job job = jobs.get(i);
            assertEquals("job_" + i, job.getId());
            assertEquals("job_description_" + i, job.getOptions().getDescription());
            assertEquals(100 * i, job.getOptions().getJobTimeout());
        }
        assertEquals(numJobs, jobs.size());
    }

    @Test
    public void isExists() {
        queue.enqueue(simpleTask);
        String jobId = queue.getAllJobIds().get(0);
        assertTrue(queue.isExists(jobId));

        queue.enqueue(simpleTask, new JobOptions().setJobId("job_test"));
        assertTrue(queue.isExists("job_test"));
    }

    @Test
    public void restoreJob() {
        queue.enqueue(simpleTask, new JobOptions()
                .setJobId("job_test")
                .setJobTimeout(600)
                .setDescription("job_description"));

        Job job = queue.restoreJob("job_test");
        assertEquals("job_test", job.getId());
        assertEquals("job_description", job.getOptions().getDescription());
        assertEquals(600, job.getOptions().getJobTimeout());
    }

    @Test
    public void getTotalJob() {
        int numJobs = 5;
        for (int i = 0; i < numJobs; i++) {
            queue.enqueue(simpleTask, new JobOptions().setJobId("job_" + i));
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
            queue.enqueue(simpleTask, new JobOptions()
                    .setJobId("job_" + i)
                    .setDescription("job_description_" + i));

            Job job = queue.restoreJob("job_" + i);
            assertEquals(JobStatus.QUEUED, job.getStatus());
            assertNotNull(job.getEnqueuedAt());
            assertEquals(job.getId(), Utils.lastItem(queue.getAllJobIds()));
        }
    }

    @Test
    public void nextJob() {
        int numJobs = 5;
        for (int i = 0; i < numJobs; i++) {
            queue.enqueue(simpleTask, new JobOptions()
                    .setJobId("job_" + i)
                    .setDescription("job_description_" + i));
        }
        queue.enqueue(simpleTask, new JobOptions().setJobId("job_test"));

        for (int i = 0; i < numJobs; i++) {
            Job job = queue.nextJob("test");
            assertEquals("job_" + i, job.getId());
            assertEquals("job_description_" + i, job.getOptions().getDescription());
        }
        Job job = queue.nextJob("test");
        assertEquals("job_test", job.getId());
        assertTrue(job.getOptions().getDescription().isEmpty());
    }

    @Test
    public void removeJob() {
        int numJobs = 7;
        for (int i = 0; i < numJobs; i++) {
            queue.enqueue(simpleTask, new JobOptions()
                    .setJobId("job_" + i)
                    .setDescription("job_description_" + i));
        }

        int last = numJobs - 1;

        Job jobFirst = queue.removeJob("job_0");
        assertEquals("job_description_0", jobFirst.getOptions().getDescription());

        Job jobLast = queue.removeJob("job_" + last);
        assertEquals("job_description_" + last, jobLast.getOptions().getDescription());

        assertEquals(numJobs - 2, queue.getTotalJob());

        assertFalse(queue.isExists("job_0"));
        assertFalse(queue.isExists("job_" + last));
        for (int i = 1; i < numJobs - 1; i++) {
            assertTrue(queue.isExists("job_" + i));
        }

        List<String> jobIds = queue.getAllJobIds();
        assertFalse(jobIds.contains(jobFirst.getId()));
        assertFalse(jobIds.contains(jobLast.getId()));

        Job job = queue.nextJob("test");
        assertEquals("job_1", job.getId());
    }

    @Test
    public void clear() {
        int numJobs = 7;
        for (int i = 0; i < numJobs; i++) {
            queue.enqueue(simpleTask, new JobOptions()
                    .setJobId("job_" + i)
                    .setDescription("job_description_" + i));
        }
        queue.clear();

        assertTrue(queue.isEmpty());
        assertEquals(0,queue.getTotalJob());
    }
}
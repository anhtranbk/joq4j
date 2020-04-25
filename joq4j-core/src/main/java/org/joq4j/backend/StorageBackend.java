package org.joq4j.backend;

import org.joq4j.Job;
import org.joq4j.JobExecutionException;
import org.joq4j.JobState;
import org.joq4j.common.utils.Threads;
import org.joq4j.encoding.TaskSerializer;

import java.io.Closeable;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface StorageBackend extends Closeable {

    TaskSerializer getTaskSerializer();

    void storeJob(Job job);

    Job getJob(String jobId);

    JobState getState(String jobId);

    void setState(String jobId, JobState status);

    void markAsSuccess(String jobId, Object result);

    void markAsFailure(String jobId, Throwable error);

    JobExecutionException getError(String jobId);

    Object getResult(String jobId);

    default Object waitForJobResult(String jobId, long timeout) {
        long now;
        do {
            if (isJobDone(jobId)) {
                Object result = getResult(jobId);
                if (result == null) throw getError(jobId);
                return result;
            }
            Threads.sleep(10);
            now = System.currentTimeMillis();
        } while (System.currentTimeMillis() - now > timeout || timeout == 0);
        throw new JobExecutionException("Job timeout");
    }

    default boolean isJobDone(String jobId) {
        JobState status = getState(jobId);
        return status.equals(JobState.SUCCESS) || status.equals(JobState.FAILURE);
    }

    default void ensureJobFinished(String jobId) {
        if (!isJobDone(jobId)) {
            throw new IllegalStateException("Job is not finished " + jobId);
        }
    }
}

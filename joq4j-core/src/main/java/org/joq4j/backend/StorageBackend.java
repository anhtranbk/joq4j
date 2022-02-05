package org.joq4j.backend;

import org.joq4j.Job;
import org.joq4j.JobExecutionException;
import org.joq4j.JobState;
import org.joq4j.common.utils.Threads;

import java.io.Closeable;

public interface StorageBackend extends Closeable {

    String FIELD_STATE = "state";
    String FIELD_RESULT = "result";
    String FIELD_ERROR = "error";
    String FIELD_QUEUED_AT = "queued_at";
    String FIELD_STARTED_AT = "started_at";
    String FIELD_FINISHED_AT = "finished_at";

    String FIELD_WORKER = "worker";
    String FIELD_TASK = "task";
    String FIELD_ID = "id";
    String FIELD_NAME = "name";
    String FIELD_DESCRIPTION = "desc";

    void storeJob(Job job);

    Job fetchJob(String jobId);

    JobState getState(String jobId);

    void setState(String jobId, JobState status);

    void setWorker(String jobId, String worker);

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

    default void ensureJobFinished(String jobId) {
        if (!isJobDone(jobId)) {
            throw new IllegalStateException("Job is not finished " + jobId);
        }
    }

    default boolean isJobStarted(String jobId) {
        return this.getState(jobId) == JobState.STARTED;
    }

    default boolean isJobDone(String jobId) {
        JobState state = getState(jobId);
        return state.equals(JobState.SUCCESS)
                || state.equals(JobState.FAILURE)
                || state.equals(JobState.CANCELLED);
    }

    default boolean isJobPaused(String jobId) {
        return this.getState(jobId) == JobState.PAUSED;
    }

    default boolean isJobSuccess(String jobId) {
        return this.getState(jobId) == JobState.SUCCESS;
    }

    default boolean isFailure(String jobId) {
        return this.getState(jobId) == JobState.FAILURE;
    }

    default boolean isJobCancelled(String jobId) {
        return this.getState(jobId) == JobState.CANCELLED;
    }
}

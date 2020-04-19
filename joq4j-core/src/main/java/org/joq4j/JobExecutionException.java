package org.joq4j;

public class JobExecutionException extends Joq4jException {

    public JobExecutionException(String message) {
        super(message);
    }

    public JobExecutionException(String message, Object... args) {
        super(message, args);
    }

    public JobExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobExecutionException(String message, Throwable cause, Object... args) {
        super(message, cause, args);
    }

    public JobExecutionException(Throwable cause) {
        super(cause);
    }
}

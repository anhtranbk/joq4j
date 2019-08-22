package org.joq4j;

public class JobExecutionException extends Joq4jException {

    public JobExecutionException() {
    }

    public JobExecutionException(String s) {
        super(s);
    }

    public JobExecutionException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public JobExecutionException(Throwable throwable) {
        super(throwable);
    }
}

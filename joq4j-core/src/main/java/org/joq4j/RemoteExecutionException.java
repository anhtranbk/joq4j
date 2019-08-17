package org.joq4j;

public class RemoteExecutionException extends RuntimeException {

    public RemoteExecutionException() {
    }

    public RemoteExecutionException(String s) {
        super(s);
    }

    public RemoteExecutionException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public RemoteExecutionException(Throwable throwable) {
        super(throwable);
    }
}

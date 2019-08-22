package org.joq4j;

public class Joq4jException extends RuntimeException {

    public Joq4jException() {
    }

    public Joq4jException(String s) {
        super(s);
    }

    public Joq4jException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public Joq4jException(Throwable throwable) {
        super(throwable);
    }
}

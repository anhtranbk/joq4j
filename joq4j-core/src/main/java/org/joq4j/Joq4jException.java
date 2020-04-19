package org.joq4j;

import org.joq4j.common.utils.Strings;

public class Joq4jException extends RuntimeException {

    private static final long serialVersionUID = -8711375282196157058L;

    public Joq4jException(String message) {
        super(message);
    }

    public Joq4jException(String message, Object... args) {
        super(String.format(message, args));
    }

    public Joq4jException(String message, Throwable cause) {
        super(message, cause);
    }

    public Joq4jException(String message, Throwable cause, Object... args) {
        super(Strings.format(message, args), cause);
    }

    public Joq4jException(Throwable cause) {
        super(cause);
    }
}

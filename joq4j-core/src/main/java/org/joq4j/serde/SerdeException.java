package org.joq4j.serde;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class SerdeException extends RuntimeException {

    public SerdeException() {
    }

    public SerdeException(String message) {
        super(message);
    }

    public SerdeException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerdeException(Throwable cause) {
        super(cause);
    }
}

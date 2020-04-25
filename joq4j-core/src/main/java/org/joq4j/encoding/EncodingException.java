package org.joq4j.encoding;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class EncodingException extends RuntimeException {

    public EncodingException() {
    }

    public EncodingException(String message) {
        super(message);
    }

    public EncodingException(String message, Throwable cause) {
        super(message, cause);
    }

    public EncodingException(Throwable cause) {
        super(cause);
    }
}

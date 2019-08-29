package org.joq4j.common.config;

public class ConfigException extends RuntimeException {

    public ConfigException() {
    }

    public ConfigException(String s) {
        super(s);
    }

    public ConfigException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ConfigException(Throwable throwable) {
        super(throwable);
    }
}

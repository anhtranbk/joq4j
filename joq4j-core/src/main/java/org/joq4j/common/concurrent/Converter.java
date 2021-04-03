package org.joq4j.common.concurrent;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface Converter<S, R> {

    R convert(S source);
}

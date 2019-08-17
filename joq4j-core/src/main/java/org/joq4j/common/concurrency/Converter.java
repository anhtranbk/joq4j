package org.joq4j.common.concurrency;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface Converter<S, R> {

    R convert(S source);
}

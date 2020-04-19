package org.joq4j;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface JobCallback {

    void onSuccess(Object result);

    void onFailure(Throwable throwable);
}

package org.joq4j;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface JobCallback {

    void onSuccess(Object result);

    void onFailure(Throwable throwable);
}

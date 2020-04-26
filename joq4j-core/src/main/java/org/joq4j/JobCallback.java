package org.joq4j;

public interface JobCallback {

    void onSuccess(Object result);

    void onFailure(Throwable throwable);
}

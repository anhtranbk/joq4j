package org.joq4j;

import java.io.Serializable;
import java.util.concurrent.Callable;

public interface AsyncTask extends Serializable, Callable<Object> {

    boolean isCancelable();
}

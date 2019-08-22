package org.joq4j;

import java.io.Serializable;
import java.util.concurrent.Callable;

public interface Task extends Serializable, Callable<Object> {

    boolean isCancelable();
}

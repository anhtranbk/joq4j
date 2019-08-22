package org.joq4j.core;

import java.util.HashMap;
import java.util.Map;

public class JobFieldMap extends HashMap<String, String> {

    public JobFieldMap(int i, float v) {
        super(i, v);
    }

    public JobFieldMap(int i) {
        super(i);
    }

    public JobFieldMap() {
    }

    public JobFieldMap(Map<? extends String, ? extends String> map) {
        super(map);
    }
}

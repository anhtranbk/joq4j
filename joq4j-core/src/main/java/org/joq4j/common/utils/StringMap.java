package org.joq4j.common.utils;

import java.util.HashMap;
import java.util.Map;

public class StringMap extends HashMap<String, String> {

    public StringMap(int i, float v) {
        super(i, v);
    }

    public StringMap(int i) {
        super(i);
    }

    public StringMap() {
    }

    public StringMap(Map<? extends String, ? extends String> map) {
        super(map);
    }
}

package org.joq4j.common.mb;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public final class Record {

    private final byte[] data;
    private final long timestamp;

    public Record(byte[] data, long timestamp) {
        this.data = data;
        this.timestamp = timestamp;
    }

    public final byte[] data() {
        return this.data;
    }

    public final long timestamp() {
        return this.timestamp;
    }
}

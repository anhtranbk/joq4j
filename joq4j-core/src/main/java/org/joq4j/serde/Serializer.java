package org.joq4j.serde;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Base64;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface Serializer {

    void serialize(Object input, OutputStream out);

    default byte[] serialize(Object input) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        this.serialize(input, bos);
        return bos.toByteArray();
    }

    default String serializeAsBase64(Object input) {
        byte[] b = serialize(input);
        return Base64.getEncoder().encodeToString(b);
    }
}

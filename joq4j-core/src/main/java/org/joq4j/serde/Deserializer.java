package org.joq4j.serde;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface Deserializer {

    Object deserialize(InputStream in);

    default Object deserialize(byte[] serialized) {
        ByteArrayInputStream bis = new ByteArrayInputStream(serialized);
        return deserialize(bis);
    }

    default Object deserializeFromBase64(String b64Str) {
        byte[] raw = Base64.getDecoder().decode(b64Str);
        return deserialize(raw);
    }
}

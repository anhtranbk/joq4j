package org.joq4j.serde;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

public interface JavaSerialization {

    <T> void serialize(T input, Class<T> tClass, OutputStream out);

    default <T> byte[] serialize(T input, Class<T> tClass) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        this.serialize(input, tClass, bos);
        return bos.toByteArray();
    }

    default <T> String serializeAsBase64(T input, Class<T> tClass) {
        byte[] b = serialize(input, tClass);
        return Base64.getEncoder().encodeToString(b);
    }

    <T> T deserialize(InputStream in, Class<T> tClass);

    default <T> T deserialize(byte[] serialized, Class<T> tClass) {
        ByteArrayInputStream bis = new ByteArrayInputStream(serialized);
        return deserialize(bis, tClass);
    }

    default <T> T deserializeFromBase64(String b64Str, Class<T> tClass) {
        byte[] raw = Base64.getDecoder().decode(b64Str);
        return deserialize(raw, tClass);
    }
}

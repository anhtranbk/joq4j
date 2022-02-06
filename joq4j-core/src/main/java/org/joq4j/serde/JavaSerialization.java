package org.joq4j.serde;

import org.joq4j.common.utils.Base64s;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public interface JavaSerialization {

    <T> void serialize(T input, Class<T> tClass, OutputStream out);

    default <T> byte[] serialize(T input, Class<T> tClass) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        this.serialize(input, tClass, bos);
        return bos.toByteArray();
    }

    default <T> String serializeAsBase64(T input, Class<T> tClass) {
        byte[] b = serialize(input, tClass);
        return Base64s.encodeAsString(b, false);
    }

    <T> T deserialize(InputStream in, Class<T> tClass);

    default <T> T deserialize(byte[] serialized, Class<T> tClass) {
        ByteArrayInputStream bis = new ByteArrayInputStream(serialized);
        return deserialize(bis, tClass);
    }

    default <T> T deserializeFromBase64(String b64Str, Class<T> tClass) {
        byte[] raw = Base64s.decode(b64Str, false);
        return deserialize(raw, tClass);
    }
}

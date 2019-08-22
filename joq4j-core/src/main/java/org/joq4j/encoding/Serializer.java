package org.joq4j.encoding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

public interface Serializer {

    <T> void write(T input, Class<T> tClass, OutputStream out);

    default <T> byte[] write(T input, Class<T> tClass) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        this.write(input, tClass, bos);
        return bos.toByteArray();
    }

    default <T> String writeAsBase64(T input, Class<T> tClass) {
        byte[] b = write(input, tClass);
        return Base64.getEncoder().encodeToString(b);
    }

    <T> T read(InputStream in, Class<T> tClass);

    default <T> T read(byte[] serialized, Class<T> tClass) {
        ByteArrayInputStream bis = new ByteArrayInputStream(serialized);
        return read(bis, tClass);
    }

    default <T> T readFromBase64(String b64Str, Class<T> tClass) {
        byte[] raw = Base64.getDecoder().decode(b64Str);
        return read(raw, tClass);
    }
}

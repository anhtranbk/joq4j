package org.joq4j.serde;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public interface MessageEncoder {

    <T> void write(T input, OutputStream out);

    default <T> byte[] write(T input) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        this.write(input, bos);
        return bos.toByteArray();
    }

    default <T> String writeAsString(T input) {
        byte[] b = write(input);
        return new String(b);
    }

    <T> T read(InputStream in, Class<T> tClass);

    default <T> T read(byte[] serialized, Class<T> tClass) {
        ByteArrayInputStream bis = new ByteArrayInputStream(serialized);
        return read(bis, tClass);
    }

    default <T> T read(String str, Class<T> tClass) {
        return read(str.getBytes(StandardCharsets.US_ASCII), tClass);
    }
}

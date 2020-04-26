package org.joq4j.encoding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

public interface MessageEncoder {

    <T> void write(T input, OutputStream out);

    default <T> byte[] write(T input) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        this.write(input, bos);
        return bos.toByteArray();
    }

    default <T> String writeAsBase64(T input) {
        byte[] b = write(input);
        return Base64.getEncoder().encodeToString(b);
    }

    <T> T read(InputStream in);

    default <T> T read(byte[] serialized) {
        ByteArrayInputStream bis = new ByteArrayInputStream(serialized);
        return read(bis);
    }

    default <T> T readFromBase64(String b64Str) {
        byte[] raw = Base64.getDecoder().decode(b64Str);
        return read(raw);
    }
}

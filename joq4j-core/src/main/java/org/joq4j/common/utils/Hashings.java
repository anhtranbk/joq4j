package org.joq4j.common.utils;

import com.google.common.hash.Hashing;

import java.nio.charset.Charset;
import java.util.Base64;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class Hashings {

    public static byte[] sha1(String input) {
        return Hashing.sha1().hashString(input, Charset.forName("utf-8")).asBytes();
    }

    public static byte[] sha1(byte[] input) {
        return Hashing.sha1().hashBytes(input).asBytes();
    }

    public static String sha1AsHex(String input) {
        return Hashing.sha1().hashString(input, Charset.forName("utf-8")).toString();
    }

    public static String sha1AsHex(byte[] input) {
        return Hashing.sha1().hashBytes(input).toString();
    }

    public static String sha1AsBase64(String input) {
        return Base64.getEncoder().encodeToString(sha1(input));
    }
}

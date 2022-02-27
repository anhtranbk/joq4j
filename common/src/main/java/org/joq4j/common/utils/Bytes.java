package org.joq4j.common.utils;

import java.util.Arrays;

public class Bytes {

    public static final long BASE = 1024L;
    public static final long KB = BASE;
    public static final long MB = KB * BASE;
    public static final long GB = MB * BASE;

    public static byte[] concat(byte[] bytes1, byte[] bytes2) {
        byte[] result = new byte[bytes1.length + bytes2.length];
        System.arraycopy(bytes1, 0, result, 0, bytes1.length);
        System.arraycopy(bytes2, 0, result, bytes1.length, bytes2.length);
        return result;
    }

    public static boolean prefixWith(byte[] bytes, byte[] prefix) {
        if (bytes.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (bytes[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean contains(byte[] bytes, byte value) {
        for (byte b : bytes) {
            if (b == value) {
                return true;
            }
        }
        return false;
    }

    public static int indexOf(byte[] bytes, byte value) {
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public static boolean equals(byte[] bytes1, byte[] bytes2) {
        return Arrays.equals(bytes1, bytes2);
    }

    public static String toHex(byte b) {
        return toHex(new byte[]{b});
    }

    public static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static String bytesToHex(byte[] hashInBytes) {
        StringBuilder sb = new StringBuilder();
        for (byte hashInByte : hashInBytes) {
            sb.append(Integer.toString((hashInByte & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();

    }

    private static String bytesToHex2(byte[] hashInBytes) {
        StringBuilder sb = new StringBuilder();
        for (byte hashInByte : hashInBytes) {
            String hex = Integer.toHexString(0xff & hashInByte);
            if (hex.length() == 1) sb.append('0');
            sb.append(hex);
        }
        return sb.toString();

    }

    public static byte[] fromHex(String hex) {
        throw new UnsupportedOperationException();
    }

    public static String toString(byte[] bytes) {
        return Strings.fromBytes(bytes);
    }
}

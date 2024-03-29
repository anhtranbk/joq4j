package org.joq4j.common.utils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class Strings {

    public static String format(String format, Object... params) {
        return String.format(Locale.US, format, params);
    }

    public static String formatV2(String format, Object... params) {
        Map<String, Object> mapParams = Maps.initFromKeyValues(params);
        return formatV2(format, mapParams);
    }

    public static String formatV2(String template, Map<String, Object> parameters) {
        char[] chars = template.toCharArray();
        StringBuilder finalBuilder = new StringBuilder();
        StringBuilder keyBuilder = new StringBuilder();

        boolean isKey = false;
        for (char aChar : chars) {
            if (aChar == '{') {
                if (isKey) {
                    finalBuilder.append('{');
                    finalBuilder.append(keyBuilder);
                }
                keyBuilder.setLength(0);
                isKey = true;
            } else if (aChar == '}') {
                String key = keyBuilder.toString();
                if (!parameters.containsKey(key)) {
                    throw new IllegalArgumentException("Missing key: " + key);
                }
                finalBuilder.append(parameters.get(key));
                isKey = false;
                keyBuilder.setLength(0);
            } else {
                if (isKey) {
                    keyBuilder.append(aChar);
                } else {
                    finalBuilder.append(aChar);
                }
            }
        }

        if (isKey) {
            finalBuilder.append('{');
            finalBuilder.append(keyBuilder);
        }

        return finalBuilder.toString();
    }

    public static String join(String separator, String... strs) {
        return join(Arrays.asList(strs), separator);
    }

    public static <T> String join(T[] strs, String separator) {
        return join(Arrays.asList(strs), separator);
    }

    public static String join(Iterable<?> iterable, String str) {
        return join(iterable.iterator(), str, "", "");
    }

    public static String join(Iterator<?> iterator, String separator) {
        return join(iterator, separator, "", "");
    }

    public static String join(Iterable<?> iterable, String separator, String prefix, String suffix) {
        return join(iterable.iterator(), separator, prefix, suffix);
    }

    public static String join(Iterator<?> iterator, String separator, String prefix, String suffix) {
        if (!iterator.hasNext()) return "";

        StringBuilder sb = new StringBuilder(prefix);
        while (iterator.hasNext()) {
            sb.append(iterator.next());
            if (iterator.hasNext()) {
                sb.append(separator);
            }
        }
        sb.append(suffix);
        return sb.toString();
    }

    public static boolean isNonEmpty(CharSequence source) {
        return source != null && source.length() > 0;
    }

    public static boolean isNullOrEmpty(CharSequence source) {
        return source == null || source.length() == 0;
    }

    public static boolean isNullOrStringEmpty(Object source) {
        return source == null || source.toString().isEmpty();
    }

    public static boolean isNullOrWhitespace(String source) {
        return source == null || source.trim().isEmpty();
    }

    public static boolean containsOnce(String source, String... strings) {
        for (String s : strings) {
            if (source.contains(s)) return true;
        }
        return false;
    }

    public static boolean containsAll(String source, String... strings) {
        for (String s : strings) {
            if (!source.contains(s)) return false;
        }
        return true;
    }

    public static String firstCharacters(String source, int numChars, boolean withThreeDot, int skip) {
        if (skip + numChars < source.length()) {
            return source.substring(skip, skip + numChars) + (withThreeDot ? "..." : "");
        } else {
            return source.substring(skip);
        }
    }
    public static String firstCharacters(String source, int numChars) {
        return firstCharacters(source, numChars, true, 0);
    }
    public static String firstCharacters(String source, int numChars, int skip) {
        return firstCharacters(source, numChars, true, skip);
    }

    public static String remove4bytesUnicodeSymbols(String source) {
        return source.replaceAll("[^\\u0000-\\uFFFF]", "");
    }

    public static String lastCharacters(String source, int numChars) {
        try {
            return source.substring(source.length() - numChars);
        } catch (IndexOutOfBoundsException e) {
            return source;
        }
    }

    public static String simplify(String source) {
        return source.toLowerCase().replaceAll("[.,:;?!\n\t]", "");
    }

    public static byte[] toBytes(String value) {
        return toBytes(value, false);
    }

    public static byte[] toBytes(String value, boolean ascii) {
        return value.getBytes(ascii ? StandardCharsets.US_ASCII : StandardCharsets.UTF_8);
    }

    public static String fromBytes(byte[] bytes) {
        return fromBytes(bytes, false);
    }

    public static String fromBytes(byte[] bytes, boolean ascii) {
        return new String(bytes, ascii ? StandardCharsets.US_ASCII : StandardCharsets.UTF_8);
    }
}

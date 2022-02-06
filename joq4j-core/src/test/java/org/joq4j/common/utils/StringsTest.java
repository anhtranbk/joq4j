package org.joq4j.common.utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringsTest {

    static class Person {
        String name;

        Person(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    static Person[] persons;
    static List<Person> personAsList;

    @BeforeAll
    static void setupAll() {
        persons = new Person[]{
                new Person("one"),
                new Person("two"),
                new Person("three")
        };
        personAsList = Arrays.asList(persons);
    }

    @Test
    void format() {
        assertEquals("Hi Andy, welcome to New World!",
                Strings.format("Hi %s, welcome to %s!", "Andy", "New World"));
    }

    @Test
    void formatV2() {
        Map<String, Object> map = new HashMap<String, Object>() {{
            put("me", "Luan");
            put("company", "New World");
        }};
        assertEquals(
                "Hi Luan, welcome to New World!",
                Strings.formatV2("Hi {me}, welcome to {company}!", map)
        );
        assertEquals(
                "Hi {me, {{ Luan, welcome to New World!",
                Strings.formatV2("Hi {me, {{ {me}, welcome to {company}!", map)
        );
        assertEquals(
                "Hi {me, {welcome to New World!",
                Strings.formatV2("Hi {me, {welcome to {company}!", map)
        );
        assertEquals(
                "Hi {me, welcome to {company!",
                Strings.formatV2("Hi {me, welcome to {company!", map
                ));
        assertEquals(
                "Hi Luan, welcome to {company!",
                Strings.formatV2("Hi {me}, welcome to {company!", map)
        );
        assertEquals(
                "Hi {{me, {welcome {to {company!",
                Strings.formatV2("Hi {{me, {welcome {to {company!", map)
        );
        assertEquals(
                "Hi Luan, welcome to New World!",
                Strings.formatV2("Hi {me}, welcome to {company}!", map)
        );
    }

    @Test
    void formatV2_arrayParams() {
        assertEquals(
                "Hi Luan, welcome to New World!",
                Strings.formatV2("Hi {me}, welcome to {company}!",
                        "me", "Luan", "company", "New World")
        );
        assertEquals(
                "Hi {me, {{ Luan, welcome to New World!",
                Strings.formatV2("Hi {me, {{ {me}, welcome to {company}!",
                        "me", "Luan", "company", "New World")
        );
        assertEquals(
                "Hi {me, {welcome to New World!",
                Strings.formatV2("Hi {me, {welcome to {company}!",
                        "me", "Luan", "company", "New World")
        );
        assertEquals(
                "Hi {me, welcome to {company!",
                Strings.formatV2("Hi {me, welcome to {company!",
                        "me", "Luan", "company", "New World")
        );
    }

    @Test
    void join_stringArray() {
        assertEquals(
                "one_two_three",
                Strings.join("_", "one", "two", "three")
        );
        assertEquals("", Strings.join("_"));
    }

    @Test
    void join_objectArray() {
        assertEquals(
                "one,two,three",
                Strings.join(persons, ",")
        );
        assertEquals("", Strings.join(new Person[]{}, "_"));
    }

    @Test
    void join_iterable() {
        assertEquals(
                "one-two-three",
                Strings.join(personAsList, "-")
        );
    }

    @Test
    void join_iterator() {
        assertEquals(
                "one+two+three",
                Strings.join(personAsList.iterator(), "+")
        );
    }

    @Test
    void join_iterableWithPrefixSuffix() {
        assertEquals(
                "<one two three>",
                Strings.join(personAsList, " ", "<", ">")
        );
    }

    @Test
    void join_iteratorWithPrefixSuffix() {
        assertEquals(
                "[one two three]",
                Strings.join(personAsList.iterator(), " ", "[", "]")
        );
    }

    @Test
    void isNonEmpty() {
        assertTrue(Strings.isNonEmpty("abc"));
        assertTrue(Strings.isNonEmpty("  "));
        assertTrue(Strings.isNonEmpty("  aaa "));

        assertFalse(Strings.isNonEmpty(null));
        assertFalse(Strings.isNonEmpty(""));
    }

    @Test
    void isNullOrEmpty() {
        assertTrue(Strings.isNullOrEmpty(null));
        assertTrue(Strings.isNullOrEmpty(""));

        assertFalse(Strings.isNullOrEmpty("  "));
        assertFalse(Strings.isNullOrEmpty("  abc"));
        assertFalse(Strings.isNullOrEmpty("def "));
    }

    @Test
    void isNullOrStringEmpty() {
        assertTrue(Strings.isNullOrStringEmpty(null));
        assertTrue(Strings.isNullOrStringEmpty(""));

        assertFalse(Strings.isNullOrStringEmpty("  "));
        assertFalse(Strings.isNullOrStringEmpty(" a"));
        assertFalse(Strings.isNullOrStringEmpty("a "));
    }

    @Test
    void isNullOrWhitespace() {
        assertTrue(Strings.isNullOrWhitespace(null));
        assertTrue(Strings.isNullOrWhitespace(""));
        assertTrue(Strings.isNullOrWhitespace("   "));

        assertFalse(Strings.isNullOrWhitespace("  a"));
        assertFalse(Strings.isNullOrWhitespace("a  "));
        assertFalse(Strings.isNullOrWhitespace("  a  "));
    }

    @Test
    void containsOnce() {
        assertTrue(Strings.containsOnce(
                "JUnit 5 tries to take full advantage of the new " +
                        "features from Java 8, especially lambda expressions.",
                "tries", "took", "advantages"));

        assertTrue(Strings.containsOnce(
                "JUnit 5 tries to take full advantage of the new " +
                        "features from Java 8, especially lambda expressions.",
                "try", "took", "lambda"));

        assertFalse(Strings.containsOnce(
                "JUnit 5 tries to take full advantage of the new " +
                        "features from Java 8, especially lambda expressions.",
                "try", "took", "advantages"));
    }

    @Test
    void containsAll() {
        assertFalse(Strings.containsAll(
                "JUnit 5 tries to take full advantage of the new " +
                        "features from Java 8, especially lambda expressions.",
                "tries", "take", "advantages"));

        assertFalse(Strings.containsAll(
                "JUnit 5 tries to take full advantage of the new " +
                        "features from Java 8, especially lambda expressions.",
                "try", "take", "advantage"));

        assertTrue(Strings.containsAll(
                "JUnit 5 tries to take full advantage of the new " +
                        "features from Java 8, especially lambda expressions.",
                "tries", "take", "advantage"));
    }

    @Test
    void firstCharacters() {
        assertEquals(
                "JUnit 5 tries...",
                Strings.firstCharacters(
                        "JUnit 5 tries to take full advantage of the new",
                        13
                )
        );
        assertEquals(
                "Unit 5 tries...",
                Strings.firstCharacters(
                        "JUnit 5 tries to take full advantage of the new.",
                        12, true, 1
                )
        );
        assertEquals(
                "JUnit 5 tries to take...",
                Strings.firstCharacters(
                        "JUnit 5 tries to take full advantage of the new",
                        21
                )
        );
        assertEquals(
                "JUnit 5 tries to take",
                Strings.firstCharacters(
                        "JUnit 5 tries to take full advantage of the new",
                        21, false, 0
                )
        );
        assertEquals(
                "5 tries to take full ",
                Strings.firstCharacters(
                        "JUnit 5 tries to take full advantage of the new",
                        21, false, 6
                )
        );
        assertEquals(
                "5 tries to take full ...",
                Strings.firstCharacters(
                        "JUnit 5 tries to take full advantage of the new.",
                        21, 6
                )
        );
        // source length lower than num characters
        assertEquals(
                "JUnit 5 tries to take full advantage.",
                Strings.firstCharacters(
                        "JUnit 5 tries to take full advantage.",
                        100
                )
        );
        assertEquals(
                "5 tries to take full advantage.",
                Strings.firstCharacters(
                        "JUnit 5 tries to take full advantage.",
                        100, 6
                )
        );
    }

    @Test
    void remove4bytesUnicodeSymbols() {
    }

    @Test
    void lastCharacters() {
        assertEquals(
                "advantage.",
                Strings.lastCharacters(
                        "JUnit 5 tries to take full advantage.",
                        10
                )
        );
        assertEquals(
                "full advantage.",
                Strings.lastCharacters(
                        "JUnit 5 tries to take full advantage.",
                        15
                )
        );
        assertEquals(
                "JUnit 5 tries to take full advantage.",
                Strings.firstCharacters(
                        "JUnit 5 tries to take full advantage.",
                        100
                )
        );
    }

    @Test
    void simplify() {
        assertEquals(
                "joe hello anh how are you me i'm good",
                Strings.simplify(
                        "Joe: Hello Anh! How are you ?." +
                        "\nMe: I'm good;"
                )
        );
    }
}
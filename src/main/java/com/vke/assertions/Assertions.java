package com.vke.assertions;

import com.vke.formatting.Formatter;
import com.vke.formatting.Formatters;
import com.vke.utils.Executable;

import java.util.ArrayList;

public class Assertions {

    private static final Formatter DEFAULT = (a, b, c) -> a;

    /** BOOLEANS **/

    public static void assertTrue(boolean condition) {
        assertTrue(condition, "Expected condition to be true");
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            fail(message, null, null, DEFAULT);
        }
    }

    public static void assertFalse(boolean condition) {
        assertFalse(condition, "Expected condition to be false");
    }

    public static void assertFalse(boolean condition, String message) {
        if (condition) {
            fail(message, null, null, DEFAULT);
        }
    }

    /** NULL CHECKS **/

    public static void assertNull(Object value) {
        assertNull(value, "Expected null but was <" + value + ">");
    }

    public static void assertNull(Object value, String message) {
        if (value != null) {
            fail(message, null, null, DEFAULT);
        }
    }

    public static void assertNotNull(Object value) {
        assertNotNull(value, "Expected non-null value");
    }

    public static void assertNotNull(Object value, String message) {
        if (value == null) {
           fail(message, null, null, DEFAULT);
        }
    }

    /** EQUALITY **/

    public static void assertEquals(Object expected, Object actual) {
        assertEquals(expected, actual, "Expected <" + expected + "> but was <" + actual + ">");
    }

    public static void assertEquals(Object expected, Object actual, String message) {
        if (expected == actual) return;
        if (expected != null && expected.equals(actual)) return;

        fail(
                message, expected, actual, Formatters.getForTypesEqual(expected.getClass(), actual.getClass())
        );
    }

    public static void assertNotEquals(Object expected, Object actual) {
        assertNotEquals(expected, actual, "Values should not be equal: <" + expected + ">");
    }

    public static void assertNotEquals(Object expected, Object actual, String message) {
        if (expected == actual) {
            fail(message, expected, actual, Formatters.getForTypesNotEqual(expected.getClass(), actual.getClass()));
        }

        if (expected != null && expected.equals(actual)) {
            fail(message, expected, actual, Formatters.getForTypesNotEqual(expected.getClass(), actual.getClass()));
        }
    }

    /** EXCEPTIONS **/

    public static void assertThrows(Class<? extends Throwable> expected, Executable block) {
        try {
            block.execute();
        } catch (Throwable t) {
            if (expected.isInstance(t)) return;
            fail(
                    "Expected " + expected.getName() +
                            " but got " + t.getClass().getName(), null, null, DEFAULT
            );
        }

        fail(
                "Expected " + expected.getName() + " but nothing was thrown", null, null, DEFAULT
        );
    }

    public static void assertDoesNotThrow(Executable block) {
        try {
            block.execute();
        } catch (Throwable e) {
            fail("Expected no error but got " + e.getClass().getName(), null, null, DEFAULT);
        }
    }

    public static void assertAll(Executable... blocks) {
        ArrayList<Throwable> exceptions = new ArrayList<>();

        for (Executable block : blocks) {
            try {
                block.execute();
            } catch (Throwable e) {
                exceptions.add(e);
            }
        }

        throw new MultipleAssertionsFailedException(exceptions);
    }

    /** UTILS **/
    private static void fail(String message, Object expected, Object actual, Formatter formatter) {
        throw new AssertionFailedException(message, expected, actual, formatter);
    }

    private Assertions() {}

}

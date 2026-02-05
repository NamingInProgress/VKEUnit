package com.vke.assertions;

import com.vke.utils.Executable;
import com.vke.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Assertions {

    /** BOOLEANS **/

    public static void assertTrue(boolean condition) {
        assertTrue(condition, "Expected condition to be true");
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            fail(message);
        }
    }

    public static void assertFalse(boolean condition) {
        assertFalse(condition, "Expected condition to be false");
    }

    public static void assertFalse(boolean condition, String message) {
        if (condition) {
            fail(message);
        }
    }

    /** NULL CHECKS **/

    public static void assertNull(Object value) {
        assertNull(value, "Expected null but was <" + value + ">");
    }

    public static void assertNull(Object value, String message) {
        if (value != null) {
            fail(message);
        }
    }

    public static void assertNotNull(Object value) {
        assertNotNull(value, "Expected non-null value");
    }

    public static void assertNotNull(Object value, String message) {
        if (value == null) {
           fail(message);
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
                message
        );
    }

    public static void assertNotEquals(Object expected, Object actual) {
        assertNotEquals(expected, actual, "Values should not be equal: <" + expected + ">");
    }

    public static void assertNotEquals(Object expected, Object actual, String message) {
        if (expected == actual) {
            fail(message);
        }

        if (expected != null && expected.equals(actual)) {
            fail(message);
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
                            " but got " + t.getClass().getName()
            );
        }

        fail(
                "Expected " + expected.getName() + " but nothing was thrown"
        );
    }

    public static void assertDoesNotThrow(Executable block) {
        try {
            block.execute();
        } catch (Throwable e) {
            fail("Expected no error but got " + e.getClass().getName());
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
    private static void fail(String message) {
        throw new AssertionFailedException(message);
    }

    private Assertions() {}

}

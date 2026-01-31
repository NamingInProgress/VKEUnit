package com.vke.assertions;

import com.vke.utils.Executable;

public class Assertions {

    /** BOOLEANS **/

    public static void assertTrue(boolean condition) {
        if (!condition) {
            fail("Expected condition to be true");
        }
    }

    public static void assertFalse(boolean condition) {
        if (condition) {
            fail("Expected condition to be false");
        }
    }

    /** NULL CHECKS **/

    public static void assertNull(Object value) {
        if (value != null) {
            fail("Expected null but was <" + value + ">");
        }
    }

    public static void assertNotNull(Object value) {
        if (value == null) {
           fail("Expected non-null value");
        }
    }

    /** EQUALITY **/

    public static void assertEquals(Object expected, Object actual) {
        if (expected == actual) return;
        if (expected != null && expected.equals(actual)) return;

        fail(
                "Expected <" + expected + "> but was <" + actual + ">"
        );
    }

    public static void assertNotEquals(Object expected, Object actual) {
        if (expected == actual) {
            fail("Values should not be equal: <" + expected + ">");
        }

        if (expected != null && expected.equals(actual)) {
            fail("Values should not be equal: <" + expected + ">");
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

    /** UTILS **/
    private static void fail(String message) {
        throw new AssertionFailedException(message);
    }

    private Assertions() {}

}

package com.vke;

import com.vke.annotations.Disabled;
import com.vke.annotations.Repeat;
import com.vke.annotations.expected.ExpectedFail;
import com.vke.annotations.lifecycle.AfterAll;
import com.vke.annotations.lifecycle.AfterEach;
import com.vke.annotations.lifecycle.BeforeAll;
import com.vke.annotations.lifecycle.BeforeEach;
import com.vke.annotations.organization.DisplayName;
import com.vke.annotations.Test;
import com.vke.annotations.organization.Tag;

import static com.vke.assertions.Assertions.assertEquals;
import static com.vke.assertions.Assertions.*;

public class TestTest {

    @BeforeAll
    public static void beforeAll() {
        // Do setup
    }

    @AfterAll
    public static void afterAll() {
        // Clean up
    }

    @BeforeEach
    public void doSomething() {

    }

    @AfterEach
    public void doSomethingAfter() {

    }

    @Test
    public void someMethod() {
        assertEquals(true, true);
        assertFalse(false);
    }

    @Test
    @ExpectedFail(reason = "Bug #123123")
    public void zdoStuff() {
        assertEquals(4, 2 + 2);
        assertThrows(IllegalArgumentException.class, () -> Integer.parseInt("idk"));

        assertAll(
                () -> assertEquals(true, false),
                () -> assertFalse(true),
                () -> assertTrue(false)
        );
    }

    @Test
    @DisplayName("Math test")
    @Tag("slow")
    public void aa() {
        assertEquals(4, 2 * 2);
    }

    @Test
    @Disabled
    public void idkItsBrokenOrSth() {
        assertEquals(true, false);
    }

    @Test
    @Repeat(2)
    public void ab() {
        assertDoesNotThrow((() -> Integer.parseInt("1")));
    }

}

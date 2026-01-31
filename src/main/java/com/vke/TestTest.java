package com.vke;

import com.vke.annotations.Disabled;
import com.vke.annotations.organization.DisplayName;
import com.vke.annotations.Test;
import com.vke.annotations.organization.Tag;

import static com.vke.assertions.Assertions.assertEquals;
import static com.vke.assertions.Assertions.*;

public class TestTest {

    @Test
    public void someMethod() {
        assertEquals(true, true);
        assertFalse(false);
    }

    @Test
    public void zdoStuff() {
        assertEquals(4, 2 + 2);
        assertThrows(IllegalArgumentException.class, () -> Integer.parseInt("idk"));
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
    public void ab() {
        assertDoesNotThrow((() -> Integer.parseInt("asd")));
    }

}

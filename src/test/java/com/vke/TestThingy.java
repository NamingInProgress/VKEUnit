package com.vke;

import com.vke.annotations.Disabled;
import com.vke.annotations.Repeat;
import com.vke.annotations.Test;

import static com.vke.assertions.Assertions.assertDoesNotThrow;

public class TestThingy {

    @Test
    public void test() {}

    @Test
    @Repeat(2)
    public void ab() {
        assertDoesNotThrow((() -> Integer.parseInt("asd")));
    }

}

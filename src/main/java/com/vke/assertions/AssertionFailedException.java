package com.vke.assertions;

import com.vke.formatting.Formatter;

public class AssertionFailedException extends RuntimeException {

    public AssertionFailedException(String message, Object expected, Object actual, Formatter formatter) {
        super(formatter.format(message, expected, actual));
    }

}

package com.vke.assertions;

import java.util.List;

public class MultipleAssertionsFailedException extends RuntimeException {

    private final List<Throwable> throwables;

    public MultipleAssertionsFailedException(List<Throwable> throwables) {
        super("Multiple assertions failed!");
        this.throwables = throwables;
    }

    public List<Throwable> getThrowables() { return this.throwables; }

}

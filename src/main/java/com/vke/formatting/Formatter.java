package com.vke.formatting;

@FunctionalInterface
public interface Formatter {

    String format(String message, Object expected, Object actual);

}

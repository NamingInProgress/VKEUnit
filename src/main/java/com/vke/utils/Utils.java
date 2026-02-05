package com.vke.utils;

public class Utils {

    public static boolean isReflectStackTraceElement(StackTraceElement el) {
        return el.getClassName().startsWith("java.lang.reflect.")
                || el.getClassName().startsWith("jdk.internal.reflect.");
    }

}

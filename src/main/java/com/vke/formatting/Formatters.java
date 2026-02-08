package com.vke.formatting;

import com.vke.utils.Colors;
import com.vke.utils.Pair;

import java.util.HashMap;
import java.util.Map;

public class Formatters {

    public static final HashMap<Pair<Class<?>, Class<?>>, Formatter> EQUALS = new HashMap<>();
    public static final HashMap<Pair<Class<?>, Class<?>>, Formatter> NOT_EQUALS = new HashMap<>();

    private static final Formatter STRING = (msg, exp, act) -> {
        String expected = exp.toString();
        String actual = act.toString();

        Colors c = new Colors(msg);
        c.red("\n\t\t\tExpected: ").write(expected);
        c.red("\n\t\t\tActual:   ").write(actual);
        c.red("\n\t\t\t          ");

        if (expected.length() < actual.length()) {
            for (int i = 0; i < expected.length(); i++) {
                c.write(" ");
            }
            c.write("^");
            return c.toString();
        }

        for (int i = 0; i < expected.length(); i++) {
            if (i == actual.length()) {
                c.write("^");
                break;
            }
            if (expected.charAt(i) == actual.charAt(i)) {
                c.write(" ");
            } else {
                c.write("^");
                break;
            }
        }

        return c.toString();
    };

    private static final Formatter DEFAULT = (msg, exp, act) -> msg;

    static {
        registerForTypesEqual(String.class, String.class, STRING);
        registerForTypesEqual(String.class, Number.class, STRING);
        registerForTypesEqual(Number.class, Number.class, STRING);
        registerForTypesEqual(Number.class, String.class, STRING);
    }

    public static void registerForTypesEqual(Class<?> a, Class<?> b, Formatter formatter) {
        EQUALS.put(new Pair<>(a, b), formatter);
    }

    @SuppressWarnings("all")
    public static Formatter getForTypesEqual(Class<?> a, Class<?> b) {
        Formatter f = findClosestFormatter(a, b, EQUALS);
        return f == null ? DEFAULT : f;
    }

    public static void registerForTypesNotEqual(Class<?> a, Class<?> b, Formatter formatter) {
        NOT_EQUALS.put(new Pair<>(a, b), formatter);
    }

    @SuppressWarnings("all")
    public static Formatter getForTypesNotEqual(Class<?> a, Class<?> b) {
        Formatter f = findClosestFormatter(a, b, NOT_EQUALS);
        return f == null ? DEFAULT : f;
    }

    static int distance(Class<?> from, Class<?> to) {
        if (!to.isAssignableFrom(from)) return Integer.MAX_VALUE;
        if (from.equals(to)) return 0;

        int min = Integer.MAX_VALUE;

        Class<?> superclass = from.getSuperclass();
        if (superclass != null) {
            min = Math.min(min, 1 + distance(superclass, to));
        }

        for (Class<?> iface : from.getInterfaces()) {
            min = Math.min(min, 1 + distance(iface, to));
        }

        return min;
    }

    static Formatter findClosestFormatter(
            Class<?> a,
            Class<?> b,
            Map<Pair<Class<?>, Class<?>>, Formatter> formatters
    ) {
        Formatter best = null;
        int bestScore = Integer.MAX_VALUE;

        for (Map.Entry<Pair<Class<?>, Class<?>>, Formatter> entry : formatters.entrySet()) {
            Class<?> ra = entry.getKey().v1;
            Class<?> rb = entry.getKey().v2;

            if (!ra.isAssignableFrom(a) || !rb.isAssignableFrom(b)) {
                continue;
            }

            int score =
                    distance(a, ra) +
                            distance(b, rb);

            if (score < bestScore) {
                bestScore = score;
                best = entry.getValue();
            }
        }

        return best;
    }

}

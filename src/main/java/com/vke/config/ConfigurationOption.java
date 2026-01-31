package com.vke.config;

public class ConfigurationOption<T> {

    private final String property;

    private volatile T state;

    public ConfigurationOption(String property, Initializer<? extends T> init) {
        this.property = property;
        this.state = init.apply(property);
    }

    public String getProperty() {
        return property;
    }

    public void set(T value) {
        this.state = value;
    }

    public T get() {
        return state;
    }

    public T get(T defaultValue) {
        T state = this.state;
        if (state == null) {
            state = defaultValue;
        }

        return state;
    }

    @FunctionalInterface
    public interface Initializer<U> {

        Initializer<String> STRING = System::getProperty;
        Initializer<Integer> INT = Integer::getInteger;
        Initializer<Boolean> BOOLEAN = Boolean::getBoolean;

        U apply(String property);
    }

}

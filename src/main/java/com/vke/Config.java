package com.vke;

import com.vke.config.ConfigurationOption;

import static com.vke.config.ConfigurationOption.Initializer;

public class Config {

    public static final ConfigurationOption<String> DISABLED_TAGS;
    public static final ConfigurationOption<Boolean> LARGE_STACK_TRACE;

    static {
        DISABLED_TAGS = new ConfigurationOption<>("vke.test.disabledTags", Initializer.STRING);
        LARGE_STACK_TRACE = new ConfigurationOption<>("vke.test.largeStackTrace", Initializer.BOOLEAN);
    }

}

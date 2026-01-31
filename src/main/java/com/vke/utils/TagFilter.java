package com.vke.utils;

import com.vke.Config;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class TagFilter {

    private static Set<String> tags = null;

    public static Set<String> readIncludedTags() {
        if (tags != null) return tags;
        String raw = Config.DISABLED_TAGS.get();

        if (raw == null || raw.isBlank()) {
            return Set.of();
        }

        tags = Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toUnmodifiableSet());

        return tags;
    }

    private TagFilter() {}

}

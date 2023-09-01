package org.drools.compiler.kie.builder.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.kie.api.conf.Option;

// An Option map wrapper class so that modularized containers don't have to depend on concrete configuration
public class KieBaseUpdaterOptions {

    private final Map<Class<? extends Option>, Option> optionMap = new HashMap<>();

    public KieBaseUpdaterOptions(OptionEntry... options) {
        for (OptionEntry o : options) {
            optionMap.put(o.key, o.value);
        }
    }

    public KieBaseUpdaterOptions(List<OptionEntry> options) {
        this(options.toArray(new OptionEntry[0]));
    }

    public Optional<Option> getOption(Class<? extends Option> optionClazz) {
        return Optional.ofNullable(optionMap.get(optionClazz));
    }

    public static class OptionEntry {
        final Class<? extends Option> key;
        final Option value;

        public OptionEntry(Class<? extends Option> key, Option value) {
            this.key = key;
            this.value = value;
        }
    }
}

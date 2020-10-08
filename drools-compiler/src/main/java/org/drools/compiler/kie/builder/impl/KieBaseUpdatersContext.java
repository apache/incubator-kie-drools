package org.drools.compiler.kie.builder.impl;

import java.util.Optional;

import org.drools.core.reteoo.Rete;
import org.kie.api.conf.Option;

public class KieBaseUpdatersContext {

    private final KieBaseUpdaterOptions options;
    private final Rete rete;
    private final ClassLoader classLoader;

    public KieBaseUpdatersContext(KieBaseUpdaterOptions options,
                                  Rete rete,
                                  ClassLoader classLoader) {
        this.options = options;
        this.rete = rete;
        this.classLoader = classLoader;
    }

    public Optional<Option> getOption(Class<? extends Option> optionClazz) {
        return options.getOption(optionClazz);
    }

    public Rete getRete() {
        return rete;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }
}

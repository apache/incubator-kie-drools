package org.drools.compiler.kie.builder.impl;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.reteoo.Rete;

public class KieBaseUpdatersContext {

    private final RuleBaseConfiguration ruleBaseConfiguration;
    private final Rete rete;
    private final ClassLoader classLoader;

    public KieBaseUpdatersContext(RuleBaseConfiguration ruleBaseConfiguration, Rete rete, ClassLoader classLoader) {
        this.ruleBaseConfiguration = ruleBaseConfiguration;
        this.rete = rete;
        this.classLoader = classLoader;
    }

    public RuleBaseConfiguration getRuleBaseConfiguration() {
        return ruleBaseConfiguration;
    }

    public Rete getRete() {
        return rete;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }
}

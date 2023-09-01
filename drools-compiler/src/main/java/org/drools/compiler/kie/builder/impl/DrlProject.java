package org.drools.compiler.kie.builder.impl;

import java.util.function.BiFunction;

import org.kie.api.builder.KieBuilder;

public class DrlProject implements KieBuilder.ProjectType {
    public static final BiFunction<InternalKieModule, ClassLoader, KieModuleKieProject> SUPPLIER =
            (InternalKieModule kieModule, ClassLoader classLoader) -> new KieModuleKieProject(kieModule, classLoader);
}

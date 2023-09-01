package org.drools.impact.analysis.parser.internal;

import java.util.function.BiFunction;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.kie.api.builder.KieBuilder;

public class ImpactAnalysisProject implements KieBuilder.ProjectType {
    public static final BiFunction<InternalKieModule, ClassLoader, KieModuleKieProject> SUPPLIER = ImpactAnalysisKieProject.create();
}

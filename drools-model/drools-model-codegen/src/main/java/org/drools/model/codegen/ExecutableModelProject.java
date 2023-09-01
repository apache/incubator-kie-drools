package org.drools.model.codegen;

import java.util.function.BiFunction;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.drools.model.codegen.execmodel.CanonicalModelKieProject;
import org.kie.api.builder.KieBuilder;

public class ExecutableModelProject implements KieBuilder.ProjectType {
    public static final BiFunction<InternalKieModule, ClassLoader, KieModuleKieProject> SUPPLIER = CanonicalModelKieProject.create();
}

package org.drools.impact.analysis.parser.internal;

import java.util.function.BiFunction;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.BuildContext;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.kie.internal.builder.KnowledgeBuilder;

public class ImpactAnalysisKieProject extends KieModuleKieProject {

    public static BiFunction<InternalKieModule, ClassLoader, KieModuleKieProject> create() {
        return (internalKieModule, classLoader) -> new ImpactAnalysisKieProject(internalKieModule, classLoader);
    }

    protected ImpactModelBuilderImpl modelBuilder;

    public ImpactAnalysisKieProject( InternalKieModule kieModule, ClassLoader classLoader) {
        super(new ImpactAnalysisKieModule(kieModule), classLoader);
    }

    @Override
    protected KnowledgeBuilder createKnowledgeBuilder(KieBaseModelImpl kBaseModel, InternalKieModule kModule) {
        if (getInternalKieModule().getKieModuleModel() != kBaseModel.getKModule()) {
            // if the KieBase belongs to a different kmodule it is not necessary to build it
            return null;
        }
        KnowledgeBuilderConfigurationImpl builderConfiguration = getBuilderConfiguration(kBaseModel, kModule);
        modelBuilder = new ImpactModelBuilderImpl(builderConfiguration, kModule.getReleaseId());
        return modelBuilder;
    }

    @Override
    public void writeProjectOutput(MemoryFileSystem trgMfs, BuildContext buildContext) {
        ImpactAnalysisKieModule kmodule = (ImpactAnalysisKieModule) getInternalKieModule();
        kmodule.setAnalysisModel( modelBuilder.getAnalysisModel() );
    }

    @Override
    protected boolean compileIncludedKieBases() {
        return false;
    }


}
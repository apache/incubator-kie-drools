package org.drools.compiler.kie.builder.impl;

import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.internal.builder.KnowledgeBuilder;

public interface KieProject {
    
    ReleaseId getGAV();
    
    InternalKieModule getKieModuleForKBase(String kBaseName);

    Collection<String> getKieBaseNames();

    KieBaseModel getKieBaseModel(String kBaseName);

    KieBaseModel getDefaultKieBaseModel();

    KieSessionModel getKieSessionModel(String kSessionName);

    KieSessionModel getDefaultKieSession();

    KieSessionModel getDefaultStatelessKieSession();

    void init();   
    
    ClassLoader getClassLoader();

    ResultsImpl verify();
    ResultsImpl verify( String... kModelNames );
    void verify(BuildContext buildContext);

    long getCreationTimestamp();

    Set<String> getTransitiveIncludes(String kBaseName);
    Set<String> getTransitiveIncludes(KieBaseModel kBaseModel);

    InputStream getPomAsStream();

    KnowledgeBuilder buildKnowledgePackages( KieBaseModelImpl kBaseModel, BuildContext buildContext );
    KnowledgeBuilder buildKnowledgePackages( KieBaseModelImpl kBaseModel, BuildContext buildContext, Predicate<String> buildFilter );

    default void writeProjectOutput(MemoryFileSystem trgMfs, BuildContext buildContext) {}
}

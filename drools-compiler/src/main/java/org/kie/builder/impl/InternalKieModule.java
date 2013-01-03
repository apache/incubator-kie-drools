package org.kie.builder.impl;

import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.ReleaseId;
import org.kie.builder.KieModule;
import org.kie.builder.model.KieModuleModel;
import org.kie.builder.Results;
import org.kie.definition.KnowledgePackage;

import java.io.File;
import java.util.Collection;
import java.util.Map;

public interface InternalKieModule extends KieModule {

    void cacheKnowledgeBuilderForKieBase(String kieBaseName, KnowledgeBuilder kbuilder);

    KnowledgeBuilder getKnowledgeBuilderForKieBase(String kieBaseName);

    Collection<KnowledgePackage> getKnowledgePackagesForKieBase(String kieBaseName);

    void cacheResultsForKieBase(String kieBaseName, Results results);

    Map<String, Results> getKnowledgeResultsCache();    
    
    KieModuleModel getKieModuleModel();    
    
    byte[] getBytes( );    
    
    Map<ReleaseId, InternalKieModule> getDependencies();

    void addDependency(InternalKieModule dependency);

    boolean isAvailable( final String pResourceName );
    
    byte[] getBytes( final String pResourceName );
    
    Collection<String> getFileNames();  
    
    File getFile();

    Map<String, byte[]> getClassesMap();
}

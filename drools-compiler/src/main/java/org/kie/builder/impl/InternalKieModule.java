package org.kie.builder.impl;

import org.kie.builder.ReleaseId;
import org.kie.builder.KieModule;
import org.kie.builder.KieModuleModel;
import org.kie.builder.Results;
import org.kie.definition.KnowledgePackage;

import java.io.File;
import java.util.Collection;
import java.util.Map;

public interface InternalKieModule extends KieModule {
    
    Map<String, Collection<KnowledgePackage>> getKnowledgePackageCache();

    Map<String, Results> getKnowledgeResultsCache();    
    
    KieModuleModel getKieModuleModel();    
    
    byte[] getBytes( );    
    
    Map<ReleaseId, InternalKieModule> getDependencies();

    void addDependency(InternalKieModule dependency);

    boolean isAvailable( final String pResourceName );
    
    byte[] getBytes( final String pResourceName );
    
    Collection<String> getFileNames();  
    
    File getFile();
}

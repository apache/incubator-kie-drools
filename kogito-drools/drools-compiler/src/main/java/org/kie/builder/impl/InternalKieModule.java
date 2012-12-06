package org.kie.builder.impl;

import org.kie.builder.GAV;
import org.kie.builder.KieModule;
import org.kie.builder.KieModuleModel;

import java.io.File;
import java.util.Collection;
import java.util.Map;

public interface InternalKieModule extends KieModule {
    
    KieModuleModel getKieModuleModel();    
    
    byte[] getBytes( );    
    
    Map<GAV, InternalKieModule> getDependencies();

    void addDependency(InternalKieModule dependency);

    boolean isAvailable( final String pResourceName );
    
    byte[] getBytes( final String pResourceName );
    
    Collection<String> getFileNames();  
    
    File getFile();
}

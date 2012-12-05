package org.kie.builder.impl;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.kie.builder.GAV;
import org.kie.builder.KieModule;
import org.kie.builder.KieModuleModel;

public interface InternalKieModule extends KieModule {
    
    KieModuleModel getKieModuleModel();    
    
    byte[] getBytes( );    
    
    public Map<GAV, InternalKieModule> getDependencies();

    public void setDependencies(Map<GAV, InternalKieModule> dependencies);    
    
    boolean isAvailable( final String pResourceName );
    
    byte[] getBytes( final String pResourceName );
    
    Collection<String> getFileNames();  
    
    File getFile();
    
}

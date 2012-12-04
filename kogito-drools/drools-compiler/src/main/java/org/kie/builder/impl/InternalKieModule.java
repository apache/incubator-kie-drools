package org.kie.builder.impl;

import org.kie.KieBase;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieModule;
import org.kie.builder.KieModuleModel;
import org.kie.builder.KieSessionModel;
import org.kie.definition.KnowledgePackage;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.zip.ZipFile;

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

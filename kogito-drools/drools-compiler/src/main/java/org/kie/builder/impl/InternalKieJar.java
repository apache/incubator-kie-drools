package org.kie.builder.impl;

import org.kie.builder.KieBaseModel;
import org.kie.builder.KieJar;
import org.kie.builder.KieProjectModel;
import org.kie.definition.KnowledgePackage;
import org.kie.runtime.KieBase;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.zip.ZipFile;

public interface InternalKieJar extends KieJar {

//    Map<String, Collection<KnowledgePackage>> getKnowledgePackageCache();
//
//    KieBaseModel getKieBaseForSession(String kSessionName);
    
    byte[] getBytes( );
    
    KieProjectModel getKieProjectModel();
    
    void setDependencies(Collection<InternalKieJar> dependencies);
    
    Collection<InternalKieJar> getDependencies();    
    
    boolean isAvailable( final String pResourceName );
    
    byte[] getBytes( final String pResourceName );
    
    Collection<String> getFileNames();  
    
    File getFile();
}

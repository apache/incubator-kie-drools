package org.kie.builder.impl;

import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieSessionModel;
import org.kie.util.CompositeClassLoader;

public interface KieProject {
    
    GAV getGAV();
    
    InternalKieModule getKieModuleForKBase(String kBaseName);

    KieBaseModel getKieBaseModel(String kBaseName);

    KieBaseModel getDefaultKieBaseModel();

    KieSessionModel getKieSessionModel(String kSessionName);

    KieSessionModel getDefaultKieSession();

    KieSessionModel getDefaultStatelessKieSession();

    void init();   
    
    CompositeClassLoader getClassLoader();

    ResultsImpl verify();
}

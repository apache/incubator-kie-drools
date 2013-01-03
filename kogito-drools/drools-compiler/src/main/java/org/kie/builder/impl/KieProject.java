package org.kie.builder.impl;

import org.kie.builder.ReleaseId;
import org.kie.builder.model.KieBaseModel;
import org.kie.builder.model.KieSessionModel;
import org.kie.internal.utils.CompositeClassLoader;

public interface KieProject {
    
    ReleaseId getGAV();
    
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

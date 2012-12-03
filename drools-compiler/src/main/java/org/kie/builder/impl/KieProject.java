package org.kie.builder.impl;

import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieSessionModel;

public interface KieProject {
    
    GAV getGAV();
    
    InternalKieModule getKieModuleForKBase(String kBaseName);

    KieBaseModel getKieBaseModel(String kBaseName);

    KieSessionModel getKieSessionModel(String kSessionName); 
    
    void verify();
}

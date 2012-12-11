package org.kie.builder;

import org.kie.KieBase;
import org.kie.runtime.KieSession;
import org.kie.runtime.StatelessKieSession;

public interface KieContainer {

    GAV getGAV();
    
    Results verify();

    void updateToVersion(GAV version);

    KieBase getKieBase();

    KieBase getKieBase(String kBaseName);

    KieSession newKieSession();

    KieSession newKieSession(String kSessionName);

    StatelessKieSession newKieStatelessSession();
    
    StatelessKieSession newKieStatelessSession(String kSessionName);
    
    ClassLoader getClassLoader();
}

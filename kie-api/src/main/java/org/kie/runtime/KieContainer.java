package org.kie.runtime;

import org.kie.KieBase;
import org.kie.builder.ReleaseId;
import org.kie.builder.Results;

public interface KieContainer {

    ReleaseId getReleaseId();
    
    Results verify();

    void updateToVersion(ReleaseId version);

    KieBase getKieBase();

    KieBase getKieBase(String kBaseName);

    KieSession newKieSession();

    KieSession newKieSession(Environment environment);

    KieSession newKieSession(String kSessionName);

    KieSession newKieSession(String kSessionName, Environment environment);

    StatelessKieSession newStatelessKieSession();
    
    StatelessKieSession newStatelessKieSession(String kSessionName);
    
    ClassLoader getClassLoader();
}

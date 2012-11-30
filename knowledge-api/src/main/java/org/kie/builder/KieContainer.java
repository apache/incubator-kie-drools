package org.kie.builder;

import org.kie.runtime.KieBase;
import org.kie.runtime.KieSession;
import org.kie.runtime.StatelessKieSession;

public interface KieContainer {

    GAV getGAV();

    void updateToVersion(String version);

    KieBase getKieBase();

    KieBase getKieBase(String kBaseName);

    KieSession getKieSession();

    KieSession getKieSession(String kSessionName);

    StatelessKieSession getKieStatelessSession(String kSessionName);

    void dispose();
}

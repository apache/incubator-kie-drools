package org.kie.builder;

import org.kie.runtime.KieBase;
import org.kie.runtime.KieSession;
import org.kie.runtime.KieStatelessSession;

public interface KieContainer {

    String KPROJECT_JAR_PATH = "META-INF/kproject.xml";
    String KPROJECT_RELATIVE_PATH = "src/main/resources/" + KPROJECT_JAR_PATH;

    GAV getGAV();

    void updateToVersion(String version);

    KieBase getKieBase(String kBaseName);

    KieSession getKieSession(String kSessionName);

    KieStatelessSession getKieStatelessSession(String kSessionName);

    void dispose();
}

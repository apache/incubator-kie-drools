package org.kie.builder.impl;

import org.kie.builder.GAV;
import org.kie.builder.KieContainer;
import org.kie.builder.KieJar;
import org.kie.builder.KieScanner;

public interface InternalKieScanner extends KieScanner {

    void setKieContainer(KieContainer kieContainer);

    KieJar loadArtifact(GAV gav);
}

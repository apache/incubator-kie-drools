package org.kie.builder.impl;

import org.kie.builder.GAV;
import org.kie.builder.KieModule;
import org.kie.builder.KieScanner;
import org.kie.runtime.KieContainer;

public interface InternalKieScanner extends KieScanner {

    void setKieContainer(KieContainer kieContainer);

    KieModule loadArtifact(GAV gav);
}

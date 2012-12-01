package org.kie.builder.impl;

import org.kie.builder.GAV;
import org.kie.builder.KieContainer;
import org.kie.builder.KieScanner;

import java.io.File;

public interface InternalKieScanner extends KieScanner {

    void setKieContainer(KieContainer kieContainer);

    File loadArtifact(GAV gav);
}

package org.drools.compiler.kie.builder.impl;

import java.io.InputStream;

import org.kie.builder.ReleaseId;
import org.kie.builder.KieModule;
import org.kie.builder.KieScanner;
import org.kie.runtime.KieContainer;

public interface InternalKieScanner extends KieScanner {

    void setKieContainer(KieContainer kieContainer);

    KieModule loadArtifact(ReleaseId releaseId);
    
    KieModule loadArtifact(ReleaseId releaseId, InputStream pomXML);
}

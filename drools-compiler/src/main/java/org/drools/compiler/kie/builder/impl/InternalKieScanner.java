package org.drools.compiler.kie.builder.impl;

import java.io.InputStream;

import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.util.maven.support.PomModel;

public interface InternalKieScanner extends KieScanner {

    void setKieContainer(KieContainer kieContainer);

    KieModule loadArtifact(ReleaseId releaseId);
    
    KieModule loadArtifact(ReleaseId releaseId, InputStream pomXML);

    KieModule loadArtifact(ReleaseId releaseId, PomModel pomModel);

    String getArtifactVersion(ReleaseId releaseId);

    ReleaseId getScannerReleaseId();

    ReleaseId getCurrentReleaseId();

    Status getStatus();

    long getPollingInterval();
}

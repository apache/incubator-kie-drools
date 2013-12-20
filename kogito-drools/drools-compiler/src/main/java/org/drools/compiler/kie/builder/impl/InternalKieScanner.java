package org.drools.compiler.kie.builder.impl;

import java.io.InputStream;

import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieScanner;
import org.kie.api.runtime.KieContainer;

public interface InternalKieScanner extends KieScanner {

    public enum Status {
        STARTING, SCANNING, UPDATING, RUNNING, STOPPED;
    }

    void setKieContainer(KieContainer kieContainer);

    KieModule loadArtifact(ReleaseId releaseId);
    
    KieModule loadArtifact(ReleaseId releaseId, InputStream pomXML);

    String getArtifactVersion(ReleaseId releaseId);

    ReleaseId getScannerReleaseId();

    ReleaseId getCurrentReleaseId();

    Status getStatus();
}

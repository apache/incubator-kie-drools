package org.kie.builder.impl;

import org.kie.builder.GAV;
import org.kie.builder.KieContainer;
import org.kie.builder.KieJar;
import org.kie.runtime.KieBase;
import org.kie.runtime.KieSession;
import org.kie.runtime.KieStatelessSession;

public class KieContainerImpl implements KieContainer {

    private GAV gav;

    public KieContainerImpl(GAV gav) {
        this.gav = gav;
    }

    public void deploy(KieJar kieJar) {
        throw new UnsupportedOperationException("org.kie.builder.impl.KieContainerImpl.deploy -> TODO");
    }

    public GAV getGAV() {
        return gav;
    }

    public void updateToVersion(String version) {
        throw new UnsupportedOperationException("org.kie.builder.impl.KieContainerImpl.updateToVersion -> TODO");
    }

    public KieBase getKieBase(String kBaseName) {
        throw new UnsupportedOperationException("org.kie.builder.impl.KieContainerImpl.getKieBase -> TODO");
    }

    public KieSession getKieSession(String kSessionName) {
        throw new UnsupportedOperationException("org.kie.builder.impl.KieContainerImpl.getKieSession -> TODO");
    }

    public KieStatelessSession getKieStatelessSession(String kSessionName) {
        throw new UnsupportedOperationException("org.kie.builder.impl.KieContainerImpl.getKieStatelessSession -> TODO");
    }

    public void dispose() {
        throw new UnsupportedOperationException("org.kie.builder.impl.KieContainerImpl.dispose -> TODO");
    }
}

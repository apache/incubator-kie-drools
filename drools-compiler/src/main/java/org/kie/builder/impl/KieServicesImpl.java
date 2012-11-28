package org.kie.builder.impl;

import org.drools.kproject.GroupArtifactVersion;
import org.kie.builder.GAV;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieContainer;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieProject;
import org.kie.builder.KieRepository;
import org.kie.builder.KieScanner;
import org.kie.builder.KieServices;
import org.kie.io.ResourceFactory;

public class KieServicesImpl implements KieServices {

    public KieFileSystem newKieFileSystem() {
        return new KieFileSystemImpl();
    }

    public KieBuilder newKieBuilder(KieFileSystem kieFileSystem) {
        return new KieBuilderImpl(kieFileSystem);
    }

    public ResourceFactory getResourceFactory() {
        throw new UnsupportedOperationException("org.kie.builder.impl.KieServicesImpl.getResourceFactory -> TODO");
    }

    public KieProject newKieProject() {
        throw new UnsupportedOperationException("org.kie.builder.impl.KieServicesImpl.newKieProject -> TODO");
    }

    public KieRepository getKieRepository() {
        throw new UnsupportedOperationException("org.kie.builder.impl.KieServicesImpl.getKieRepository -> TODO");
    }

    public KieContainer getKieContainer(GAV gav) {
        return new KieContainerImpl(gav);
    }

    public KieScanner newKieScanner(KieContainer kieContainer) {
        throw new UnsupportedOperationException("org.kie.builder.impl.KieServicesImpl.newKieScanner -> TODO");
    }

    public GAV newGav(String groupId, String artifactId, String version) {
        return new GroupArtifactVersion(groupId, artifactId, version);
    }
}

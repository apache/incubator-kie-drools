package org.kie.builder.impl;

import org.drools.kproject.GroupArtifactVersion;
import org.kie.builder.GAV;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieContainer;
import org.kie.builder.KieFactory;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieProject;
import org.kie.builder.KieScanner;

public class KieFactoryImpl implements KieFactory {
    public KieFileSystem newKieFileSystem() {
        return new KieFileSystemImpl();
    }

    public KieBuilder newKieBuilder(KieFileSystem kieFileSystem) {
        return new KieBuilderImpl(kieFileSystem);
    }

    public KieScanner newKieScanner(KieContainer kieContainer) {
        throw new UnsupportedOperationException("org.kie.builder.impl.KieFactoryImpl.newKieScanner -> TODO");

    }

    public GAV newGav(String groupId, String artifactId, String version) {
        return new GroupArtifactVersion(groupId, artifactId, version);
    }

    public KieProject newKieProject() {
        throw new UnsupportedOperationException("org.kie.builder.impl.KieFactoryImpl.newKieProject -> TODO");
    }
}

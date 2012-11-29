package org.kie.builder.impl;

import org.drools.kproject.GroupArtifactVersion;
import org.drools.kproject.KieProjectImpl;
import org.drools.kproject.SimpleKieProjectImpl;
import org.kie.builder.GAV;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieContainer;
import org.kie.builder.KieFactory;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieProject;
import org.kie.builder.KieScanner;

public class KieFactoryImpl implements KieFactory {

    private static final String DEFAULT_VERSION = "1.0";
    private static final String DEFAULT_ARTIFACT = "default";
    private static final String DEFAULT_GROUP = "org.user";

    public KieBuilder newKieBuilder(KieFileSystem kieFileSystem) {
        return new KieBuilderImpl(kieFileSystem);
    }

    public KieScanner newKieScanner(KieContainer kieContainer) {
        throw new UnsupportedOperationException("org.kie.builder.impl.KieFactoryImpl.newKieScanner -> TODO");

    }

    public GAV newDefaultGav() {
        return new GroupArtifactVersion( DEFAULT_GROUP, DEFAULT_ARTIFACT, DEFAULT_VERSION);
    }

    public GAV newGav(String groupId, String artifactId, String version) {
        return new GroupArtifactVersion(groupId, artifactId, version);
    }

    public KieProject newKieProject() {
        return new KieProjectImpl();
    }

    public KieFileSystem newKieFileSystem() {
        return new KieFileSystemImpl();
    }
    
    public KieFileSystem newKieFileSystem(KieProject kp) {
        return new KieFileSystemImpl( kp );
    }

    public KieProject newSimpleKieProject(GAV gav) {
        return new SimpleKieProjectImpl( gav );
    }

    public KieProject newSimpleKieProject() {
        return new SimpleKieProjectImpl();
    }

}

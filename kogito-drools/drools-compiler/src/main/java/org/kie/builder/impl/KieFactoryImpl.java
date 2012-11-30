package org.kie.builder.impl;

import org.drools.kproject.GroupArtifactVersion;
import org.drools.kproject.KieProjectImpl;
import org.kie.builder.GAV;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieContainer;
import org.kie.builder.KieFactory;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieProject;
import org.kie.builder.KieScanner;
import org.kie.util.ServiceRegistryImpl;

public class KieFactoryImpl implements KieFactory {

    public GAV newGav(String groupId, String artifactId, String version) {
        return new GroupArtifactVersion(groupId, artifactId, version);
    }

    public KieProject newKieProject() {
        return new KieProjectImpl();
    }

    public KieFileSystem newKieFileSystem() {
        return new KieFileSystemImpl();
    }
        
    
}

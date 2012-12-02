package org.kie.builder.impl;

import org.drools.kproject.GAVImpl;
import org.drools.kproject.KieProjectModelImpl;
import org.kie.builder.GAV;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieContainer;
import org.kie.builder.KieFactory;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieProjectModel;
import org.kie.builder.KieScanner;
import org.kie.util.ServiceRegistryImpl;

public class KieFactoryImpl implements KieFactory {

    public GAV newGav(String groupId, String artifactId, String version) {
        return new GAVImpl(groupId, artifactId, version);
    }

    public KieProjectModel newKieProject() {
        return new KieProjectModelImpl();
    }

    public KieFileSystem newKieFileSystem() {
        return new KieFileSystemImpl();
    }
        
    
}

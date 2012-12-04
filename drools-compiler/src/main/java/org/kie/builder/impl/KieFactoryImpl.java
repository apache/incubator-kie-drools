package org.kie.builder.impl;

import org.drools.kproject.GAVImpl;
import org.drools.kproject.models.KieModuleModelImpl;
import org.kie.builder.GAV;
import org.kie.builder.KieFactory;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieModuleModel;

public class KieFactoryImpl implements KieFactory {

    public GAV newGav(String groupId, String artifactId, String version) {
        return new GAVImpl(groupId, artifactId, version);
    }

    public KieModuleModel newKieModuleModel() {
        return new KieModuleModelImpl();
    }

    public KieFileSystem newKieFileSystem() {
        return new KieFileSystemImpl();
    }
        
    
}

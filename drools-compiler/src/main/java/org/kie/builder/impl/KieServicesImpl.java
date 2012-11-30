package org.kie.builder.impl;

import java.io.File;

import org.kie.builder.GAV;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieContainer;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieRepository;
import org.kie.builder.KieScanner;
import org.kie.builder.KieServices;
import org.kie.io.ResourceFactory;
import org.kie.util.ServiceRegistryImpl;

public class KieServicesImpl implements KieServices {

    public ResourceFactory getResourceFactory() {
        throw new UnsupportedOperationException("org.kie.builder.impl.KieServicesImpl.getResourceFactory -> TODO");
    }

    public KieRepository getKieRepository() {
        return KieRepositoryImpl.INSTANCE;
    }

    public KieContainer getKieContainer() {
        return new KieContainerImpl(getKieRepository().getDefaultGAV());
    }
    
    public KieContainer getKieContainer(GAV gav) {
        return new KieContainerImpl(gav);
    }
    

    public KieBuilder newKieBuilder(File file) {
        return new KieBuilderImpl(file);
    }
    
    public KieBuilder newKieBuilder(KieFileSystem kieFileSystem) {
        return new KieBuilderImpl(kieFileSystem);
    }    

    public KieScanner newKieScanner(KieContainer kieContainer) {
        InternalKieScanner scanner = (InternalKieScanner)ServiceRegistryImpl.getInstance().get( KieScanner.class );
        scanner.setKieContainer(kieContainer);
        return scanner;
    }
    
}


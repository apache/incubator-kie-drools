package org.kie.builder.impl;

import org.kie.builder.GAV;
import org.kie.builder.KieContainer;
import org.kie.builder.KieRepository;
import org.kie.builder.KieServices;
import org.kie.io.ResourceFactory;

public class KieServicesImpl implements KieServices {

    public ResourceFactory getResourceFactory() {
        throw new UnsupportedOperationException("org.kie.builder.impl.KieServicesImpl.getResourceFactory -> TODO");
    }

    public KieRepository getKieRepository() {
        return KieRepositoryImpl.INSTANCE;
    }

    public KieContainer getKieContainer(GAV gav) {
        return new KieContainerImpl(gav);
    }
}


package org.kie.builder.impl;

import org.drools.kproject.GroupArtifactVersion;
import org.kie.builder.GAV;
import org.kie.builder.KieJar;
import org.kie.builder.KieRepository;
import org.kie.builder.Results;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class KieRepositoryImpl implements KieRepository {

    private static final String DEFAULT_VERSION = "1.0.0-SNAPSHOT";
    private static final String DEFAULT_ARTIFACT = "artifact";
    private static final String DEFAULT_GROUP = "org.default";
    
    static final KieRepositoryImpl INSTANCE = new KieRepositoryImpl();

    private final Map<GAV, KieJar> kieJars = new HashMap<GAV, KieJar>();
    
    private AtomicReference<GAV> defaultGAV = new AtomicReference( new GroupArtifactVersion(DEFAULT_GROUP, DEFAULT_ARTIFACT, DEFAULT_VERSION) );

    public void setDefaultGAV(GAV gav) {
        this.defaultGAV.set( gav );
    }

    public GAV getDefaultGAV() {
        return this.defaultGAV.get();
    }    
    
    public void addKieJar(KieJar kjar) {
        kieJars.put(kjar.getGAV(), kjar);
    }

    public Results verfyKieJar(GAV gav) {
        throw new UnsupportedOperationException("org.kie.builder.impl.KieRepositoryImpl.verfyKieJar -> TODO");

    }

    public KieJar getKieJar(GAV gav) {
        return kieJars.get(gav);
    }
}

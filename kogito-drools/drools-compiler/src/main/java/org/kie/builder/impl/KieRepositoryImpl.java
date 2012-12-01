package org.kie.builder.impl;

import org.drools.kproject.GroupArtifactVersion;
import org.kie.builder.GAV;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieContainer;
import org.kie.builder.KieJar;
import org.kie.builder.KieRepository;
import org.kie.builder.KieScanner;
import org.kie.builder.KieServices;
import org.kie.builder.Results;
import org.kie.util.ServiceRegistryImpl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class KieRepositoryImpl implements KieRepository {

    private static final String DEFAULT_VERSION = "1.0.0-SNAPSHOT";
    private static final String DEFAULT_ARTIFACT = "artifact";
    private static final String DEFAULT_GROUP = "org.default";
    
    static final KieRepositoryImpl INSTANCE = new KieRepositoryImpl();

    private final Map<GAV, KieJar> kieJars = new HashMap<GAV, KieJar>();
    
    private final AtomicReference<GAV> defaultGAV = new AtomicReference( new GroupArtifactVersion(DEFAULT_GROUP, DEFAULT_ARTIFACT, DEFAULT_VERSION) );

    private InternalKieScanner internalKieScanner;

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
        KieJar kieJar = kieJars.get(gav);
        return kieJar != null ? kieJar : loadKieJarFromMavenRepo(gav);
    }

    private KieJar loadKieJarFromMavenRepo(GAV gav) {
        File artifact = getInternalKieScanner().loadArtifact(gav);
        if (artifact == null) {
            return null;
        }

        KieBuilder kieBuilder = KieServices.Factory.get().newKieBuilder(artifact);
        Results results = kieBuilder.build();
        return results.getInsertedMessages().isEmpty() ? kieBuilder.getKieJar() : null;
    }

    private InternalKieScanner getInternalKieScanner() {
        if (internalKieScanner == null) {
            try {
                internalKieScanner = (InternalKieScanner) ServiceRegistryImpl.getInstance().get( KieScanner.class );
            } catch (Exception e) {
                // kie-ci is not on the classpath
                internalKieScanner = new DummyKieScanner();
            }
        }
        return internalKieScanner;
    }

    private static class DummyKieScanner implements InternalKieScanner {

        public void setKieContainer(KieContainer kieContainer) { }

        public File loadArtifact(GAV gav) {
            return null;
        }

        public void start(long pollingInterval) { }

        public void stop() { }

        public void scanNow() { }
    }
}

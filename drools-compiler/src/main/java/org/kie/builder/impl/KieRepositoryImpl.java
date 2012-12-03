package org.kie.builder.impl;

import org.drools.cdi.KProjectExtension;
import org.drools.kproject.GAVImpl;
import org.kie.builder.GAV;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieContainer;
import org.kie.builder.KieModule;
import org.kie.builder.KieRepository;
import org.kie.builder.KieScanner;
import org.kie.builder.KieServices;
import org.kie.builder.Results;
import org.kie.util.ServiceRegistryImpl;
import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Singleton;

@Singleton
public class KieRepositoryImpl
    implements
    KieRepository {
    private static final Logger        log              = LoggerFactory.getLogger( KieRepositoryImpl.class );

    private static final String        DEFAULT_VERSION  = "1.0.0-SNAPSHOT";
    private static final String        DEFAULT_ARTIFACT = "artifact";
    private static final String        DEFAULT_GROUP    = "org.default";

    static final KieRepositoryImpl     INSTANCE         = new KieRepositoryImpl();

    private final Map<GAV, KieModule>     kieJars          = new HashMap<GAV, KieModule>();

    private final AtomicReference<GAV> defaultGAV       = new AtomicReference( new GAVImpl( DEFAULT_GROUP,
                                                                                            DEFAULT_ARTIFACT,
                                                                                            DEFAULT_VERSION ) );

    private InternalKieScanner         internalKieScanner;

    public void setDefaultGAV(GAV gav) {
        this.defaultGAV.set( gav );
    }

    public GAV getDefaultGAV() {
        return this.defaultGAV.get();
    }

    public void addKieJar(KieModule kjar) {
        kieJars.put( kjar.getGAV(),
                     kjar );
    }

    public Results verfyKieJar(GAV gav) {
        throw new UnsupportedOperationException( "org.kie.builder.impl.KieRepositoryImpl.verfyKieJar -> TODO" );
    }

    public KieModule getKieJar(GAV gav) {
        KieModule kieJar = kieJars.get( gav );
        if ( kieJar == null ) {
            log.debug( "KieJar Lookup. GAV {} was not in cache, checking classpath",
                       gav.toExternalForm() );
            kieJar = checkClasspathForKieJar( gav );
        }
        
        if ( kieJar == null ) {
            log.debug( "KieJar Lookup. GAV {} was not in cache, checking maven repository",
                       gav.toExternalForm() );   
            kieJar =  loadKieJarFromMavenRepo( gav );
        }
        
        return kieJar; 
    }

    private KieModule checkClasspathForKieJar(GAV gav) {
        // check classpath
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        URL url = classLoader.getResource( "((GAVImpl)gav).getPomPropertiesPath()" );
        if ( url == null ) {
            log.debug( "KieJar Lookup. GAV {} is not on the classpath",
                       gav.toExternalForm() );
        }
        
//        KieBuilder kieBuilder = KieServices.Factory.get().newKieBuilder(artifact.getFile());
//        Results results = kieBuilder.build();
//        return results.getInsertedMessages().isEmpty() ? kieBuilder.getKieJar() : null;        
        
        return null;
    }

    private KieModule loadKieJarFromMavenRepo(GAV gav) {
        return getInternalKieScanner().loadArtifact( gav );
    }

    private InternalKieScanner getInternalKieScanner() {
        if ( internalKieScanner == null ) {
            try {
                internalKieScanner = (InternalKieScanner) ServiceRegistryImpl.getInstance().get( KieScanner.class );
            } catch ( Exception e ) {
                // kie-ci is not on the classpath
                internalKieScanner = new DummyKieScanner();
            }
        }
        return internalKieScanner;
    }

    private static class DummyKieScanner
        implements
        InternalKieScanner {

        public void setKieContainer(KieContainer kieContainer) {
        }

        public KieModule loadArtifact(GAV gav) {
            return null;
        }

        public void start(long pollingInterval) {
        }

        public void stop() {
        }

        public void scanNow() {
        }
    }
    
    
}

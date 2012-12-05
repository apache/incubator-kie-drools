package org.kie.builder.impl;

import org.drools.io.internal.InternalResource;
import org.drools.kproject.GAVImpl;
import org.drools.kproject.models.KieModuleModelImpl;
import org.kie.builder.GAV;
import org.kie.builder.KieContainer;
import org.kie.builder.KieModule;
import org.kie.builder.KieRepository;
import org.kie.builder.KieScanner;
import org.kie.builder.Results;
import org.kie.io.Resource;
import org.kie.util.ServiceRegistryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class KieRepositoryImpl
    implements
    KieRepository {
    private static final Logger        log              = LoggerFactory.getLogger( KieRepositoryImpl.class );

    private static final String        DEFAULT_VERSION  = "1.0.0-SNAPSHOT";
    private static final String        DEFAULT_ARTIFACT = "artifact";
    private static final String        DEFAULT_GROUP    = "org.default";

    static final KieRepositoryImpl     INSTANCE         = new KieRepositoryImpl();

    private final Map<GAV, KieModule> kieModules        = new HashMap<GAV, KieModule>();

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

    public void addKieModule(KieModule kieModule) {
        kieModules.put(kieModule.getGAV(),
                       kieModule);
        log.info( "KieModule was added:" + kieModule);
    }

    public Results verfyKieModule(GAV gav) {
        throw new UnsupportedOperationException( "org.kie.builder.impl.KieRepositoryImpl.verfyKieModule -> TODO" );
    }

    public KieModule getKieModule(GAV gav) {
        KieModule kieModule = kieModules.get( gav );
        if ( kieModule == null ) {
            log.debug( "KieModule Lookup. GAV {} was not in cache, checking classpath",
                       gav.toExternalForm() );
            kieModule = checkClasspathForKieModule(gav);
        }
        
        if ( kieModule == null ) {
            log.debug( "KieModule Lookup. GAV {} was not in cache, checking maven repository",
                       gav.toExternalForm() );   
            kieModule =  loadKieModuleFromMavenRepo(gav);
        }
        
        return kieModule;
    }

    private KieModule checkClasspathForKieModule(GAV gav) {
        // check classpath
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        URL url = classLoader.getResource( "((GAVImpl)gav).getPomPropertiesPath()" );
        if ( url == null ) {
            log.debug( "KieJar Lookup. GAV {} is not on the classpath",
                       gav.toExternalForm() );
        }
        
//        KieBuilder kieBuilder = KieServices.Factory.get().newKieBuilder(artifact.getFile());
//        Results results = kieBuilder.build();
//        return results.getInsertedMessages().isEmpty() ? kieBuilder.getKieModule() : null;
        
        return null;
    }

    private KieModule loadKieModuleFromMavenRepo(GAV gav) {
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


    public KieModule addKieModule(Resource resource, Resource... dependencies) {
        log.info( "Adding KieModule from resource :" + resource  );
        KieModule kModule = getKieModule( resource );
        
        if ( dependencies != null && dependencies.length > 0 ) {
            Map<GAV, InternalKieModule> list = new HashMap<GAV, InternalKieModule>();
            for ( Resource depRes : dependencies ) {
                InternalKieModule depKModule = ( InternalKieModule ) getKieModule( depRes );
                log.info( "Adding KieModule dependency from resource :" + resource  );
                list.put( depKModule.getGAV(), depKModule );
            }
            ((InternalKieModule)kModule).setDependencies( list );
        }
        addKieModule( kModule );
        return kModule;
    }
    
    public KieModule getKieModule(Resource resource) {
        InternalResource res = (InternalResource) resource;
        try {
            // find kmodule.xml
            String urlPath = res.getURL().toExternalForm();
            if (res.isDirectory() ) {
                if ( !urlPath.endsWith( "/" ) ) {
                    urlPath = urlPath + "/";
                }
                urlPath = urlPath  + KieModuleModelImpl.KMODULE_JAR_PATH;
                
            } else {
                urlPath = "jar:"+ urlPath  + "!/" + KieModuleModelImpl.KMODULE_JAR_PATH;              
            }
            KieModule kModule = ClasspathKieProject.fetchKModule( new URL( urlPath )  );
            log.debug( "fetched KieModule from resource :" + resource  );
            return kModule;
        } catch ( Exception e ) {
            throw new RuntimeException("Unable to fetch module from resource :" + res, e);
        }          
    }
        
}

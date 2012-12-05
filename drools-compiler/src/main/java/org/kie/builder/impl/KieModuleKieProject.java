package org.kie.builder.impl;

import org.drools.core.util.ClassUtils;
import org.kie.builder.GAV;
import org.kie.builder.KieRepository;
import org.kie.builder.KieSessionModel;
import org.kie.util.ClassLoaderUtil;
import org.kie.util.CompositeClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Discovers all KieModules on the classpath, via the kmodule.xml file.
 * KieBaseModels and KieSessionModels are then indexed, with helper lookups
 * Each resulting KieModule is added to the KieRepository
 *
 */
public class KieModuleKieProject extends AbstractKieProject {

    private static final Logger                  log               = LoggerFactory.getLogger( KieModuleKieProject.class );

    private Map<GAV, InternalKieModule>          kieModules;

    private final Map<String, InternalKieModule> kJarFromKBaseName = new HashMap<String, InternalKieModule>();

    private final Map<String, KieSessionModel>   kSessionModels    = new HashMap<String, KieSessionModel>();

    private InternalKieModule                    kieModule;
    private KieRepository                        kr;
    private CompositeClassLoader                 cl;

    public KieModuleKieProject(InternalKieModule kieModule,
                               KieRepository kr) {
        this.kieModule = kieModule;
        this.kr = kr;
        this.cl = ClassLoaderUtil.getClassLoader( null, null, true );
    }

    public void init() {
        if ( kieModules == null ) {
            kieModules = new HashMap<GAV, InternalKieModule>();
            kieModules.putAll( kieModule.getDependencies() );
            kieModules.put( kieModule.getGAV(),
                            kieModule );
            AbstractKieModule.indexParts( kieModules,
                                          kBaseModels,
                                          kSessionModels,
                                          kJarFromKBaseName );
            initClassLaoder();
        }
    }

    public void initClassLaoder() {
        Map<String, byte[]> classes = new HashMap<String, byte[]>();
        for ( InternalKieModule kModule : kieModules.values() ) {
            for ( String fileName : kModule.getFileNames() ) {
                if ( fileName.endsWith( ".class" ) ) {
                    classes.put( fileName,
                                 kModule.getBytes( fileName ) );
                }
            }
        }
        if ( !classes.isEmpty() ) {
            cl.addClassLoaderToEnd( new ClassUtils.MapClassLoader( classes,
                                                                   cl ) );
        }
    }

    public GAV getGAV() {
        return kieModule.getGAV();
    }

    public InternalKieModule getKieModuleForKBase(String kBaseName) {
        return this.kJarFromKBaseName.get( kBaseName );
    }

    public boolean kieBaseExists(String kBaseName) {
        return kBaseModels.containsKey( kBaseName );
    }

    public boolean kieSessionExists(String kSessionName) {
        return kSessionModels.containsKey( kSessionName );
    }

    public KieSessionModel getKieSessionModel(String kSessionName) {
        return kSessionModels.get( kSessionName );
    }

    @Override
    public CompositeClassLoader getClassLoader() {
        return this.cl;
    }

}

package org.drools.compiler.kie.builder.impl;

import org.drools.core.util.ClassUtils;
import org.kie.builder.ReleaseId;
import org.kie.builder.KieRepository;
import org.kie.internal.utils.ClassLoaderUtil;
import org.kie.internal.utils.CompositeClassLoader;
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

    private Map<ReleaseId, InternalKieModule>          kieModules;

    private final Map<String, InternalKieModule> kJarFromKBaseName = new HashMap<String, InternalKieModule>();

    private final InternalKieModule              kieModule;
    private final KieRepository                  kr;
    private final CompositeClassLoader           cl;

    public KieModuleKieProject(InternalKieModule kieModule,
                               KieRepository kr) {
        this.kieModule = kieModule;
        this.kr = kr;
        this.cl = ClassLoaderUtil.getClassLoader( null, null, true );
    }

    public void init() {
        if ( kieModules == null ) {
            kieModules = new HashMap<ReleaseId, InternalKieModule>();
            kieModules.putAll( kieModule.getDependencies() );
            kieModules.put( kieModule.getReleaseId(),
                            kieModule );
            indexParts( kieModules, kJarFromKBaseName );
            initClassLoader();
        }
    }

    private void initClassLoader() {
        Map<String, byte[]> classes = getClassesMap();
        if ( !classes.isEmpty() ) {
            cl.addClassLoaderToEnd( new ClassUtils.MapClassLoader( classes, cl ) );
        }
    }

    private Map<String, byte[]> getClassesMap() {
        Map<String, byte[]> classes = new HashMap<String, byte[]>();
        for ( InternalKieModule kModule : kieModules.values() ) {
            classes.putAll(kModule.getClassesMap());
        }
        return classes;
    }

    public ReleaseId getGAV() {
        return kieModule.getReleaseId();
    }

    public InternalKieModule getKieModuleForKBase(String kBaseName) {
        return this.kJarFromKBaseName.get(kBaseName);
    }

    public boolean kieBaseExists(String kBaseName) {
        return kBaseModels.containsKey(kBaseName);
    }

    @Override
    public CompositeClassLoader getClassLoader() {
        return this.cl;
    }

}

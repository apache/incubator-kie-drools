package org.drools.compiler.kie.builder.impl;

import org.drools.core.common.ProjectClassLoader;
import org.kie.api.builder.ReleaseId;
import org.kie.internal.utils.ClassLoaderResolver;
import org.kie.internal.utils.NoDepsClassLoaderResolver;
import org.kie.internal.utils.ServiceRegistryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.drools.core.common.ProjectClassLoader.createProjectClassLoader;
import static org.drools.core.util.ClassUtils.convertResourceToClassName;

/**
 * Discovers all KieModules on the classpath, via the kmodule.xml file.
 * KieBaseModels and KieSessionModels are then indexed, with helper lookups
 * Each resulting KieModule is added to the KieRepository
 *
 */
public class KieModuleKieProject extends AbstractKieProject {

    private static final Logger            log               = LoggerFactory.getLogger( KieModuleKieProject.class );

    private List<InternalKieModule>        kieModules;

    private Map<String, InternalKieModule> kJarFromKBaseName = new HashMap<String, InternalKieModule>();

    private InternalKieModule              kieModule;

    private ProjectClassLoader             cl;

    public KieModuleKieProject( InternalKieModule kieModule ) {
        this( kieModule, null );
    }
    
    public KieModuleKieProject(InternalKieModule kieModule, ClassLoader parent) {
        this.kieModule = kieModule;
        if( parent == null ) {
            ClassLoaderResolver resolver;
            try {
                resolver = ServiceRegistryImpl.getInstance().get(ClassLoaderResolver.class);
            } catch ( Exception cne ) {
                resolver = new NoDepsClassLoaderResolver();
            }
            parent = resolver.getClassLoader( kieModule );
        }
        this.cl = createProjectClassLoader( parent );
    }

    public void init() {
        if ( kieModules == null ) {
            kieModules = new ArrayList<InternalKieModule>();
            kieModules.addAll( kieModule.getKieDependencies().values() );
            kieModules.add( kieModule );
            indexParts( kieModules, kJarFromKBaseName );
            initClassLoader( cl );
        }
    }

    private void initClassLoader(ProjectClassLoader projectCL) {
        for ( Map.Entry<String, byte[]> entry : getClassesMap().entrySet() ) {
            if ( entry.getValue() != null ) {
                String resourceName = entry.getKey();
                String className = convertResourceToClassName( resourceName );
                projectCL.storeClass( className, resourceName, entry.getValue() );
            }
        }
    }

    private Map<String, byte[]> getClassesMap() {
        Map<String, byte[]> classes = new HashMap<String, byte[]>();
        for ( InternalKieModule kModule : kieModules ) {
            // avoid to take type declarations defined directly in this kieModule since they have to be recompiled
            classes.putAll( kModule.getClassesMap( kModule != this.kieModule ) );
        }
        return classes;
    }

    public ReleaseId getGAV() {
        return kieModule.getReleaseId();
    }

    public long getCreationTimestamp() {
        return kieModule.getCreationTimestamp();
    }

    public InternalKieModule getKieModuleForKBase(String kBaseName) {
        return this.kJarFromKBaseName.get( kBaseName );
    }

    public InternalKieModule getInternalKieModule() {
        return kieModule;
    }

    public ClassLoader getClassLoader() {
        return this.cl;
    }

    public ClassLoader getClonedClassLoader() {
        ProjectClassLoader clonedCL = createProjectClassLoader( cl.getParent() );
        initClassLoader( clonedCL );
        return clonedCL;
    }

    public void updateToModule(InternalKieModule updatedKieModule) {
        this.kieModules = null;
        this.kJarFromKBaseName.clear();
        cleanIndex();

        ReleaseId currentReleaseId = this.kieModule.getReleaseId();
        ReleaseId updatingReleaseId = updatedKieModule.getReleaseId();

        if (currentReleaseId.getGroupId().equals(updatingReleaseId.getGroupId()) &&
            currentReleaseId.getArtifactId().equals(updatingReleaseId.getArtifactId())) {
            this.kieModule = updatedKieModule;
        } else if (this.kieModule.getKieDependencies().keySet().contains(updatingReleaseId)) {
            this.kieModule.addKieDependency(updatedKieModule);
        }

        //this.cl.getStore().clear(); // can we do this in order to preserve the reference to the classloader?
        this.init(); // this might override class definitions, not sure we can do it any other way though
    }
}

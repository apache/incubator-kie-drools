package org.drools.compiler.kie.builder.impl;

import org.drools.core.common.ProjectClassLoader;
import org.drools.core.common.ResourceProvider;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.io.Resource;
import org.kie.internal.utils.ClassLoaderResolver;
import org.kie.internal.utils.NoDepsClassLoaderResolver;
import org.kie.internal.utils.ServiceRegistryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
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
        this.cl = createProjectClassLoader( parent, createKieModuleResourceProvider(kieModule) );
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
        ProjectClassLoader clonedCL = createProjectClassLoader( cl.getParent(), createKieModuleResourceProvider(kieModule) );
        initClassLoader( clonedCL );
        return clonedCL;
    }

    public void updateToModule(InternalKieModule updatedKieModule) {
        this.kieModules = null;
        this.kJarFromKBaseName.clear();

        ReleaseId currentReleaseId = this.kieModule.getReleaseId();
        ReleaseId updatingReleaseId = updatedKieModule.getReleaseId();

        if (currentReleaseId.getGroupId().equals(updatingReleaseId.getGroupId()) &&
            currentReleaseId.getArtifactId().equals(updatingReleaseId.getArtifactId())) {
            this.kieModule = updatedKieModule;
        } else if (this.kieModule.getKieDependencies().keySet().contains(updatingReleaseId)) {
            this.kieModule.addKieDependency(updatedKieModule);
        }

        synchronized (this) {
            cleanIndex();
            init(); // this might override class definitions, not sure we can do it any other way though
        }
    }

    @Override
    public synchronized KieBaseModel getDefaultKieBaseModel() {
        return super.getDefaultKieBaseModel();
    }

    @Override
    public synchronized KieSessionModel getDefaultKieSession() {
        return super.getDefaultKieSession();
    }

    @Override
    public synchronized KieSessionModel getDefaultStatelessKieSession() {
        return super.getDefaultStatelessKieSession();
    }

    @Override
    public synchronized KieBaseModel getKieBaseModel(String kBaseName) {
        return super.getKieBaseModel(kBaseName);
    }

    @Override
    public synchronized KieSessionModel getKieSessionModel(String kSessionName) {
        return super.getKieSessionModel(kSessionName);
    }

    private KieModuleResourceProvider createKieModuleResourceProvider(InternalKieModule kieModule) {
        try {
            URL url = kieModule.getFile().toURI().toURL();
            return new KieModuleResourceProvider(kieModule, url);
        } catch (Exception e) {
            return null;
        }
    }

    private static class KieModuleResourceProvider implements ResourceProvider {

        private final InternalKieModule kieModule;
        private final URL kieModuleUrl;

        private KieModuleResourceProvider(InternalKieModule kieModule, URL kieModuleUrl) {
            this.kieModule = kieModule;
            this.kieModuleUrl = kieModuleUrl;
        }

        @Override
        public InputStream getResourceAsStream(String name) throws IOException {
            Resource resource = kieModule.getResource(name);
            return resource != null ? resource.getInputStream() : null;
        }

        @Override
        public URL getResource(String name) {
            return kieModule.hasResource(name) ? createURLForResource(name) : null;
        }

        private URL createURLForResource(String name) {
            try {
                if (kieModule instanceof ZipKieModule) {
                    return new URL("jar", "", kieModuleUrl + "!/" + name);
                } else {
                    return new URL(kieModuleUrl, name);
                }
            } catch (MalformedURLException e) {
                return null;
            }
        }
    }
}

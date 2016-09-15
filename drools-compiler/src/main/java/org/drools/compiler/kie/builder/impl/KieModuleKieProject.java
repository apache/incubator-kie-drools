/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.kie.builder.impl;

import org.drools.core.common.ProjectClassLoader;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.internal.utils.ClassLoaderResolver;
import org.kie.internal.utils.NoDepsClassLoaderResolver;
import org.kie.internal.utils.ServiceRegistryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
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
        this.cl = createProjectClassLoader( parent, kieModule.createResourceProvider() );
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

    public InputStream getPomAsStream() {
        return kieModule.getPomAsStream();
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
        ProjectClassLoader clonedCL = createProjectClassLoader( cl.getParent(), kieModule.createResourceProvider() );
        initClassLoader( clonedCL );
        return clonedCL;
    }

    public Map<String, KieBaseModel> updateToModule(InternalKieModule updatedKieModule) {
        Map<String, KieBaseModel> oldKieBaseModels = new HashMap<String, KieBaseModel>();
        oldKieBaseModels.putAll( kBaseModels );

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

        return oldKieBaseModels;
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
}

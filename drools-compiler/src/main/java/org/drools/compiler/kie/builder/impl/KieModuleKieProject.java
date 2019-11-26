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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.reflective.classloader.ProjectClassLoader;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieSessionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private ProjectClassLoader cl;

    public KieModuleKieProject( InternalKieModule kieModule ) {
        this( kieModule, null );
    }
    
    public KieModuleKieProject(InternalKieModule kieModule, ClassLoader parent) {
        this.kieModule = kieModule;
        this.cl = kieModule.createModuleClassLoader( parent );
    }

    public void init() {
        if ( kieModules == null ) {
            Collection<InternalKieModule> depKieModules = kieModule.getKieDependencies().values();
            indexParts( kieModule, depKieModules, kJarFromKBaseName );
            kieModules = new ArrayList<InternalKieModule>();
            kieModules.addAll( depKieModules );
            kieModules.add( kieModule );
            cl.storeClasses( getClassesMap() );
        }
    }

    private Map<String, byte[]> getClassesMap() {
        Map<String, byte[]> classes = new HashMap<String, byte[]>();
        for ( InternalKieModule kModule : kieModules ) {
            classes.putAll( kModule.getClassesMap() );
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
            // reset resource provider so it will serve resources from updated kmodule
            this.cl.setResourceProvider(kieModule.createResourceProvider());
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

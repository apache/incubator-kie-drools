/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.scanner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.drools.compiler.kie.builder.impl.AbstractKieScanner;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.InternalKieScanner;
import org.drools.compiler.kie.builder.impl.MemoryKieModule;
import org.drools.compiler.kie.builder.impl.ResultsImpl;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.core.impl.InternalKieContainer;
import org.eclipse.aether.artifact.Artifact;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.kie.maven.integration.ArtifactResolver;
import org.kie.maven.integration.DependencyDescriptor;
import org.kie.scanner.management.KieScannerMBean;
import org.kie.scanner.management.KieScannerMBeanImpl;
import org.kie.scanner.management.MBeanUtils;
import org.kie.util.maven.support.DependencyFilter;
import org.kie.util.maven.support.PomModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.compiler.kie.builder.impl.InternalKieModule.createKieModule;

public class KieRepositoryScannerImpl extends AbstractKieScanner<Map<DependencyDescriptor, Artifact>> implements InternalKieScanner {

    private static final Logger log = LoggerFactory.getLogger(KieScanner.class);
    private static final DependencyFilter.ExcludeScopeFilter DEPENDENCY_SCOPEFILTER = new DependencyFilter.ExcludeScopeFilter("test", "provided", "system");

    private DependencyDescriptor kieProjectDescr;

    private Map<ReleaseId, DependencyDescriptor> usedDependencies;

    private ArtifactResolver artifactResolver;

    private KieScannerMBean mbean;

    public synchronized void setKieContainer(KieContainer kieContainer) {
        if (this.kieContainer != null) {
            throw new RuntimeException("Cannot change KieContainer on an already initialized KieScanner");
        }
        this.kieContainer = (InternalKieContainer)kieContainer;
        if (this.kieContainer.getContainerReleaseId() == null) {
            throw new RuntimeException("The KieContainer's ReleaseId cannot be null. Are you using a KieClasspathContainer?");
        }

        artifactResolver = getResolverFor(this.kieContainer, true);
        kieProjectDescr = new DependencyDescriptor(this.kieContainer.getReleaseId(),
                                                   this.kieContainer.getCreationTimestamp());
        usedDependencies = indexArtifacts();

        KieScannersRegistry.register(this);
        changeStatus( Status.STOPPED );
        
        if( MBeanUtils.isMBeanEnabled() ) {
            mbean = new KieScannerMBeanImpl(this);
        }
    }

    private ArtifactResolver getArtifactResolver() {
        if (artifactResolver == null) {
            artifactResolver = ArtifactResolver.create();
        }
        return artifactResolver;
    }

    public synchronized KieModule loadArtifact(ReleaseId releaseId) {
        return loadArtifact(releaseId, getArtifactResolver());
    }

    public synchronized KieModule loadArtifact(ReleaseId releaseId, InputStream pomXml) {
        ArtifactResolver resolver = pomXml != null ?
                                    ArtifactResolver.getResolverFor(pomXml) :
                                    getArtifactResolver();
        return loadArtifact( releaseId, resolver );
    }

    public synchronized KieModule loadArtifact(ReleaseId releaseId, PomModel pomModel) {
        ArtifactResolver resolver = pomModel != null ?
                                    ArtifactResolver.getResolverFor(pomModel) :
                                    getArtifactResolver();
        return loadArtifact( releaseId, resolver );
    }

    private KieModule loadArtifact( ReleaseId releaseId, ArtifactResolver resolver ) {
        Artifact artifact = resolver.resolveArtifact(releaseId);
        return artifact != null ? buildArtifact(artifact, resolver) : loadPomArtifact(releaseId);
    }

    public synchronized String getArtifactVersion(ReleaseId releaseId) {
        if (!releaseId.isSnapshot()) {
            return releaseId.getVersion();
        }
        Artifact artifact = getArtifactResolver().resolveArtifact(releaseId);
        return artifact != null ? artifact.getVersion() : null;
    }

    private KieModule loadPomArtifact(ReleaseId releaseId) {
        ArtifactResolver resolver = ArtifactResolver.getResolverFor(releaseId, false);
        if (resolver == null) {
            return null;
        }

        MemoryKieModule kieModule = new MemoryKieModule(releaseId);
        addDependencies(kieModule, resolver, resolver.getPomDirectDependencies( DependencyFilter.COMPILE_FILTER ));
        return kieModule;
    }

    private InternalKieModule buildArtifact(Artifact artifact, ArtifactResolver resolver) {
        DependencyDescriptor dependencyDescriptor = new DependencyDescriptor(artifact);
        ReleaseId releaseId = dependencyDescriptor.getReleaseId();
        InternalKieModule kieModule = createKieModule(releaseId, artifact.getFile());
        if (kieModule != null) {
            addDependencies(kieModule, resolver, resolver.getArtifactDependecies(dependencyDescriptor.toString()));
        }
        return kieModule;
    }
    
    private void addDependencies(InternalKieModule kieModule, ArtifactResolver resolver, List<DependencyDescriptor> dependencies) {
        for (DependencyDescriptor dep : dependencies) {
            InternalKieModule dependency = (InternalKieModule) KieServices.Factory.get().getRepository().getKieModule(dep.getReleaseId());
            if (dependency != null) {
                kieModule.addKieDependency(dependency);
            } else {
                Artifact depArtifact = resolver.resolveArtifact(dep.getReleaseId());
                if (depArtifact != null && isKJar(depArtifact.getFile())) {
                    ReleaseId depReleaseId = new DependencyDescriptor(depArtifact).getReleaseId();
                    InternalKieModule zipKieModule = createKieModule(depReleaseId, depArtifact.getFile());
                    if (zipKieModule != null) {
                        kieModule.addKieDependency(zipKieModule);
                    }
                }
            }
        }
    }

    @Override
    protected Map<DependencyDescriptor, Artifact> internalScan() {
        Map<DependencyDescriptor, Artifact> updatedArtifacts = scanForUpdates();
        return updatedArtifacts.isEmpty() ? null : updatedArtifacts;
    }

    @Override
    protected void internalUpdate( Map<DependencyDescriptor, Artifact> updatedArtifacts ) {
        boolean allUpdatesSucceeded = true;
        // build the dependencies first
        Map.Entry<DependencyDescriptor, Artifact> containerEntry = null;
        for (Map.Entry<DependencyDescriptor, Artifact> entry : updatedArtifacts.entrySet()) {
            if (entry.getKey().isSameArtifact(kieContainer.getContainerReleaseId())) {
                containerEntry = entry;
            } else {
                allUpdatesSucceeded = updateKieModule(entry.getKey(), entry.getValue()) && allUpdatesSucceeded;
            }
        }
        if (containerEntry != null) {
            allUpdatesSucceeded = updateKieModule(containerEntry.getKey(), containerEntry.getValue()) && allUpdatesSucceeded;
        }

        if ( allUpdatesSucceeded ) {
            log.info("The following artifacts have been updated: " + updatedArtifacts);
        } else {
            log.error("Some errors occured while updating the following artifacts: " + updatedArtifacts);
        }
    }

    private boolean updateKieModule(DependencyDescriptor oldDependency, Artifact artifact) {
        ReleaseId newReleaseId = new DependencyDescriptor( artifact).getReleaseId();
        InternalKieModule kieModule = createKieModule(newReleaseId, artifact.getFile());
        if (kieModule != null) {
            addDependencies(kieModule, artifactResolver, artifactResolver.getArtifactDependecies(newReleaseId.toString()));
            ResultsImpl messages = kieModule.build();
            if ( messages.filterMessages(Message.Level.ERROR).isEmpty()) {
                Results updateMessages = kieContainer.updateDependencyToVersion(oldDependency.getArtifactReleaseId(), newReleaseId);
                oldDependency.setArtifactVersion(artifact.getVersion());
                messages.getMessages().addAll( updateMessages.getMessages() ); // append all update Results into build Results to notify listeners
            }
            listeners.fireKieScannerUpdateResultsEventImpl(messages);
            return !messages.hasMessages(Message.Level.ERROR);
        }
        return false;
    }

    private Map<DependencyDescriptor, Artifact> scanForUpdates() {
        artifactResolver = getResolverFor(kieContainer, true);

        if ( !kieProjectDescr.getReleaseId().equals( this.kieContainer.getReleaseId() ) ) {
            kieProjectDescr = new DependencyDescriptor( this.kieContainer.getReleaseId(),
                                                        this.kieContainer.getCreationTimestamp() );
        }

        Map<DependencyDescriptor, Artifact> newArtifacts = new HashMap<>();

        Artifact newArtifact = artifactResolver.resolveArtifact(this.kieContainer.getConfiguredReleaseId());
        if (newArtifact != null) {
            DependencyDescriptor resolvedDep = new DependencyDescriptor(newArtifact);
            if (resolvedDep.isNewerThan(kieProjectDescr)) {
                newArtifacts.put(kieProjectDescr, newArtifact);
                kieProjectDescr = new DependencyDescriptor(newArtifact);
            }
        }

        for (DependencyDescriptor dep : artifactResolver.getAllDependecies()) {
            ReleaseId artifactId = dep.getReleaseIdWithoutVersion();
            DependencyDescriptor oldDep = usedDependencies.get(artifactId);
            if (oldDep != null) {
                newArtifact = artifactResolver.resolveArtifact(dep.getReleaseId());
                if (newArtifact != null) {
                    DependencyDescriptor newDep = new DependencyDescriptor(newArtifact);
                    if (newDep.isNewerThan(oldDep)) {
                        newArtifacts.put(oldDep, newArtifact);
                        usedDependencies.put(artifactId, newDep);
                    }
                }
            }
        }

        return newArtifacts;
    }

    private Map<ReleaseId, DependencyDescriptor> indexArtifacts() {
        Map<ReleaseId, DependencyDescriptor> depsMap = new HashMap<>();
        for (DependencyDescriptor dep : artifactResolver.getAllDependecies(DEPENDENCY_SCOPEFILTER)) {
            Artifact artifact = artifactResolver.resolveArtifact(dep.getReleaseId());
            if (artifact != null) {
                if ( log.isDebugEnabled() ) {
                     log.debug( artifact + " resolved to  " + artifact.getFile() );
                 }
                 if ( isKJar( artifact.getFile() ) ) {
                     depsMap.put( dep.getReleaseIdWithoutVersion(), new DependencyDescriptor( artifact ) );
                 }
            }
        }
        return depsMap;
    }

    private boolean isKJar(File jar) {
        try (ZipFile zipFile = new ZipFile( jar )) {
            ZipEntry zipEntry = zipFile.getEntry( KieModuleModelImpl.KMODULE_JAR_PATH.asString() );
            return zipEntry != null;
        } catch (IOException e) {
            throw new RuntimeException("Failed to open Zip file '" + jar.getAbsolutePath() + "'!", e);
        }

    }
    
    public synchronized KieScannerMBean getMBean() {
        return this.mbean;
    }

    public static ArtifactResolver getResolverFor( InternalKieContainer kieContainer, boolean allowDefaultPom ) {
        return ArtifactResolver.getResolverFor( kieContainer.getPomAsStream(), kieContainer.getReleaseId(), allowDefaultPom );
    }
}

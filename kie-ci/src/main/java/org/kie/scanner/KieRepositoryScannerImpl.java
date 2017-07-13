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

package org.kie.scanner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.drools.core.impl.InternalKieContainer;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.InternalKieScanner;
import org.drools.compiler.kie.builder.impl.MemoryKieModule;
import org.drools.compiler.kie.builder.impl.ResultsImpl;
import org.drools.compiler.kie.builder.impl.ZipKieModule;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.eclipse.aether.artifact.Artifact;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.event.kiescanner.KieScannerEventListener;
import org.kie.api.runtime.KieContainer;
import org.appformer.maven.integration.ArtifactResolver;
import org.appformer.maven.integration.DependencyDescriptor;
import org.appformer.maven.support.DependencyFilter;
import org.appformer.maven.support.PomModel;
import org.kie.scanner.event.KieScannerEventSupport;
import org.kie.scanner.management.KieScannerMBean;
import org.kie.scanner.management.KieScannerMBeanImpl;
import org.kie.scanner.management.MBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.buildKieModule;
import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.setDefaultsforEmptyKieModule;
import static org.drools.compiler.kproject.ReleaseIdImpl.adapt;

public class KieRepositoryScannerImpl implements InternalKieScanner {

    private Timer timer;

    private static final Logger log = LoggerFactory.getLogger(KieScanner.class);

    private InternalKieContainer kieContainer;

    private DependencyDescriptor kieProjectDescr;

    private Map<ReleaseId, DependencyDescriptor> usedDependencies;

    private ArtifactResolver artifactResolver;

    private volatile Status status = Status.STARTING;

    private KieScannerMBean mbean;

    private long pollingInterval;
    
    private KieScannerEventSupport listeners = new KieScannerEventSupport();
    
    @Override
    public void addListener(KieScannerEventListener listener) {
        listeners.addEventListener(listener);
    }

    @Override
    public void removeListener(KieScannerEventListener listener) {
        listeners.removeEventListener(listener);
    }

    @Override
    public Collection<KieScannerEventListener> getListeners() {
        return listeners.getEventListeners();
    }
    
    private void changeStatus( Status status ) {
        this.status = status;
        listeners.fireKieScannerStatusChangeEventImpl(status);
    }
    
    public synchronized void setKieContainer(KieContainer kieContainer) {
        if (this.kieContainer != null) {
            throw new RuntimeException("Cannot change KieContainer on an already initialized KieScanner");
        }
        this.kieContainer = (InternalKieContainer)kieContainer;
        if (this.kieContainer.getContainerReleaseId() == null) {
            throw new RuntimeException("The KieContainer's ReleaseId cannot be null. Are you using a KieClasspathContainer?");
        }

        kieProjectDescr = new DependencyDescriptor(this.kieContainer.getReleaseId(),
                                                   this.kieContainer.getCreationTimestamp());

        artifactResolver = getResolverFor(this.kieContainer, true);
        usedDependencies = indexArtifacts(artifactResolver);

        KieScannersRegistry.register(this);
        changeStatus( Status.STOPPED );
        
        if( MBeanUtils.isMBeanEnabled() ) {
            mbean = new KieScannerMBeanImpl(this);
        }
    }

    private ArtifactResolver getArtifactResolver() {
        if (artifactResolver == null) {
            artifactResolver = new ArtifactResolver();
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

    public synchronized ReleaseId getScannerReleaseId() {
        return kieContainer.getContainerReleaseId();
    }

    public synchronized ReleaseId getCurrentReleaseId() {
        return kieContainer.getReleaseId();
    }

    public synchronized Status getStatus() {
        return status;
    }

    private KieModule loadPomArtifact(ReleaseId releaseId) {
        ArtifactResolver resolver = ArtifactResolver.getResolverFor(releaseId, false);
        if (resolver == null) {
            return null;
        }

        MemoryKieModule kieModule = new MemoryKieModule(releaseId);
        addDependencies(kieModule, resolver, resolver.getPomDirectDependencies( DependencyFilter.COMPILE_FILTER ));
        build(kieModule);
        return kieModule;
    }

    private InternalKieModule buildArtifact(Artifact artifact, ArtifactResolver resolver) {
        DependencyDescriptor dependencyDescriptor = new DependencyDescriptor(artifact);
        ReleaseId releaseId = adapt( dependencyDescriptor.getReleaseId() );
        if (releaseId.isSnapshot()) {
            ((ReleaseIdImpl)releaseId).setSnapshotVersion(artifact.getVersion());
        }
        ZipKieModule kieModule = createZipKieModule(releaseId, artifact.getFile());
        if (kieModule != null) {
            addDependencies(kieModule, resolver, resolver.getArtifactDependecies(dependencyDescriptor.toString()));
            build(kieModule);
        }
        return kieModule;
    }
    
    private void addDependencies(InternalKieModule kieModule, ArtifactResolver resolver, List<DependencyDescriptor> dependencies) {
        for (DependencyDescriptor dep : dependencies) {
            InternalKieModule dependency = (InternalKieModule) KieServices.Factory.get().getRepository().getKieModule(adapt( dep.getReleaseId() ));
            if (dependency != null) {
                kieModule.addKieDependency(dependency);
            } else {
                Artifact depArtifact = resolver.resolveArtifact(dep.getReleaseId());
                if (depArtifact != null && isKJar(depArtifact.getFile())) {
                    ReleaseId depReleaseId = adapt( new DependencyDescriptor(depArtifact).getReleaseId() );
                    ZipKieModule zipKieModule = createZipKieModule(depReleaseId, depArtifact.getFile());
                    if (zipKieModule != null) {
                        kieModule.addKieDependency(zipKieModule);
                    }
                }
            }
        }
    }

    private static ZipKieModule createZipKieModule( org.appformer.maven.support.AFReleaseId releaseId, File jar ) {
        KieModuleModel kieModuleModel = getKieModuleModelFromJar(jar);
        return kieModuleModel != null ? new ZipKieModule(adapt( releaseId ), kieModuleModel, jar) : null;
    }

    private static KieModuleModel getKieModuleModelFromJar(File jar) {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile( jar );
            ZipEntry zipEntry = zipFile.getEntry(KieModuleModelImpl.KMODULE_JAR_PATH);
            KieModuleModel kieModuleModel = KieModuleModelImpl.fromXML(zipFile.getInputStream(zipEntry));
            setDefaultsforEmptyKieModule(kieModuleModel);
            return kieModuleModel;
        } catch ( Exception e ) {
        } finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
				}
			}
        }
        return null;
    }

    private ResultsImpl build(InternalKieModule kieModule) {
        ResultsImpl messages = new ResultsImpl();
        buildKieModule(kieModule, messages);
        return messages;
    }

    public synchronized void start(long pollingInterval) {
        if (getStatus() == Status.SHUTDOWN ) {
            throw new IllegalStateException("The scanner was shut down and can no longer be started.");
        }
        if (pollingInterval <= 0) {
            throw new IllegalArgumentException("pollingInterval must be positive");
        }
        if (timer != null) {
            throw new IllegalStateException("The scanner is already running");
        }
        startScanTask(pollingInterval);
    }

    public synchronized void stop() {
        if (getStatus() == Status.SHUTDOWN ) {
            throw new IllegalStateException("The scanner was already shut down.");
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        this.pollingInterval = 0;
        changeStatus( Status.STOPPED );
    }

    public synchronized long getPollingInterval() {
        return this.pollingInterval;
    }
    
    public void shutdown() {
        if( getStatus() != Status.SHUTDOWN ) {
            stop(); // making sure it is stopped
            changeStatus( Status.SHUTDOWN );
        }
    }

    private void startScanTask(long pollingInterval) {
        changeStatus( Status.RUNNING );
        this.pollingInterval = pollingInterval;
        timer = new Timer(true);
        timer.schedule(new ScanTask(), pollingInterval, pollingInterval);
    }

    private class ScanTask extends TimerTask {
        public void run() {
            synchronized (KieRepositoryScannerImpl.this) {
                // don't scan if the scanner was already stopped! This would lead to inconsistent scanner behavior.
                if (status == Status.STOPPED) {
                    return;
                }
                scanNow();
                changeStatus( Status.RUNNING );
            }
        }
    }

    public synchronized void scanNow() {
        if (getStatus() == Status.SHUTDOWN ) {
            throw new IllegalStateException("The scanner was already shut down and can no longer be used.");
        }
        // Polling can be started so remember the original state.
        final Status originalStatus = status;
        try {
            changeStatus( Status.SCANNING );
            Map<DependencyDescriptor, Artifact> updatedArtifacts = scanForUpdates();
            if (updatedArtifacts.isEmpty()) {
                changeStatus( originalStatus );
                return;
            }
            changeStatus( Status.UPDATING );

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
            
            // show we catch exceptions here and shutdown the scanner if one happens?
            
        } finally {
            changeStatus( originalStatus );
        }
    }

    private boolean updateKieModule(DependencyDescriptor oldDependency, Artifact artifact) {
        org.appformer.maven.support.AFReleaseId newReleaseId = new DependencyDescriptor( artifact).getReleaseId();
        ZipKieModule kieModule = createZipKieModule(newReleaseId, artifact.getFile());
        if (kieModule != null) {
            addDependencies(kieModule, artifactResolver, artifactResolver.getArtifactDependecies(newReleaseId.toString()));
            ResultsImpl messages = build(kieModule);
            if ( messages.filterMessages(Message.Level.ERROR).isEmpty()) {
                Results updateMessages = kieContainer.updateDependencyToVersion(adapt( oldDependency.getArtifactReleaseId() ), adapt( newReleaseId ));
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
        Map<DependencyDescriptor, Artifact> newArtifacts = new HashMap<DependencyDescriptor, Artifact>();

        Artifact newArtifact = artifactResolver.resolveArtifact(this.kieContainer.getContainerReleaseId());
        if (newArtifact != null) {
            DependencyDescriptor resolvedDep = new DependencyDescriptor(newArtifact);
            if (resolvedDep.isNewerThan(kieProjectDescr)) {
                newArtifacts.put(kieProjectDescr, newArtifact);
                kieProjectDescr = new DependencyDescriptor(newArtifact);
            }
        }

        for (DependencyDescriptor dep : artifactResolver.getAllDependecies()) {
            ReleaseId artifactId = adapt( dep.getReleaseIdWithoutVersion() );
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

    private Map<ReleaseId, DependencyDescriptor> indexArtifacts(ArtifactResolver artifactResolver) {
        Map<ReleaseId, DependencyDescriptor> depsMap = new HashMap<ReleaseId, DependencyDescriptor>();
        for (DependencyDescriptor dep : artifactResolver.getAllDependecies()) {
            if ( !"test".equals(dep.getScope()) && !"provided".equals(dep.getScope()) && !"system".equals(dep.getScope()) ) {
                Artifact artifact = artifactResolver.resolveArtifact(dep.getReleaseId());
                log.debug( artifact + " resolved to  " + artifact.getFile() );
                if (isKJar(artifact.getFile())) {
                    depsMap.put(adapt( dep.getReleaseIdWithoutVersion() ), new DependencyDescriptor(artifact));
                }
            } else {
                log.debug("{} does not need to be resolved because in scope {}", dep, dep.getScope());
            }
        }
        return depsMap;
    }

    private boolean isKJar(File jar) {
        try (ZipFile zipFile = new ZipFile( jar )) {
            ZipEntry zipEntry = zipFile.getEntry( KieModuleModelImpl.KMODULE_JAR_PATH );
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

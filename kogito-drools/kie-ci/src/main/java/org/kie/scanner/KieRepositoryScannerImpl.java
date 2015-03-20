package org.kie.scanner;

import org.drools.compiler.kie.builder.impl.InternalKieContainer;
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
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.scanner.management.KieScannerMBean;
import org.kie.scanner.management.KieScannerMBeanImpl;
import org.kie.scanner.management.MBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.buildKieModule;
import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.setDefaultsforEmptyKieModule;
import static org.kie.scanner.ArtifactResolver.getResolverFor;

public class KieRepositoryScannerImpl implements InternalKieScanner {

    private Timer timer;

    private static final Logger log = LoggerFactory.getLogger(KieScanner.class);

    private InternalKieContainer kieContainer;

    private DependencyDescriptor kieProjectDescr;

    private Map<ReleaseId, DependencyDescriptor> usedDependencies;

    private ArtifactResolver artifactResolver;

    private Status status = Status.STARTING;

    private KieScannerMBean mbean;

    private long pollingInterval;

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
        usedDependencies = indexAtifacts(artifactResolver);

        KieScannersRegistry.register(this);
        status = Status.STOPPED;
        
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
        return loadArtifact(releaseId, null);
    }

    public synchronized KieModule loadArtifact(ReleaseId releaseId, InputStream pomXml) {
        ArtifactResolver resolver = pomXml != null ?
                                    ArtifactResolver.getResolverFor(pomXml) :
                                    getArtifactResolver();
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
        ArtifactResolver resolver = getResolverFor(releaseId, false);
        if (resolver == null) {
            return null;
        }

        MemoryKieModule kieModule = new MemoryKieModule(releaseId);
        addDependencies(kieModule, resolver, resolver.getPomDirectDependencies());
        build(kieModule);
        return kieModule;
    }

    private InternalKieModule buildArtifact(Artifact artifact, ArtifactResolver resolver) {
        DependencyDescriptor dependencyDescriptor = new DependencyDescriptor(artifact);
        ReleaseId releaseId = dependencyDescriptor.getReleaseId();
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
            InternalKieModule dependency = (InternalKieModule) KieServices.Factory.get().getRepository().getKieModule(dep.getReleaseId());
            if (dependency != null) {
                kieModule.addKieDependency(dependency);
            } else {
                Artifact depArtifact = resolver.resolveArtifact(dep.getReleaseId());
                if (depArtifact != null && isKJar(depArtifact.getFile())) {
                    ReleaseId depReleaseId = new DependencyDescriptor(depArtifact).getReleaseId();
                    ZipKieModule zipKieModule = createZipKieModule(depReleaseId, depArtifact.getFile());
                    if (zipKieModule != null) {
                        kieModule.addKieDependency(zipKieModule);
                    }
                }
            }
        }
    }

    private static ZipKieModule createZipKieModule(ReleaseId releaseId, File jar) {
        KieModuleModel kieModuleModel = getKieModuleModelFromJar(jar);
        return kieModuleModel != null ? new ZipKieModule(releaseId, kieModuleModel, jar) : null;
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
        status = Status.STOPPED;
    }

    public synchronized long getPollingInterval() {
        return this.pollingInterval;
    }
    
    public void shutdown() {
        if( getStatus() != Status.SHUTDOWN ) {
            stop(); // making sure it is stopped
            status = Status.SHUTDOWN;
        }
    }

    private void startScanTask(long pollingInterval) {
        status = Status.RUNNING;
        this.pollingInterval = pollingInterval;
        timer = new Timer(true);
        timer.schedule(new ScanTask(), pollingInterval, pollingInterval);
    }

    private class ScanTask extends TimerTask {
        public void run() {
            scanNow();
            status = Status.RUNNING;
        }
    }

    public synchronized void scanNow() {
        if (getStatus() == Status.SHUTDOWN ) {
            throw new IllegalStateException("The scanner was already shut down and can no longer be used.");
        }
        try {
            status = Status.SCANNING;
            Map<DependencyDescriptor, Artifact> updatedArtifacts = scanForUpdates();
            if (updatedArtifacts.isEmpty()) {
                status = Status.STOPPED;
                return;
            }
            status = Status.UPDATING;

            // build the dependencies first
            Map.Entry<DependencyDescriptor, Artifact> containerEntry = null;
            for (Map.Entry<DependencyDescriptor, Artifact> entry : updatedArtifacts.entrySet()) {
                if (entry.getKey().isSameArtifact(kieContainer.getContainerReleaseId())) {
                    containerEntry = entry;
                } else {
                    updateKieModule(entry.getKey(), entry.getValue());
                }
            }
            if (containerEntry != null) {
                updateKieModule(containerEntry.getKey(), containerEntry.getValue());
            }

            log.info("The following artifacts have been updated: " + updatedArtifacts);
            
            // show we catch exceptions here and shutdown the scanner if one happens?
            
        } finally {
            status = Status.STOPPED;
        }
    }

    private void updateKieModule(DependencyDescriptor oldDependency, Artifact artifact) {
        ReleaseId newReleaseId = new DependencyDescriptor(artifact).getReleaseId();
        ZipKieModule kieModule = createZipKieModule(newReleaseId, artifact.getFile());
        if (kieModule != null) {
            addDependencies(kieModule, artifactResolver, artifactResolver.getArtifactDependecies(newReleaseId.toString()));
            ResultsImpl messages = build(kieModule);
            if ( messages.filterMessages(Message.Level.ERROR).isEmpty()) {
                ((InternalKieContainer)kieContainer).updateDependencyToVersion(oldDependency.getArtifactReleaseId(),
                                                                               newReleaseId);
                oldDependency.setArtifactVersion(artifact.getVersion());
            }
        }
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

    private Map<ReleaseId, DependencyDescriptor> indexAtifacts(ArtifactResolver artifactResolver) {
        Map<ReleaseId, DependencyDescriptor> depsMap = new HashMap<ReleaseId, DependencyDescriptor>();
        for (DependencyDescriptor dep : artifactResolver.getAllDependecies()) {
            Artifact artifact = artifactResolver.resolveArtifact(dep.getReleaseId());
            log.debug( artifact + " resolved to  " + artifact.getFile() );
            if (isKJar(artifact.getFile())) {
                depsMap.put(dep.getReleaseIdWithoutVersion(), new DependencyDescriptor(artifact));
            }
        }
        return depsMap;
    }

    private boolean isKJar(File jar) {
        ZipFile zipFile;
        try {
            zipFile = new ZipFile( jar );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ZipEntry zipEntry = zipFile.getEntry( KieModuleModelImpl.KMODULE_JAR_PATH );
        return zipEntry != null;
    }
    
    public synchronized KieScannerMBean getMBean() {
        return this.mbean;
    }
}

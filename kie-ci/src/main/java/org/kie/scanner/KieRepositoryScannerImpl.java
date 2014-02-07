package org.kie.scanner;

import org.drools.compiler.kie.builder.impl.InternalKieContainer;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.Message;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.InternalKieScanner;
import org.drools.compiler.kie.builder.impl.MemoryKieModule;
import org.drools.compiler.kie.builder.impl.ResultsImpl;
import org.drools.compiler.kie.builder.impl.ZipKieModule;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.artifact.Artifact;

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

import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.setDefaultsforEmptyKieModule;
import static org.kie.scanner.ArtifactResolver.getResolverFor;
import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.buildKieModule;
import static org.kie.scanner.DependencyDescriptor.isFixedVersion;

public class KieRepositoryScannerImpl implements InternalKieScanner {

    private Timer timer;

    private static final Logger log = LoggerFactory.getLogger(KieScanner.class);

    private InternalKieContainer kieContainer;

    private final Map<ReleaseId, DependencyDescriptor> usedDependencies = new HashMap<ReleaseId, DependencyDescriptor>();

    private ArtifactResolver artifactResolver;

    private Status status = Status.STARTING;

    public void setKieContainer(KieContainer kieContainer) {
        if (this.kieContainer != null) {
            throw new RuntimeException("Cannot change KieContainer on an already initialized KieScanner");
        }
        this.kieContainer = (InternalKieContainer)kieContainer;
        ReleaseId containerReleaseId = this.kieContainer.getContainerReleaseId();
        if (containerReleaseId == null) {
            throw new RuntimeException("The KieContainer's ReleaseId cannot be null. Are you using a KieClasspathContainer?");
        }

        artifactResolver = getResolverFor(kieContainer.getReleaseId(), true);

        if (!isFixedVersion(containerReleaseId.getVersion())) {
            usedDependencies.put(containerReleaseId,
                                 new DependencyDescriptor(this.kieContainer.getReleaseId(),
                                                          this.kieContainer.getCreationTimestamp()));
        }

        indexAtifacts();
        KieScannersRegistry.register(this);
        status = Status.STOPPED;
    }

    private ArtifactResolver getArtifactResolver() {
        if (artifactResolver == null) {
            artifactResolver = new ArtifactResolver();
        }
        return artifactResolver;
    }

    public KieModule loadArtifact(ReleaseId releaseId) {
        return loadArtifact(releaseId, null);
    }

    public KieModule loadArtifact(ReleaseId releaseId, InputStream pomXml) {
        ArtifactResolver resolver = pomXml != null ?
                                    ArtifactResolver.getResolverFor(pomXml) :
                                    getArtifactResolver();
        Artifact artifact = resolver.resolveArtifact(releaseId);
        return artifact != null ? buildArtifact(artifact, resolver) : loadPomArtifact(releaseId);
    }

    public String getArtifactVersion(ReleaseId releaseId) {
        if (!releaseId.isSnapshot()) {
            return releaseId.getVersion();
        }
        Artifact artifact = getArtifactResolver().resolveArtifact(releaseId);
        return artifact != null ? artifact.getVersion() : null;
    }

    public ReleaseId getScannerReleaseId() {
        return ((InternalKieContainer)kieContainer).getContainerReleaseId();
    }

    public ReleaseId getCurrentReleaseId() {
        return kieContainer.getReleaseId();
    }

    public Status getStatus() {
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
            Artifact depArtifact = resolver.resolveArtifact(dep.getReleaseId());
            if (isKJar(depArtifact.getFile())) {
                ReleaseId depReleaseId = new DependencyDescriptor(depArtifact).getReleaseId();
                ZipKieModule zipKieModule = createZipKieModule(depReleaseId, depArtifact.getFile());
                if (zipKieModule != null) {
                    kieModule.addKieDependency(zipKieModule);
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

    public void start(long pollingInterval) {
        if (pollingInterval <= 0) {
            throw new IllegalArgumentException("pollingInterval must be positive");
        }
        if (timer != null) {
            throw new IllegalStateException("The scanner is already running");
        }
        startScanTask(pollingInterval);
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        status = Status.STOPPED;
    }

    private void startScanTask(long pollingInterval) {
        status = Status.RUNNING;
        timer = new Timer(true);
        timer.schedule(new ScanTask(), pollingInterval, pollingInterval);
    }

    private class ScanTask extends TimerTask {
        public void run() {
            scanNow();
            status = Status.RUNNING;
        }
    }

    public void scanNow() {
        status = Status.SCANNING;
        Map<DependencyDescriptor, Artifact> updatedArtifacts = scanForUpdates();
        if (updatedArtifacts.isEmpty()) {
            status = Status.STOPPED;
            return;
        }
        status = Status.UPDATING;
        for (Map.Entry<DependencyDescriptor, Artifact> entry : updatedArtifacts.entrySet()) {
            updateKieModule(entry.getKey(), entry.getValue());
        }
        log.info("The following artifacts have been updated: " + updatedArtifacts);
        status = Status.STOPPED;
    }

    private void updateKieModule(DependencyDescriptor oldDependency, Artifact artifact) {
        ReleaseId newReleaseId = new DependencyDescriptor(artifact).getReleaseId();
        ZipKieModule kieModule = createZipKieModule(newReleaseId, artifact.getFile());
        if (kieModule != null) {
            ResultsImpl messages = build(kieModule);
            if ( messages.filterMessages(Message.Level.ERROR).isEmpty()) {
                ((InternalKieContainer)kieContainer).updateDependencyToVersion(oldDependency.getArtifactReleaseId(),
                                                                               newReleaseId);
                oldDependency.setArtifactVersion(artifact.getVersion());
            }
        }
    }

    private Map<DependencyDescriptor, Artifact> scanForUpdates() {
        Map<ReleaseId, DependencyDescriptor> replacedArtifacts = new HashMap<ReleaseId, DependencyDescriptor>();
        Map<DependencyDescriptor, Artifact> newArtifacts = new HashMap<DependencyDescriptor, Artifact>();
        ArtifactResolver artifactResolver = getArtifactResolver();

        for (Map.Entry<ReleaseId, DependencyDescriptor> entry : usedDependencies.entrySet()) {
            Artifact newArtifact = artifactResolver.resolveArtifact(entry.getKey());
            if (newArtifact == null) {
                continue;
            }
            DependencyDescriptor resolvedDep = new DependencyDescriptor(newArtifact);
            if (resolvedDep.isNewerThan(entry.getValue())) {
                newArtifacts.put(entry.getValue(), newArtifact);
                replacedArtifacts.put(entry.getKey(), resolvedDep);
            }
        }

        for (Map.Entry<ReleaseId, DependencyDescriptor> entry : replacedArtifacts.entrySet()) {
            usedDependencies.put(entry.getKey(), entry.getValue());
        }

        return newArtifacts;
    }

    private void indexAtifacts() {
        Collection<DependencyDescriptor> deps = getArtifactResolver().getAllDependecies();
        for (DependencyDescriptor dep : deps) {
            if (!dep.isFixedVersion()) {
                Artifact artifact = getArtifactResolver().resolveArtifact(dep.getReleaseId());
                log.debug( artifact + " resolved to  " + artifact.getFile() );
                if (isKJar(artifact.getFile())) {
                    usedDependencies.put(dep.getReleaseId(), new DependencyDescriptor(artifact));
                }
            }
        }
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
}

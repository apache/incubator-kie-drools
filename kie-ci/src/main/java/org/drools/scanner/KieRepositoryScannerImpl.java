package org.drools.scanner;

import org.drools.kproject.models.KieModuleModelImpl;
import org.kie.builder.GAV;
import org.kie.builder.KieContainer;
import org.kie.builder.KieModule;
import org.kie.builder.KieScanner;
import org.kie.builder.Message;
import org.kie.builder.impl.InternalKieModule;
import org.kie.builder.impl.InternalKieScanner;
import org.kie.builder.impl.MemoryKieModule;
import org.kie.builder.impl.ResultsImpl;
import org.kie.builder.impl.ZipKieModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.artifact.Artifact;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.drools.scanner.ArtifactResolver.getResolverFor;
import static org.kie.builder.impl.KieBuilderImpl.buildKieModule;

public class KieRepositoryScannerImpl implements InternalKieScanner {

    private Timer timer;

    private static final Logger log = LoggerFactory.getLogger(KieScanner.class);

    private KieContainer kieContainer;

    private final Set<DependencyDescriptor> usedDependencies = new HashSet<DependencyDescriptor>();

    private ArtifactResolver artifactResolver;

    public void setKieContainer(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
        DependencyDescriptor projectDescr = new DependencyDescriptor(kieContainer.getGAV());
        if (!projectDescr.isFixedVersion()) {
            usedDependencies.add(projectDescr);
        }

        artifactResolver = getResolverFor(kieContainer.getGAV(), true);
        init();
    }

    private ArtifactResolver getArtifactResolver() {
        if (artifactResolver == null) {
            artifactResolver = new ArtifactResolver();
        }
        return artifactResolver;
    }

    private void init() {
        Collection<Artifact> artifacts = findKJarAtifacts();
        log.info("Artifacts containing a kjar: " + artifacts);
        if (artifacts.isEmpty()) {
            log.info("There's no artifacts containing a kjar: shutdown the scanner");
            return;
        }
        indexAtifacts(artifacts);
    }

    public KieModule loadArtifact(GAV gav) {
        String artifactName = gav.toString();
        Artifact artifact = getArtifactResolver().resolveArtifact(artifactName);
        return artifact != null ? buildArtifact(gav, artifact) : loadPomArtifact(gav);
    }

    private KieModule loadPomArtifact(GAV gav) {
        ArtifactResolver resolver = getResolverFor(gav, false);
        if (resolver == null) {
            return null;
        }

        MemoryKieModule kieModule = new MemoryKieModule(gav);
        addDependencies(kieModule, resolver, resolver.getPomDirectDependencies());
        build(kieModule);
        return kieModule;
    }

    private InternalKieModule buildArtifact(GAV gav, Artifact artifact) {
        ArtifactResolver resolver = getArtifactResolver();
        ZipKieModule kieModule = new ZipKieModule(gav, artifact.getFile());
        addDependencies(kieModule, resolver, resolver.getArtifactDependecies(new DependencyDescriptor(artifact).toString()));
        build(kieModule);
        return kieModule;
    }
    
    private void addDependencies(InternalKieModule kieModule, ArtifactResolver resolver, List<DependencyDescriptor> dependencies) {
        for (DependencyDescriptor dep : dependencies) {
            Artifact depArtifact = resolver.resolveArtifact(dep.toString());
            if (isKJar(depArtifact.getFile())) {
                GAV depGav = new DependencyDescriptor(depArtifact).getGav();
                kieModule.addDependency(new ZipKieModule(depGav, depArtifact.getFile()));
            }
        }
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
    }

    private void startScanTask(long pollingInterval) {
        timer = new Timer(true);
        timer.schedule(new ScanTask(), pollingInterval, pollingInterval);
    }

    private class ScanTask extends TimerTask {
        public void run() {
            scanNow();
        }
    }

    public void scanNow() {
        Collection<Artifact> updatedArtifacts = scanForUpdates(usedDependencies);
        if (updatedArtifacts.isEmpty()) {
            return;
        }
        for (Artifact artifact : updatedArtifacts) {
            DependencyDescriptor depDescr = new DependencyDescriptor(artifact);
            usedDependencies.remove(depDescr);
            usedDependencies.add(depDescr);
            updateKieModule(artifact, depDescr.getGav());
        }
        log.info("The following artifacts have been updated: " + updatedArtifacts);
    }

    private void updateKieModule(Artifact artifact, GAV gav) {
        ZipKieModule kieModule = new ZipKieModule(gav, artifact.getFile());
        ResultsImpl messages = build(kieModule);
        if ( messages.filterMessages(Message.Level.ERROR).isEmpty()) {
            kieContainer.updateToVersion(gav);
        }
    }

    private Collection<Artifact> scanForUpdates(Collection<DependencyDescriptor> dependencies) {
        List<Artifact> newArtifacts = new ArrayList<Artifact>();
        for (DependencyDescriptor dependency : dependencies) {
            Artifact newArtifact = getArtifactResolver().resolveArtifact(dependency.toResolvableString());
            if (newArtifact == null) {
                continue;
            }
            DependencyDescriptor resolvedDep = new DependencyDescriptor(newArtifact);
            if (resolvedDep.isNewerThan(dependency)) {
                newArtifacts.add(newArtifact);
            }
        }
        return newArtifacts;
    }

    private void indexAtifacts(Collection<Artifact> artifacts) {
        for (Artifact artifact : artifacts) {
            usedDependencies.add(new DependencyDescriptor(artifact));
        }
    }

    private Collection<Artifact> findKJarAtifacts() {
        Collection<DependencyDescriptor> deps = getArtifactResolver().getAllDependecies();
        deps = filterNonFixedDependecies(deps);
        Collection<Artifact> artifacts = resolveArtifacts(deps);
        return filterKJarArtifacts(artifacts);
    }

    private Collection<DependencyDescriptor> filterNonFixedDependecies(Collection<DependencyDescriptor> dependencies) {
        List<DependencyDescriptor> nonFixedDeps = new ArrayList<DependencyDescriptor>();
        for (DependencyDescriptor dep : dependencies) {
            if (!dep.isFixedVersion()) {
                nonFixedDeps.add(dep);
            }
        }
        return nonFixedDeps;
    }

    private List<Artifact> resolveArtifacts(Collection<DependencyDescriptor> dependencies) {
        List<Artifact> artifacts = new ArrayList<Artifact>();
        for (DependencyDescriptor dep : dependencies) {
            Artifact artifact = getArtifactResolver().resolveArtifact(dep.toString());
            artifacts.add(artifact);
            log.debug( artifact + " resolved to  " + artifact.getFile() );
        }
        return artifacts;
    }

    private Collection<Artifact> filterKJarArtifacts(Collection<Artifact> artifacts) {
        List<Artifact> kJarArtifacts = new ArrayList<Artifact>();
        for (Artifact artifact : artifacts) {
            if (isKJar(artifact.getFile())) {
                kJarArtifacts.add(artifact);
            }
        }
        return kJarArtifacts;
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

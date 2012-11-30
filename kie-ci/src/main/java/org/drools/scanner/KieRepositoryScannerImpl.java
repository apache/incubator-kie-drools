package org.drools.scanner;

import org.drools.scanner.embedder.EmbeddedPomParser;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieContainer;
import org.kie.builder.KieScanner;
import org.kie.builder.KieServices;
import org.kie.builder.impl.InternalKieContainer;
import org.kie.builder.impl.InternalKieScanner;
import org.kie.builder.impl.KieFileSystemImpl;
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

import static org.drools.kproject.memory.MemoryFileSystem.readFromJar;

public class KieRepositoryScannerImpl implements InternalKieScanner {

    private long pollingInterval;

    private Timer timer;

    private static final Logger log = LoggerFactory.getLogger(KieScanner.class);

    private KieContainer kieContainer;

    private final Set<DependencyDescriptor> usedDependencies = new HashSet<DependencyDescriptor>();

    private PomParser pomParser = new EmbeddedPomParser();

    public void setKieContainer(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
        DependencyDescriptor projectDescr = new DependencyDescriptor(kieContainer.getGAV());
        if (!projectDescr.isFixedVersion()) {
            usedDependencies.add(projectDescr);
        }
    }

    public void start(long pollingInterval) {
        if (timer != null) {
            throw new IllegalStateException("The scanner is already running");
        }
        this.pollingInterval = pollingInterval;
        if (init() && pollingInterval > 0) {
            startScanTask();
        }
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private boolean init() {
        Collection<Artifact> artifacts = findKJarAtifacts();
        log.info("Artifacts containing a kjar: " + artifacts);
        if (artifacts.isEmpty()) {
            log.info("There's no artifacts containing a kjar: shutdown the scanner");
            return false;
        }
        indexAtifacts(artifacts);
        return !usedDependencies.isEmpty();
    }

    private void startScanTask() {
        timer = new Timer(true);
        timer.schedule(new ScanTask(), pollingInterval, pollingInterval);
    }

    private class ScanTask extends TimerTask {
        public void run() {
            scanNow();
        }
    }

    public void scanNow() {
        if (usedDependencies == null) {
            return;
        }
        Collection<Artifact> updatedArtifacts = scanForUpdates(usedDependencies);
        if (updatedArtifacts.isEmpty()) {
            return;
        }
        for (Artifact artifact : updatedArtifacts) {
            File kJar = artifact.getFile();
            DependencyDescriptor depDescr = new DependencyDescriptor(artifact);
            usedDependencies.remove(depDescr);
            usedDependencies.add(depDescr);
            if (kieContainer.getGAV().equals(depDescr.getGav())) {
                updateKieJar(kJar);
            }
        }
        log.info("The following artifacts have been updated: " + updatedArtifacts);
    }

    private void updateKieJar(File kJar) {
        KieBuilder kieBuilder = KieServices.Factory.get().newKieBuilder(new KieFileSystemImpl(readFromJar(kJar)));
        kieBuilder.build();
        ((InternalKieContainer)kieContainer).updateKieJar(kieBuilder.getKieJar());
    }

    private Collection<Artifact> scanForUpdates(Collection<DependencyDescriptor> dependencies) {
        List<Artifact> newArtifacts = new ArrayList<Artifact>();
        MavenRepository repository = new MavenRepository();
        for (DependencyDescriptor dependency : dependencies) {
            Artifact newArtifact = repository.resolveArtifact(dependency.toResolvableString());
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
        Collection<DependencyDescriptor> deps = getAllDependecies();
        deps = filterNonFixedDependecies(deps);
        Collection<Artifact> artifacts = resolveArtifacts(deps);
        return filterKJarArtifacts(artifacts);
    }

    private Collection<DependencyDescriptor> getAllDependecies() {
        MavenRepository repository = new MavenRepository();
        Set<DependencyDescriptor> dependencies = new HashSet<DependencyDescriptor>();
        for (DependencyDescriptor dep : pomParser.getPomDirectDependencies()) {
            dependencies.addAll(repository.getArtifactDependecies(dep.toString()));
        }
        return dependencies;
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
        MavenRepository repository = new MavenRepository();
        List<Artifact> artifacts = new ArrayList<Artifact>();
        for (DependencyDescriptor dep : dependencies) {
            Artifact artifact = repository.resolveArtifact(dep.toString());
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

    public static final String KPROJECT_JAR_PATH = "META-INF/kproject.xml";

    private boolean isKJar(File jar) {
        ZipFile zipFile;
        try {
            zipFile = new ZipFile( jar );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ZipEntry zipEntry = zipFile.getEntry( KPROJECT_JAR_PATH );
        return zipEntry != null;
    }
}

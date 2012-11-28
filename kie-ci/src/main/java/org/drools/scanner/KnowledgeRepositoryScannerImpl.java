package org.drools.scanner;

import org.drools.scanner.embedder.EmbeddedPomParser;
import org.kie.builder.KnowledgeContainer;
import org.kie.builder.KnowledgeRepositoryScanner;
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

public class KnowledgeRepositoryScannerImpl implements KnowledgeRepositoryScanner {

    private static final long DEFAULT_POLLING_INTERVAL = 60 * 1000L; // 1 minute
    private final long pollingInterval;

    private static final Logger log = LoggerFactory.getLogger(KnowledgeRepositoryScanner.class);

    private final KnowledgeContainer knowledgeContainer;

    private Set<DependencyDescriptor> usedDependencies;

    private PomParser pomParser = new EmbeddedPomParser();

    public KnowledgeRepositoryScannerImpl(KnowledgeContainer knowledgeContainer) {
        this(knowledgeContainer, DEFAULT_POLLING_INTERVAL);
    }

    public KnowledgeRepositoryScannerImpl(KnowledgeContainer knowledgeContainer, long pollingInterval) {
        this.knowledgeContainer = knowledgeContainer;
        this.pollingInterval = pollingInterval;
        start();
    }

    private void start() {
        if (init() && pollingInterval > 0) {
            startScanTask();
        }
    }

    private boolean init() {
        Collection<Artifact> artifacts = findKJarAtifacts();
        log.info("Artifacts containing a kjar: " + artifacts);
        if (artifacts.isEmpty()) {
            log.info("There's no artifacts containing a kjar: shutdown the scanner");
            return false;
        }
        usedDependencies = indexAtifacts(artifacts);
        return true;
    }

    private void startScanTask() {
        new Timer(true).schedule(new ScanTask(), pollingInterval, pollingInterval);
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
        File[] kJars = new File[updatedArtifacts.size()];
        int i = 0;
        for (Artifact artifact : updatedArtifacts) {
            kJars[i++] = artifact.getFile();
            DependencyDescriptor depDescr = new DependencyDescriptor(artifact);
            usedDependencies.remove(depDescr);
            usedDependencies.add(depDescr);
        }
        knowledgeContainer.deploy(kJars);
        log.info("The following artifacts have been updated: " + updatedArtifacts);
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

    private Set<DependencyDescriptor> indexAtifacts(Collection<Artifact> artifacts) {
        Set<DependencyDescriptor> deps = new HashSet<DependencyDescriptor>();
        for (Artifact artifact : artifacts) {
            deps.add(new DependencyDescriptor(artifact));
        }
        return deps;
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

    // Only for testing purpose
    void setPomParser(PomParser pomParser) {
        this.pomParser = pomParser;
        start();
    }
}

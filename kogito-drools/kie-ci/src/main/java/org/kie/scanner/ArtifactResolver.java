package org.kie.scanner;

import org.apache.maven.project.MavenProject;
import org.drools.compiler.kie.builder.impl.InternalKieContainer;
import org.drools.compiler.kproject.xml.MinimalPomParser;
import org.drools.compiler.kproject.xml.PomModel;
import org.kie.api.builder.KieScanner;
import org.kie.scanner.embedder.EmbeddedPomParser;
import org.kie.api.builder.ReleaseId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.aether.artifact.Artifact;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.kie.scanner.embedder.MavenProjectLoader.parseMavenPom;

class ArtifactResolver {

    private static final Logger log = LoggerFactory.getLogger(KieScanner.class);

    private final PomParser pomParser;

    private final MavenRepository mavenRepository;

    ArtifactResolver() {
        mavenRepository = MavenRepository.getMavenRepository();
        pomParser = new EmbeddedPomParser();
    }

    private ArtifactResolver(MavenProject mavenProject) {
        mavenRepository = MavenRepository.getMavenRepository(mavenProject);
        pomParser = new EmbeddedPomParser(mavenProject);
    }

    private ArtifactResolver(PomParser pomParser) {
        mavenRepository = MavenRepository.getMavenRepository();
        this.pomParser = pomParser;
    }

    Artifact resolveArtifact(ReleaseId releaseId) {
        return mavenRepository.resolveArtifact(releaseId);
    }

    List<DependencyDescriptor> getArtifactDependecies(String artifactName) {
        return mavenRepository.getArtifactDependecies(artifactName);
    }

    List<DependencyDescriptor> getPomDirectDependencies() {
        return pomParser.getPomDirectDependencies();
    }

    Collection<DependencyDescriptor> getAllDependecies() {
        Set<DependencyDescriptor> dependencies = new HashSet<DependencyDescriptor>();
        for (DependencyDescriptor dep : getPomDirectDependencies()) {
            dependencies.add(dep);
            dependencies.addAll(getArtifactDependecies(dep.toString()));
        }
        return dependencies;
    }

    public static ArtifactResolver getResolverFor(InternalKieContainer kieContainer, boolean allowDefaultPom) {
        InputStream pomStream = kieContainer.getPomAsStream();
        if (pomStream != null) {
            ArtifactResolver artifactResolver = getResolverFor(pomStream);
            if (artifactResolver != null) {
                return artifactResolver;
            }
        }
        return getResolverFor(kieContainer.getReleaseId(), allowDefaultPom);
    }

    public static ArtifactResolver getResolverFor(ReleaseId releaseId, boolean allowDefaultPom) {
        File pomFile = getPomFileForGAV(releaseId, allowDefaultPom);
        if (pomFile != null) {
            ArtifactResolver artifactResolver = getResolverFor(pomFile);
            if (artifactResolver != null) {
                return artifactResolver;
            }
        }
        return allowDefaultPom ? new ArtifactResolver() : null;
    }

    public static ArtifactResolver getResolverFor(URI uri) {
        return getResolverFor(new File(uri));
    }

    public static ArtifactResolver getResolverFor(File pomFile) {
        try {
            return new ArtifactResolver(parseMavenPom(pomFile));
        } catch (RuntimeException e) {
            log.warn("Cannot use native maven pom parser, fall back to the internal one", e);
            PomParser pomParser = createInternalPomParser(pomFile);
            if (pomParser != null) {
                return new ArtifactResolver(pomParser);
            }
        }
        return null;
    }

    public static ArtifactResolver getResolverFor(InputStream pomStream) {
        MavenProject mavenProject = parseMavenPom(pomStream);
        return new ArtifactResolver(mavenProject);
    }

    private static File getPomFileForGAV(ReleaseId releaseId, boolean allowDefaultPom) {
        String artifactName = releaseId.getGroupId() + ":" + releaseId.getArtifactId() + ":pom:" + releaseId.getVersion();
        Artifact artifact = MavenRepository.getMavenRepository().resolveArtifact(artifactName, !allowDefaultPom);
        return artifact != null ? artifact.getFile() : null;
    }

    private static InternalPomParser createInternalPomParser(File pomFile) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(pomFile);
            return new InternalPomParser(MinimalPomParser.parse(pomFile.getAbsolutePath(), fis));
        } catch (FileNotFoundException e) {
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) { }
            }
        }
        return null;
    }

    private static class InternalPomParser implements PomParser {
        private final PomModel pomModel;

        private InternalPomParser(PomModel pomModel) {
            this.pomModel = pomModel;
        }

        @Override
        public List<DependencyDescriptor> getPomDirectDependencies() {
            List<DependencyDescriptor> deps = new ArrayList<DependencyDescriptor>();
            for (ReleaseId rId : pomModel.getDependencies()) {
                deps.add(new DependencyDescriptor(rId));
            }
            return deps;
        }
    }

    public void renewSession() {
        mavenRepository.renewSession();
    }
}

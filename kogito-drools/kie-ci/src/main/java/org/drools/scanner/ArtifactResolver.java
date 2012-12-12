package org.drools.scanner;

import org.apache.maven.project.MavenProject;
import org.drools.scanner.embedder.EmbeddedPomParser;
import org.kie.builder.ReleaseId;
import org.sonatype.aether.artifact.Artifact;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.drools.scanner.embedder.MavenProjectLoader.parseMavenPom;

class ArtifactResolver {

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

    Artifact resolveArtifact(String artifactName) {
        return mavenRepository.resolveArtifact(artifactName);
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

    public static ArtifactResolver getResolverFor(ReleaseId releaseId, boolean allowDefaultPom) {
        MavenProject mavenProject = getMavenProjectForGAV(releaseId);
        return mavenProject == null ?
                (allowDefaultPom ? new ArtifactResolver() : null) :
                new ArtifactResolver(mavenProject);
    }

    public static ArtifactResolver getResolverFor(URI uri) {
        return getResolverFor(new File(uri));
    }

    public static ArtifactResolver getResolverFor(File pomFile) {
        MavenProject mavenProject = parseMavenPom(pomFile);
        return new ArtifactResolver(mavenProject);
    }

    static MavenProject getMavenProjectForGAV(ReleaseId releaseId) {
        String artifactName = releaseId.getGroupId() + ":" + releaseId.getArtifactId() + ":pom:" + releaseId.getVersion();
        Artifact artifact = MavenRepository.getMavenRepository().resolveArtifact(artifactName);
        return artifact != null ? parseMavenPom(artifact.getFile()) : null;
    }
}

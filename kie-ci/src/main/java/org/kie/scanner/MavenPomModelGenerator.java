package org.kie.scanner;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.kproject.xml.PomModel;
import org.drools.compiler.kproject.xml.PomModelGenerator;
import org.kie.api.builder.ReleaseId;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.kie.scanner.embedder.MavenProjectLoader.parseMavenPom;

public class MavenPomModelGenerator implements PomModelGenerator {

    @Override
    public PomModel parse(String path, InputStream pomStream) {
        return new MavenModel(parseMavenPom(pomStream));
    }

    public static class MavenModel implements PomModel {

        private final MavenProject mavenProject;
        private final ReleaseId releaseId;
        private final ReleaseId parentReleaseId;
        private final Set<ReleaseId> dependencies;

        public MavenModel( MavenProject mavenProject ) {
            this.mavenProject = mavenProject;
            this.releaseId = initReleaseId( mavenProject );
            this.parentReleaseId = initParentReleaseId( mavenProject );
            this.dependencies = initDependencies( mavenProject );
        }

        public MavenProject getMavenProject() {
            return mavenProject;
        }

        @Override
        public ReleaseId getReleaseId() {
            return releaseId;
        }

        private ReleaseId initReleaseId(MavenProject mavenProject) {
            return new ReleaseIdImpl(mavenProject.getGroupId(),
                                     mavenProject.getArtifactId(),
                                     mavenProject.getVersion());
        }

        @Override
        public ReleaseId getParentReleaseId() {
            return parentReleaseId;
        }

        private ReleaseId initParentReleaseId(MavenProject mavenProject) {
            try {
                MavenProject parentProject = mavenProject.getParent();
                if (parentProject != null) {
                    return new ReleaseIdImpl(parentProject.getGroupId(),
                                             parentProject.getArtifactId(),
                                             parentProject.getVersion());
                }
            } catch (Exception e) {
                // ignore
            }
            return null;
        }

        @Override
        public Collection<ReleaseId> getDependencies() {
            return dependencies;
        }

        private Set<ReleaseId> initDependencies(MavenProject mavenProject) {
            Set<ReleaseId> dependencies = new HashSet<ReleaseId>();
            // use getArtifacts instead of getDependencies to load transitive dependencies as well
            for (Artifact dep : mavenProject.getArtifacts()) {
                String scope = dep.getScope();
                if ("provided".equals(scope) || "test".equals( scope ) ) {
                    continue;
                }
                dependencies.add( new ReleaseIdImpl( dep.getGroupId(),
                                                     dep.getArtifactId(),
                                                   dep.getVersion()));
            }
            return dependencies;
        }
    }
}

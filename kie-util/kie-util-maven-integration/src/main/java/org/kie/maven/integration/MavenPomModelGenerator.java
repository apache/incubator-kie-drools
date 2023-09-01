package org.kie.maven.integration;

import java.io.InputStream;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.kie.api.builder.ReleaseId;
import org.kie.util.maven.support.PomModel;
import org.kie.util.maven.support.PomModelGenerator;
import org.kie.util.maven.support.ReleaseIdImpl;

import static org.kie.maven.integration.embedder.MavenProjectLoader.parseMavenPom;

public class MavenPomModelGenerator implements PomModelGenerator {

    @Override
    public PomModel parse(String path, InputStream pomStream ) {
        return new MavenModel(parseMavenPom(pomStream));
    }

    public static class MavenModel extends PomModel.InternalModel {

        private final MavenProject mavenProject;

        public MavenModel( MavenProject mavenProject ) {
            this.mavenProject = mavenProject;
            setReleaseId( initReleaseId( mavenProject ) );
            setParentReleaseId( initParentReleaseId( mavenProject ) );
            initDependencies( mavenProject );
        }

        public MavenProject getMavenProject() {
            return mavenProject;
        }

        private ReleaseId initReleaseId(MavenProject mavenProject ) {
            return new ReleaseIdImpl(mavenProject.getGroupId(),
                                       mavenProject.getArtifactId(),
                                       mavenProject.getVersion());
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

        private void initDependencies(MavenProject mavenProject) {
            // use getArtifacts instead of getDependencies to load transitive dependencies as well
            for (Artifact dep : mavenProject.getArtifacts()) {
                addDependency(new ReleaseIdImpl(dep.getGroupId(), dep.getArtifactId(), dep.getVersion(), dep.getType() ), dep.getScope());
            }
        }
    }
}

package org.kie.scanner;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.kproject.xml.PomModel;
import org.drools.compiler.kproject.xml.PomModelGenerator;

import java.io.InputStream;

import static org.kie.scanner.embedder.MavenProjectLoader.parseMavenPom;

public class MavenPomModelGenerator implements PomModelGenerator {

    @Override
    public PomModel parse(String path, InputStream pomStream) {
        PomModel pomModel = new PomModel();
        MavenProject mavenProject = parseMavenPom(pomStream);

        pomModel.setReleaseId(new ReleaseIdImpl(mavenProject.getGroupId(),
                                                mavenProject.getArtifactId(),
                                                mavenProject.getVersion()));
        try {
            MavenProject parentProject = mavenProject.getParent();
            if (parentProject != null) {
                pomModel.setParentReleaseId(new ReleaseIdImpl(parentProject.getGroupId(),
                                                              parentProject.getArtifactId(),
                                                              parentProject.getVersion()));
            }
        } catch (Exception e) {
            // ignore
        }

        // use getArtifacts instead of getDependencies to load transitive dependencies as well
        for (Artifact dep : mavenProject.getArtifacts()) {
            String scope = dep.getScope();
            if ("provided".equals(scope) || "test".equals(scope)) {
                continue;
            }
            pomModel.addDependency(new ReleaseIdImpl(dep.getGroupId(),
                    dep.getArtifactId(),
                    dep.getVersion()));
        }


        return pomModel;
    }
}

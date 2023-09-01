package org.kie.maven.integration.embedder;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.kie.maven.integration.DependencyDescriptor;
import org.kie.maven.integration.PomParser;
import org.kie.util.maven.support.DependencyFilter;

public class EmbeddedPomParser implements PomParser {

    private final MavenProject mavenProject;

    public EmbeddedPomParser() {
        this( MavenProjectLoader.loadMavenProject() );
    }

    public EmbeddedPomParser(MavenProject mavenProject) {
        this.mavenProject = mavenProject;
    }

    public List<DependencyDescriptor> getPomDirectDependencies(DependencyFilter filter ) {
        List<DependencyDescriptor> deps = new ArrayList<>();
        for (Dependency dep : mavenProject.getDependencies()) {
            DependencyDescriptor depDescr = new DependencyDescriptor(dep);
            if (depDescr.isValid() && filter.accept(depDescr.getReleaseId(), depDescr.getScope())) {
                deps.add(depDescr);
            }
        }
        return deps;
    }
}

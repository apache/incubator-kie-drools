package org.drools.scanner.embedder;

import org.apache.maven.model.Dependency;
import org.drools.scanner.DependencyDescriptor;
import org.drools.scanner.PomParser;

import java.util.ArrayList;
import java.util.List;

import static org.drools.scanner.embedder.MavenProjectLoader.loadMavenProject;

public class EmbeddedPomParser implements PomParser {
    
    public List<DependencyDescriptor> getPomDirectDependencies() {
        List<DependencyDescriptor> deps = new ArrayList<DependencyDescriptor>();
        for (Dependency dep : loadMavenProject().getDependencies()) {
            DependencyDescriptor depDescr = new DependencyDescriptor(dep);
            if (depDescr.isValid()) {
                deps.add(depDescr);
            }
        }
        return deps;
    }
}

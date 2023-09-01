package org.kie.maven.integration;

import java.util.List;

import org.kie.util.maven.support.DependencyFilter;


public interface PomParser {
    List<DependencyDescriptor> getPomDirectDependencies( DependencyFilter filter );
}

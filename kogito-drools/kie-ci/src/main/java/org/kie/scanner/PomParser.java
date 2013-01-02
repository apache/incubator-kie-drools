package org.kie.scanner;

import java.util.List;

public interface PomParser {
    List<DependencyDescriptor> getPomDirectDependencies();
}

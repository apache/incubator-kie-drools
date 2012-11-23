package org.drools.scanner;

import java.util.List;

public interface PomParser {
    List<DependencyDescriptor> getPomDirectDependencies();
}

package org.drools.wiring.api;

import java.io.IOException;
import java.util.Map;

import org.drools.wiring.api.classloader.ProjectClassLoader;
import org.drools.wiring.api.util.ByteArrayClassLoader;
import org.kie.api.internal.utils.KieService;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.internal.builder.KnowledgeBuilder;

public interface ComponentsSupplier extends KieService {
    ProjectClassLoader createProjectClassLoader(ClassLoader parent, ResourceProvider resourceProvider );

    ByteArrayClassLoader createByteArrayClassLoader(ClassLoader parent );

    default ClassLoader createPackageClassLoader(Map<String, byte[]> store, ClassLoader rootClassLoader) {
        return rootClassLoader;
    }

    Object createConsequenceExceptionHandler(String className, ClassLoader classLoader);

    default void addPackageFromXSD(KnowledgeBuilder kBuilder, Resource resource, ResourceConfiguration configuration) throws IOException { }
}

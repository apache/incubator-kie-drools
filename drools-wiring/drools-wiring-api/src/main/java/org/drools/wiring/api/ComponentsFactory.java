package org.drools.wiring.api;

import java.io.IOException;
import java.util.Map;

import org.drools.wiring.api.classloader.ProjectClassLoader;
import org.drools.wiring.api.util.ByteArrayClassLoader;
import org.kie.api.internal.utils.KieService;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.internal.builder.KnowledgeBuilder;

public class ComponentsFactory {

    private static ComponentsSupplier supplier;

    public static ProjectClassLoader createProjectClassLoader(ClassLoader parent, ResourceProvider resourceProvider ) {
        return getComponentsSupplier().createProjectClassLoader(parent, resourceProvider);
    }

    public static ByteArrayClassLoader createByteArrayClassLoader(ClassLoader parent ) {
        return getComponentsSupplier().createByteArrayClassLoader(parent);
    }

    public static ClassLoader createPackageClassLoader(Map<String, byte[]> store, ClassLoader rootClassLoader) {
        return getComponentsSupplier().createPackageClassLoader(store, rootClassLoader);
    }

    public static Object createConsequenceExceptionHandler(String className, ClassLoader classLoader) {
        return getComponentsSupplier().createConsequenceExceptionHandler(className, classLoader);
    }

    public static void addPackageFromXSD( KnowledgeBuilder kBuilder, Resource resource, ResourceConfiguration configuration) throws IOException {
        getComponentsSupplier().addPackageFromXSD(kBuilder, resource, configuration);
    }

    public static void setComponentsSupplier( ComponentsSupplier supplier ) {
        ComponentsFactory.supplier = supplier;
    }

    private static ComponentsSupplier getComponentsSupplier() {
        if (supplier == null) {
            ComponentsFactory.supplier = Holder.supplier;
        }
        return ComponentsFactory.supplier;
    }

    private static class Holder {
        private static ComponentsSupplier supplier = KieService.load(ComponentsSupplier.class);
    }
}

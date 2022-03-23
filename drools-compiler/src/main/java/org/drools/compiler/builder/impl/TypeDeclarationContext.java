package org.drools.compiler.builder.impl;

import org.drools.compiler.builder.PackageRegistryManager;
import org.kie.api.io.Resource;
import org.kie.internal.builder.ResourceChange;

public interface TypeDeclarationContext extends
        RootClassLoaderProvider,
        BuildResultAccumulator,
        BuilderConfigurationProvider,
        PackageRegistryManager,
        InternalKnowledgeBaseProvider {

    TypeDeclarationBuilder getTypeBuilder();

    Resource getCurrentResource();

    boolean filterAccepts(ResourceChange.Type declaration, String namespace, String typeName);
}

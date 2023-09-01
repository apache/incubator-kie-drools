package org.drools.compiler.builder.impl;

import org.drools.compiler.builder.PackageRegistryManager;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.internal.builder.ResourceChange;

import java.util.List;

/**
 * The build context for {@link TypeDeclarationBuilder}, {@link ClassDefinitionFactory} and
 * all their related siblings.
 * <p>
 * This is a facade that exposes only part of the {@link KnowledgeBuilderImpl} API surface
 */
public interface TypeDeclarationContext extends
        RootClassLoaderProvider,
        BuilderConfigurationProvider,
        PackageRegistryManager,
        InternalKnowledgeBaseProvider,
        GlobalVariableContext,
        TypeDeclarationManager {

    // these methods are necessary to complete the facade
    // but they should be refactored and cleaned up (possibly removed)
    // https://issues.redhat.com/browse/DROOLS-6884

    TypeDeclarationBuilder getTypeBuilder();

    boolean filterAccepts(ResourceChange.Type declaration, String namespace, String typeName);

    List<PackageDescr> getPackageDescrs(String namespace);
}

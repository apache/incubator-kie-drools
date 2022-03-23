package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.PackageRegistryManager;
import org.drools.compiler.builder.impl.BuildResultAccumulator;
import org.drools.compiler.builder.impl.BuildResultAccumulatorImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.builder.impl.PackageRegistryManagerImpl;
import org.drools.compiler.builder.impl.TypeDeclarationBuilder;
import org.drools.compiler.builder.impl.TypeDeclarationContext;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.ResourceChange;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DummyTypeDeclarationContext implements TypeDeclarationContext {

    private KnowledgeBuilderConfigurationImpl configuration;
    private final PackageRegistryManager packageRegistryManager;
    private final BuildResultAccumulator buildResultAccumulator = new BuildResultAccumulatorImpl();

    public DummyTypeDeclarationContext(KnowledgeBuilderConfigurationImpl configuration, PackageRegistryManager packageRegistryManager) {
        this.configuration = configuration;
        this.packageRegistryManager = packageRegistryManager;
    }

    @Override
    public TypeDeclarationBuilder getTypeBuilder() {
        return null;
    }

    @Override
    public Resource getCurrentResource() {
        return null;
    }

    @Override
    public boolean filterAccepts(ResourceChange.Type declaration, String namespace, String typeName) {
        return false;
    }

    @Override
    public PackageRegistry getPackageRegistry(String packageName) {
        return packageRegistryManager.getPackageRegistry(packageName);
    }

    @Override
    public PackageRegistry getOrCreatePackageRegistry(PackageDescr packageDescr) {
        return packageRegistryManager.getOrCreatePackageRegistry(packageDescr);
    }

    @Override
    public Map<String, PackageRegistry> getPackageRegistry() {
        return packageRegistryManager.getPackageRegistry();
    }



    @Override
    public KnowledgeBuilderConfigurationImpl getBuilderConfiguration() {
        return configuration;
    }

    @Override
    public InternalKnowledgeBase getKnowledgeBase() {
        return null;
    }

    @Override
    public ClassLoader getRootClassLoader() {
        return configuration.getClassLoader();
    }

    @Override
    public void addBuilderResult(KnowledgeBuilderResult result) {
        buildResultAccumulator.addBuilderResult(result);
    }

    @Override
    public boolean hasErrors() {
        return buildResultAccumulator.hasErrors();
    }
}

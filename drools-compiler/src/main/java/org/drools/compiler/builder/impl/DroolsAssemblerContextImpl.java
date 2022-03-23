package org.drools.compiler.builder.impl;

import org.drools.compiler.builder.DroolsAssemblerContext;
import org.drools.compiler.builder.PackageRegistryManager;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.core.base.ClassObjectType;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.ObjectType;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilderErrors;

import java.util.List;
import java.util.Map;

public class DroolsAssemblerContextImpl implements DroolsAssemblerContext, BuilderConfigurationProvider, InternalKnowledgeBaseProvider, RootClassLoaderProvider {
//    private final BuilderConfigurationProvider builderConfigurationProvider;
//    private final InternalKnowledgeBaseProvider knowledgeBaseProvider;
//    private final RootClassLoaderProvider rootClassLoaderProvider;

    private final KnowledgeBuilderConfigurationImpl knowledgeBuilderConfiguration;
    private final ClassLoader rootClassLoader;
    private final InternalKnowledgeBase kBase;

    private final GlobalVariableContext globalVariableContext;
    private final TypeDeclarationBuilder typeBuilder;
    private final PackageRegistryManager pkgRegistryManager;
    private final BuildResultAccumulator buildResultAccumulator;

    public DroolsAssemblerContextImpl(
            KnowledgeBuilderConfigurationImpl knowledgeBuilderConfiguration,
            ClassLoader rootClassLoader,
            InternalKnowledgeBase kBase,
            GlobalVariableContext globalVariableContext,
            TypeDeclarationBuilder typeBuilder,
            PackageRegistryManager pkgRegistryManager,
            BuildResultAccumulator buildResultAccumulator) {
        this.knowledgeBuilderConfiguration = knowledgeBuilderConfiguration;
        this.rootClassLoader = rootClassLoader;
        this.kBase = kBase;
        this.globalVariableContext = globalVariableContext;
        this.typeBuilder = typeBuilder;
        this.pkgRegistryManager = pkgRegistryManager;
        this.buildResultAccumulator = buildResultAccumulator;
    }

    @Override
    public Map<String, Class<?>> getGlobals() {
        return globalVariableContext.getGlobals();
    }

    @Override
    public KnowledgeBuilderConfigurationImpl getBuilderConfiguration() {
        return knowledgeBuilderConfiguration;
    }

    @Override
    public TypeDeclaration getAndRegisterTypeDeclaration(Class<?> cls, String packageName) {
        InternalKnowledgeBase kBase = getKnowledgeBase();
        if (kBase != null) {
            InternalKnowledgePackage pkg = kBase.getPackage(packageName);
            if (pkg != null) {
                TypeDeclaration typeDeclaration = pkg.getTypeDeclaration(cls);
                if (typeDeclaration != null) {
                    return typeDeclaration;
                }
            }
        }
        return typeBuilder.getAndRegisterTypeDeclaration(cls, packageName);
    }

    @Override
    public TypeDeclaration getTypeDeclaration(Class<?> cls) {
        return cls != null ? typeBuilder.getTypeDeclaration(cls) : null;
    }

    @Override
    public TypeDeclaration getTypeDeclaration(ObjectType objectType) {
        return objectType.isTemplate() ?
                typeBuilder.getExistingTypeDeclaration(objectType.getClassName()) :
                typeBuilder.getTypeDeclaration(((ClassObjectType) objectType).getClassType());
    }

    @Override
    public ClassLoader getRootClassLoader() {
        return rootClassLoader;
    }

    @Override
    public List<PackageDescr> getPackageDescrs(String namespace) {
        return pkgRegistryManager.getPackageDescrs(namespace);
    }

    @Override
    public PackageRegistry getPackageRegistry(String packageName) {
        return pkgRegistryManager.getPackageRegistry(packageName);
    }

    @Override
    public InternalKnowledgeBase getKnowledgeBase() {
        return kBase;
    }

    @Override
    public KnowledgeBuilderErrors getErrors() {
        return buildResultAccumulator.getErrors();
    }
}

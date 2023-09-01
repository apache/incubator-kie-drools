package org.drools.compiler.builder.impl;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.drools.compiler.builder.DroolsAssemblerContext;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.base.base.ClassObjectType;
import org.drools.base.base.ObjectType;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.rule.TypeDeclaration;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilderErrors;

public class DroolsAssemblerContextImpl implements DroolsAssemblerContext, BuilderConfigurationProvider, InternalKnowledgeBaseProvider, RootClassLoaderProvider {

    private final KnowledgeBuilderConfigurationImpl knowledgeBuilderConfiguration;
    private final ClassLoader rootClassLoader;
    private final InternalKnowledgeBase kBase;

    private final GlobalVariableContext globalVariableContext;
    private final TypeDeclarationBuilder typeBuilder;
    private final PackageRegistryManagerImpl pkgRegistryManager;
    private final BuildResultCollectorImpl buildResultAccumulator;

    public DroolsAssemblerContextImpl(
            KnowledgeBuilderConfigurationImpl knowledgeBuilderConfiguration,
            ClassLoader rootClassLoader,
            InternalKnowledgeBase kBase,
            GlobalVariableContext globalVariableContext,
            TypeDeclarationBuilder typeBuilder,
            PackageRegistryManagerImpl pkgRegistryManager,
            BuildResultCollectorImpl buildResultAccumulator) {
        this.knowledgeBuilderConfiguration = knowledgeBuilderConfiguration;
        this.rootClassLoader = rootClassLoader;
        this.kBase = kBase;
        this.globalVariableContext = globalVariableContext;
        this.typeBuilder = typeBuilder;
        this.pkgRegistryManager = pkgRegistryManager;
        this.buildResultAccumulator = buildResultAccumulator;
    }

    @Override
    public Map<String, Type> getGlobals() {
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

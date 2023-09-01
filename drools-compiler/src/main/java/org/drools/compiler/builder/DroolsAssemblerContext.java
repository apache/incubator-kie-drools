package org.drools.compiler.builder;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.drools.compiler.builder.impl.BuilderConfigurationProvider;
import org.drools.compiler.builder.impl.InternalKnowledgeBaseProvider;
import org.drools.compiler.builder.impl.RootClassLoaderProvider;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.base.base.ObjectType;
import org.drools.base.rule.TypeDeclaration;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.internal.builder.KnowledgeBuilderErrors;

/**
 * This interface solely exist to make work legacy package processing
 * classes such as {@link PackageBuildContext} and {@link RuleBuildContext}.
 * It should be regarded as an implementation detail, and it should be deprecated
 */
public interface DroolsAssemblerContext
        extends BuilderConfigurationProvider, InternalKnowledgeBaseProvider, RootClassLoaderProvider {

    Map<String, Type> getGlobals();

    TypeDeclaration getAndRegisterTypeDeclaration(Class<?> cls, String name);

    TypeDeclaration getTypeDeclaration(Class<?> typeClass);
    TypeDeclaration getTypeDeclaration(ObjectType objectType);

    List<PackageDescr> getPackageDescrs(String namespace);

    PackageRegistry getPackageRegistry(String packageName);

    KnowledgeBuilderErrors getErrors();
}

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.builder.impl;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.drools.base.base.ClassObjectType;
import org.drools.base.base.ObjectType;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.rule.TypeDeclaration;
import org.drools.compiler.builder.DroolsAssemblerContext;
import org.drools.compiler.compiler.PackageRegistry;
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
        return objectType.isPrototype() ?
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

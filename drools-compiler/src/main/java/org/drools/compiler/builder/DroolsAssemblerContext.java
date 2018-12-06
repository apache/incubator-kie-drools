/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.builder;

import java.util.List;
import java.util.Map;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.rule.TypeDeclaration;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderErrors;

/**
 * This interface solely exist to make work legacy package processing
 * classes such as {@link PackageBuildContext} and {@link RuleBuildContext}.
 * It should be regarded as an implementation detail, and it should be deprecated
 */
public interface DroolsAssemblerContext {

    Map<String, Class<?>> getGlobals();

    KnowledgeBuilderConfigurationImpl getBuilderConfiguration();

    TypeDeclaration getAndRegisterTypeDeclaration(Class<?> cls, String name);

    TypeDeclaration getTypeDeclaration(Class<?> typeClass);

    ClassLoader getRootClassLoader();

    List<PackageDescr> getPackageDescrs(String namespace);

    PackageRegistry getPackageRegistry(String packageName);

    InternalKnowledgeBase getKnowledgeBase();

    KnowledgeBuilderErrors getErrors();
}

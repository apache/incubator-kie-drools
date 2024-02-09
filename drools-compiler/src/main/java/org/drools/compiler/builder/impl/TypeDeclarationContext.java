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

import java.util.List;

import org.drools.compiler.builder.PackageRegistryManager;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.internal.builder.ResourceChange;

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

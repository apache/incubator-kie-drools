/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.builder.impl;

import org.drools.compiler.builder.PackageRegistryManager;
import org.kie.api.io.Resource;
import org.kie.internal.builder.ResourceChange;

/**
 * The build context for {@link TypeDeclarationBuilder}, {@link ClassDefinitionFactory} and
 * all their related siblings.
 *
 * This is a facade that exposes only part of the {@link KnowledgeBuilderImpl} API surface
 */
public interface TypeDeclarationContext extends
        RootClassLoaderProvider,
        BuildResultAccumulator,
        BuilderConfigurationProvider,
        PackageRegistryManager,
        InternalKnowledgeBaseProvider {

    // these methods are necessary to complete the facade
    // but they should be refactored and cleaned up (possibly removed)
    // https://issues.redhat.com/browse/DROOLS-6884

    TypeDeclarationBuilder getTypeBuilder();

    Resource getCurrentResource();

    boolean filterAccepts(ResourceChange.Type declaration, String namespace, String typeName);
}

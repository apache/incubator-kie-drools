/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.builder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.PackageBuilderErrors;
import org.drools.compiler.compiler.PackageBuilderResults;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.rule.TypeDeclaration;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.internal.io.ResourceTypePackage;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.AssemblerContext;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderResults;
import org.kie.internal.builder.ResultSeverity;

public interface InternalKnowledgeBuilder extends KnowledgeBuilder, DroolsAssemblerContext, AssemblerContext {

    ResourceRemovalResult removeObjectsGeneratedFromResource( Resource resource );

    void addPackage( PackageDescr packageDescr );

    InternalKnowledgePackage getPackage(String name);

    void rewireAllClassObjectTypes();

    class ResourceRemovalResult {
        private boolean modified;
        private Collection<String> removedTypes;

        public ResourceRemovalResult(  ) {
            this( false, Collections.emptyList() );
        }

        public ResourceRemovalResult( boolean modified, Collection<String> removedTypes ) {
            this.modified = modified;
            this.removedTypes = removedTypes;
        }

        public void add(ResourceRemovalResult other) {
            mergeModified( other.modified );
            if (this.removedTypes.isEmpty()) {
                this.removedTypes = other.removedTypes;
            } else {
                this.removedTypes.addAll( other.removedTypes );
            }
        }

        public void mergeModified( boolean otherModified ) {
            this.modified = this.modified || otherModified;
        }

        public boolean isModified() {
            return modified;
        }

        public Collection<String> getRemovedTypes() {
            return removedTypes;
        }
    }

    class Empty implements InternalKnowledgeBuilder {

        private final ClassLoader rootClassLoader;

        public Empty( ClassLoader rootClassLoader ) {
            this.rootClassLoader = rootClassLoader;
        }

        @Override
        public Collection<KiePackage> getKnowledgePackages() {
            return Collections.emptyList();
        }

        @Override
        public boolean hasErrors() {
            return false;
        }

        @Override
        public KnowledgeBuilderErrors getErrors() {
            return new PackageBuilderErrors();
        }

        @Override
        public KnowledgeBuilderResults getResults( ResultSeverity... severities ) {
            return new PackageBuilderResults();
        }

        @Override
        public boolean hasResults( ResultSeverity... severities ) {
            return false;
        }

        @Override
        public ClassLoader getRootClassLoader() {
            return rootClassLoader;
        }

        @Override
        public void rewireAllClassObjectTypes() {
        }

        @Override
        public Map<String, Class<?>> getGlobals() {
            return Collections.emptyMap();
        }

        @Override
        public void add( Resource resource, ResourceType type ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add( Resource resource, ResourceType type, ResourceConfiguration configuration ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public KieBase newKieBase() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void undo() {
            throw new UnsupportedOperationException();
        }

        @Override
        public CompositeKnowledgeBuilder batch() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T extends ResourceTypePackage<?>> T computeIfAbsent( ResourceType resourceType, String namespace, Function<? super ResourceType, T> mappingFunction ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void reportError( KnowledgeBuilderError error ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ResourceRemovalResult removeObjectsGeneratedFromResource( Resource resource ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addPackage( PackageDescr packageDescr ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public InternalKnowledgePackage getPackage( String name ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public KnowledgeBuilderConfigurationImpl getBuilderConfiguration() {
            throw new UnsupportedOperationException();
        }

        @Override
        public TypeDeclaration getAndRegisterTypeDeclaration( Class<?> cls, String name ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public TypeDeclaration getTypeDeclaration( Class<?> typeClass ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<PackageDescr> getPackageDescrs( String namespace ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public PackageRegistry getPackageRegistry( String packageName ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public InternalKnowledgeBase getKnowledgeBase() {
            throw new UnsupportedOperationException();
        }
    }
}

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
package org.drools.compiler.builder;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.drools.base.base.ObjectType;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.rule.TypeDeclaration;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.PackageBuilderErrors;
import org.drools.compiler.compiler.PackageBuilderResults;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
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
        private final Supplier<KnowledgeBuilderImpl> lazyBuilder;

        private KnowledgeBuilderImpl knowledgeBuilder;

        public Empty( ClassLoader rootClassLoader, Supplier<KnowledgeBuilderImpl> lazyBuilder ) {
            this.rootClassLoader = rootClassLoader;
            this.lazyBuilder = lazyBuilder;
        }

        @Override
        public Collection<KiePackage> getKnowledgePackages() {
            return withKnowledgeBuilder(InternalKnowledgeBuilder::getKnowledgePackages, Collections.emptyList());
        }

        @Override
        public boolean hasErrors() {
            return withKnowledgeBuilder(InternalKnowledgeBuilder::hasErrors, false);
        }

        @Override
        public KnowledgeBuilderErrors getErrors() {
            return withKnowledgeBuilder(InternalKnowledgeBuilder::getErrors, new PackageBuilderErrors());
        }

        @Override
        public KnowledgeBuilderResults getResults( ResultSeverity... severities ) {
            return withKnowledgeBuilder(kb -> kb.getResults( severities ), new PackageBuilderResults());
        }

        @Override
        public boolean hasResults( ResultSeverity... severities ) {
            return withKnowledgeBuilder(kb -> kb.hasResults( severities ), false);
        }

        @Override
        public ClassLoader getRootClassLoader() {
            return rootClassLoader;
        }

        @Override
        public void rewireAllClassObjectTypes() {
            withKnowledgeBuilder(InternalKnowledgeBuilder::rewireAllClassObjectTypes);
        }

        @Override
        public Map<String, Type> getGlobals() {
            return withKnowledgeBuilder(InternalKnowledgeBuilder::getGlobals, Collections.emptyMap());
        }

        @Override
        public KieBase newKieBase() {
            return withKnowledgeBuilder(InternalKnowledgeBuilder::newKieBase, null);
        }

        @Override
        public void undo() {
            withKnowledgeBuilder(InternalKnowledgeBuilder::undo);
        }

        @Override
        public void reportError( KnowledgeBuilderError error ) {
            withKnowledgeBuilder(kb -> kb.reportError( error ));
        }

        @Override
        public ResourceRemovalResult removeObjectsGeneratedFromResource( Resource resource ) {
            return getOrCreateKnowledgeBuilder().removeObjectsGeneratedFromResource( resource );
        }

        @Override
        public InternalKnowledgePackage getPackage( String name ) {
            return withKnowledgeBuilder(kb -> kb.getPackage( name ), null);
        }

        @Override
        public KnowledgeBuilderConfigurationImpl getBuilderConfiguration() {
            return withKnowledgeBuilder(InternalKnowledgeBuilder::getBuilderConfiguration, null);
        }

        @Override
        public TypeDeclaration getAndRegisterTypeDeclaration( Class<?> cls, String name ) {
            return withKnowledgeBuilder(kb -> kb.getAndRegisterTypeDeclaration( cls, name ), null);
        }

        @Override
        public TypeDeclaration getTypeDeclaration( Class<?> typeClass ) {
            return withKnowledgeBuilder(kb -> kb.getTypeDeclaration( typeClass ), null);
        }

        @Override
        public TypeDeclaration getTypeDeclaration( ObjectType objectType ) {
            return withKnowledgeBuilder(kb -> kb.getTypeDeclaration( objectType ), null);
        }

        @Override
        public List<PackageDescr> getPackageDescrs( String namespace ) {
            return withKnowledgeBuilder(kb -> kb.getPackageDescrs( namespace ), Collections.emptyList());
        }

        @Override
        public PackageRegistry getPackageRegistry( String packageName ) {
            return withKnowledgeBuilder(kb -> kb.getPackageRegistry( packageName ), null);
        }

        @Override
        public InternalKnowledgeBase getKnowledgeBase() {
            return withKnowledgeBuilder(InternalKnowledgeBuilder::getKnowledgeBase, null);
        }

        private synchronized void withKnowledgeBuilder(Consumer<InternalKnowledgeBuilder> f) {
            if (knowledgeBuilder != null) {
                f.accept( knowledgeBuilder );
            }
        }

        private synchronized <T> T withKnowledgeBuilder(Function<InternalKnowledgeBuilder, T> f, T defaultValue) {
            return knowledgeBuilder != null ? f.apply( knowledgeBuilder ) : defaultValue;
        }

        @Override
        public void addPackage( PackageDescr packageDescr ) {
            getOrCreateKnowledgeBuilder().addPackage( packageDescr );
        }

        @Override
        public void add( Resource resource, ResourceType type ) {
            getOrCreateKnowledgeBuilder().add(resource, type);
        }

        @Override
        public void add( Resource resource, ResourceType type, ResourceConfiguration configuration ) {
            getOrCreateKnowledgeBuilder().add(resource, type, configuration);
        }

        @Override
        public <T extends ResourceTypePackage<?>> T computeIfAbsent( ResourceType resourceType, String namespace, Function<? super ResourceType, T> mappingFunction ) {
            return getOrCreateKnowledgeBuilder().computeIfAbsent( resourceType, namespace, mappingFunction );
        }

        @Override
        public CompositeKnowledgeBuilder batch() {
            return getOrCreateKnowledgeBuilder().batch();
        }

        // this method forces the creation of a KnowledgeBuilder so it should be internally called only by methods
        // modifying this empty builder and not by ones only attempting to retrieve infos from it
        private synchronized KnowledgeBuilderImpl getOrCreateKnowledgeBuilder() {
            if (knowledgeBuilder == null) {
                knowledgeBuilder = lazyBuilder.get();
            }
            return knowledgeBuilder;
        }
    }
}

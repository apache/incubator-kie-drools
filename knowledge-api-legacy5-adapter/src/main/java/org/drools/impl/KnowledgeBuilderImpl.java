/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.impl;

import org.drools.KnowledgeBase;
import org.drools.base.DefaultConsequenceExceptionHandler;
import org.drools.builder.CompositeKnowledgeBuilder;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.JaxbConfiguration;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderResults;
import org.drools.builder.ResourceConfiguration;
import org.drools.builder.ResourceType;
import org.drools.builder.ResultSeverity;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.conf.ConsequenceExceptionHandlerOption;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.definition.KnowledgePackage;
import org.drools.impl.adapters.CompositeKnowledgeBuilderAdapter;
import org.drools.impl.adapters.DecisionTableConfigurationAdapter;
import org.drools.impl.adapters.JaxbConfigurationAdapter;
import org.drools.impl.adapters.KnowledgeBaseAdapter;
import org.drools.impl.adapters.KnowledgeBuilderErrorsAdapter;
import org.drools.impl.adapters.KnowledgeBuilderResultsAdapter;
import org.drools.impl.adapters.ResourceAdapter;
import org.drools.io.Resource;
import org.kie.api.KieBaseConfiguration;

import java.util.Collection;

import static org.drools.impl.adapters.AdapterUtil.adaptResultSeverity;
import static org.drools.impl.adapters.KnowledgePackageAdapter.adaptKnowledgePackages;

public class KnowledgeBuilderImpl implements KnowledgeBuilder {

    private final org.drools.compiler.builder.impl.KnowledgeBuilderImpl delegate;

    public KnowledgeBuilderImpl() {
        delegate = new org.drools.compiler.builder.impl.KnowledgeBuilderImpl();
    }

    public KnowledgeBuilderImpl(InternalKnowledgePackage pkg) {
        this(pkg,
             null);
    }

    public KnowledgeBuilderImpl(InternalKnowledgeBase kBase) {
        this(kBase,
             null);
    }

    public KnowledgeBuilderImpl(final KnowledgeBuilderConfigurationImpl configuration) {
        this((InternalKnowledgeBase) null,
             configuration);
    }

    public KnowledgeBuilderImpl(InternalKnowledgeBase kBase,
                                KnowledgeBuilderConfigurationImpl configuration) {
        delegate = new org.drools.compiler.builder.impl.KnowledgeBuilderImpl(kBase, configuration);
    }

    public KnowledgeBuilderImpl(InternalKnowledgePackage pkg,
                                KnowledgeBuilderConfigurationImpl configuration) {
        delegate = new org.drools.compiler.builder.impl.KnowledgeBuilderImpl(pkg, configuration);
    }

    public void add(Resource resource, ResourceType type) {
        delegate.add(((ResourceAdapter)resource).getDelegate(), type.toKieResourceType());
    }

    public void add(Resource resource, ResourceType type, ResourceConfiguration configuration) {
        org.kie.api.io.ResourceConfiguration conf = null;
        if( configuration != null ) {
            if( configuration instanceof DecisionTableConfiguration ) {
                conf = new DecisionTableConfigurationAdapter( (DecisionTableConfiguration) configuration );
            } else if( configuration instanceof JaxbConfiguration ) {
                conf = new JaxbConfigurationAdapter((JaxbConfiguration) configuration);
            }
        }
        delegate.add(((ResourceAdapter)resource).getDelegate(), type.toKieResourceType(), conf );
    }

    public Collection<KnowledgePackage> getKnowledgePackages() {
        return adaptKnowledgePackages(delegate.getKnowledgePackages());
    }

    public KnowledgeBase newKnowledgeBase() {
        KieBaseConfiguration conf = new org.drools.core.impl.KnowledgeBaseFactoryServiceImpl().newKnowledgeBaseConfiguration();
        conf.setProperty(ConsequenceExceptionHandlerOption.PROPERTY_NAME, DefaultConsequenceExceptionHandler.class.getCanonicalName());
        return new KnowledgeBaseAdapter(delegate.newKnowledgeBase(conf));
    }

    public boolean hasErrors() {
        return delegate.hasErrors();
    }

    public KnowledgeBuilderErrors getErrors() {
        return new KnowledgeBuilderErrorsAdapter(delegate.getErrors());
    }

    public KnowledgeBuilderResults getResults(ResultSeverity... severities) {
        return new KnowledgeBuilderResultsAdapter(delegate.getResults(adaptResultSeverity(severities)));
    }

    public boolean hasResults(ResultSeverity... severities) {
        return delegate.hasResults(adaptResultSeverity(severities));
    }

    public void undo() {
        delegate.undo();
    }

    public CompositeKnowledgeBuilder batch() {
        return new CompositeKnowledgeBuilderAdapter(delegate.batch());
    }
}

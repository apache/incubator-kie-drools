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

import com.sun.tools.xjc.Options;
import org.drools.KnowledgeBase;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.JaxbConfiguration;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactoryService;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.impl.adapters.KnowledgeBaseAdapter;
import org.drools.impl.adapters.KnowledgeBuilderConfigurationAdapter;

import java.util.Properties;

public class KnowledgeBuilderFactoryServiceImpl implements KnowledgeBuilderFactoryService {

    public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration() {
        return new KnowledgeBuilderConfigurationAdapter(new KnowledgeBuilderConfigurationImpl());
    }

    public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration(Properties properties, ClassLoader... classLoaders) {
        return new KnowledgeBuilderConfigurationAdapter(new KnowledgeBuilderConfigurationImpl(properties, classLoaders));
    }

    public DecisionTableConfiguration newDecisionTableConfiguration() {
        return new DecisionTableConfigurationImpl();
    }

    public KnowledgeBuilder newKnowledgeBuilder() {
        return new KnowledgeBuilderImpl( );
    }

    public KnowledgeBuilder newKnowledgeBuilder(KnowledgeBuilderConfiguration conf) {
        return conf == null ? new KnowledgeBuilderImpl() : new KnowledgeBuilderImpl( (KnowledgeBuilderConfigurationImpl) ((KnowledgeBuilderConfigurationAdapter)conf).getDelegate() );
    }

    public KnowledgeBuilder newKnowledgeBuilder(KnowledgeBase kbase) {
        if (kbase instanceof KnowledgeBaseAdapter ) {
            return new KnowledgeBuilderImpl( (InternalKnowledgeBase) ( (KnowledgeBaseAdapter) kbase ).getDelegate() );
        } else if ( kbase != null ) {
            return new KnowledgeBuilderImpl( (InternalKnowledgeBase) kbase );
        } else {
            return new KnowledgeBuilderImpl( );
        }
    }

    public KnowledgeBuilder newKnowledgeBuilder(KnowledgeBase kbase,
                                                KnowledgeBuilderConfiguration conf) {
        if ( kbase != null ) {
            return new KnowledgeBuilderImpl( (InternalKnowledgeBase) kbase, (KnowledgeBuilderConfigurationImpl) conf );
        } else {
            return conf == null ? new KnowledgeBuilderImpl() : new KnowledgeBuilderImpl((KnowledgeBuilderConfigurationImpl) ((KnowledgeBuilderConfigurationAdapter)conf).getDelegate() );
        }
    }

    public JaxbConfiguration newJaxbConfiguration(Options xjcOpts,
                                                  String systemId) {
        return new org.drools.impl.JaxbConfigurationImpl( xjcOpts, systemId );
    }
}

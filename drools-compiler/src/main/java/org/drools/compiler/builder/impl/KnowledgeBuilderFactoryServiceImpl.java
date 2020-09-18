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

package org.drools.compiler.builder.impl;

import java.util.Properties;

import org.drools.core.builder.conf.impl.DecisionTableConfigurationImpl;
import org.drools.core.builder.conf.impl.ScoreCardConfigurationImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.kie.api.KieBase;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactoryService;
import org.kie.internal.builder.ScoreCardConfiguration;

public class KnowledgeBuilderFactoryServiceImpl implements KnowledgeBuilderFactoryService {

    @Override
    public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration() {
        return new KnowledgeBuilderConfigurationImpl();
    }

    @Override
    public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration(Properties properties, ClassLoader... classLoaders) {
        return new KnowledgeBuilderConfigurationImpl(properties, classLoaders);
    }

    @Override
    public DecisionTableConfiguration newDecisionTableConfiguration() {
        return new DecisionTableConfigurationImpl();
    }

    @Override
    public ScoreCardConfiguration newScoreCardConfiguration() {
        return new ScoreCardConfigurationImpl();
    }

    @Override
    public KnowledgeBuilder newKnowledgeBuilder() {
        return new KnowledgeBuilderImpl( );
    }

    @Override
    public KnowledgeBuilder newKnowledgeBuilder(KnowledgeBuilderConfiguration conf) {
        return new KnowledgeBuilderImpl( (KnowledgeBuilderConfigurationImpl) conf );
    }

    @Override
    public KnowledgeBuilder newKnowledgeBuilder(KieBase kbase) {
        if ( kbase != null ) {
            return new KnowledgeBuilderImpl( (InternalKnowledgeBase)kbase );
        } else {
            return new KnowledgeBuilderImpl();
        }
    }

    @Override
    public KnowledgeBuilder newKnowledgeBuilder(KieBase kbase,
                                                KnowledgeBuilderConfiguration conf) {
        if ( kbase != null ) {
            return new KnowledgeBuilderImpl( (InternalKnowledgeBase)kbase, (KnowledgeBuilderConfigurationImpl) conf );
        } else {
            return new KnowledgeBuilderImpl((KnowledgeBuilderConfigurationImpl) conf );
        }        
    }
}

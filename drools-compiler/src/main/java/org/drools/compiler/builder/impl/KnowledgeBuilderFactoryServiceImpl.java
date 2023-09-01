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

import java.util.Properties;

import org.drools.compiler.builder.conf.DecisionTableConfigurationImpl;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.wiring.api.classloader.ProjectClassLoader;
import org.kie.api.KieBase;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactoryService;
import org.kie.internal.utils.ChainedProperties;

public class KnowledgeBuilderFactoryServiceImpl implements KnowledgeBuilderFactoryService {

    @Override
    public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration() {
        ClassLoader projClassLoader = getClassLoader(null);

        ChainedProperties chained = ChainedProperties.getChainedProperties(projClassLoader);

        return new CompositeBuilderConfiguration(chained, projClassLoader,
                                                 KnowledgeBuilderConfigurationFactories.baseConf,
                                                 KnowledgeBuilderConfigurationFactories.ruleConf,
                                                 KnowledgeBuilderConfigurationFactories.flowConf);
    }

    @Override
    public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration(ClassLoader classLoader) {
        return newKnowledgeBuilderConfiguration(null, classLoader);
    }

    protected ClassLoader getClassLoader(ClassLoader classLoader) {
        ClassLoader projClassLoader = classLoader instanceof ProjectClassLoader ? classLoader : ProjectClassLoader.getClassLoader(classLoader, getClass());
        return projClassLoader;
    }

    @Override
    public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration(Properties properties, ClassLoader classLoader) {
        ClassLoader projClassLoader = getClassLoader(classLoader);

        ChainedProperties chained = ChainedProperties.getChainedProperties(projClassLoader);

        if ( properties != null ) {
            chained.addProperties( properties );
        }

        return new CompositeBuilderConfiguration(chained, projClassLoader,
                                                 KnowledgeBuilderConfigurationFactories.baseConf,
                                                 KnowledgeBuilderConfigurationFactories.ruleConf,
                                                 KnowledgeBuilderConfigurationFactories.flowConf);
    }

    @Override
    public DecisionTableConfiguration newDecisionTableConfiguration() {
        return new DecisionTableConfigurationImpl();
    }

    @Override
    public KnowledgeBuilder newKnowledgeBuilder() {
        return new KnowledgeBuilderImpl( );
    }

    @Override
    public KnowledgeBuilder newKnowledgeBuilder(KnowledgeBuilderConfiguration conf) {
        return new KnowledgeBuilderImpl(conf);
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
            return new KnowledgeBuilderImpl((InternalKnowledgeBase)kbase, conf );
        } else {
            return new KnowledgeBuilderImpl(conf );
        }
    }
}

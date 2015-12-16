/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.impl;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.SessionConfiguration;
import org.drools.core.SessionConfigurationImpl;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactoryService;

import java.util.Properties;
import java.util.UUID;

public class KnowledgeBaseFactoryServiceImpl implements KnowledgeBaseFactoryService {

    public KieBaseConfiguration newKnowledgeBaseConfiguration() {
        return new RuleBaseConfiguration();
    }
        
    public KieBaseConfiguration newKnowledgeBaseConfiguration(Properties properties, ClassLoader... classLoaders) {
        return new RuleBaseConfiguration(properties, classLoaders);
    }
    
    public KieSessionConfiguration newKnowledgeSessionConfiguration() {
        return SessionConfiguration.newInstance();
    }
        
    public KieSessionConfiguration newKnowledgeSessionConfiguration(Properties properties) {
        return new SessionConfigurationImpl(properties);
    }
    
    public KnowledgeBase newKnowledgeBase() {
        return newKnowledgeBase( UUID.randomUUID().toString() );
    }
    
    public KnowledgeBase newKnowledgeBase( String kbaseId ) {
        return newKnowledgeBase( kbaseId, null );
    }
    
    public KnowledgeBase newKnowledgeBase(KieBaseConfiguration conf) {
        return newKnowledgeBase( UUID.randomUUID().toString(), (RuleBaseConfiguration) conf );
    }

    public KnowledgeBase newKnowledgeBase(String kbaseId, 
                                          KieBaseConfiguration conf) {
        return new KnowledgeBaseImpl( kbaseId, (RuleBaseConfiguration) conf);
    }

    public Environment newEnvironment() {
        return EnvironmentFactory.newEnvironment();//new EnvironmentImpl(); //
    }
}

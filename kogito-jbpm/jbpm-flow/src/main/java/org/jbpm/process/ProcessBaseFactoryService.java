/**
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

package org.jbpm.process;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.SessionConfiguration;
import org.drools.core.impl.EnvironmentFactory;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactoryService;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.util.Properties;

public class ProcessBaseFactoryService implements KnowledgeBaseFactoryService {

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
        return SessionConfiguration.newInstance(properties);
    }        
    
    public KnowledgeBase newKnowledgeBase() {       
        return new ProcessBaseImpl();      
    }   
    
    public KnowledgeBase newKnowledgeBase( String kbaseId ) {       
        return new ProcessBaseImpl();      
    }   
    
    public KnowledgeBase newKnowledgeBase(KieBaseConfiguration conf) {
        return new ProcessBaseImpl();
    }

    public KnowledgeBase newKnowledgeBase(String kbaseId, 
                                          KieBaseConfiguration conf) {
        return new ProcessBaseImpl();
    }

	public Environment newEnvironment() {
		return EnvironmentFactory.newEnvironment();
	}

    public KnowledgeBase getKnowledgeBase(String arg0) {
        return new ProcessBaseImpl();
    }

    public StatefulKnowledgeSession getStatefulKnowlegeSession(String arg0) {
        return new ProcessBaseImpl().newStatefulKnowledgeSession();
    }
}

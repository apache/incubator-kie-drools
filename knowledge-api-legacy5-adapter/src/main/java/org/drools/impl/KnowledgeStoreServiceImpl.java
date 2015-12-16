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
import org.drools.impl.adapters.EnvironmentAdapter;
import org.drools.impl.adapters.KnowledgeBaseAdapter;
import org.drools.impl.adapters.KnowledgeSessionConfigurationAdapter;
import org.drools.impl.adapters.StatefulKnowledgeSessionAdapter;
import org.drools.persistence.jpa.KnowledgeStoreService;
import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.kie.api.KieBase;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;

public class KnowledgeStoreServiceImpl implements KnowledgeStoreService {
	
	public StatefulKnowledgeSession newStatefulKnowledgeSession(
			KnowledgeBase kbase, KnowledgeSessionConfiguration configuration,
			Environment environment) {
		return new StatefulKnowledgeSessionAdapter(JPAKnowledgeService.newStatefulKnowledgeSession(
			(KieBase) ((KnowledgeBaseAdapter) kbase).delegate, 
			configuration == null ? null : ((KnowledgeSessionConfigurationAdapter) configuration).getDelegate(), 
			((EnvironmentAdapter) environment).delegate));
	}

    @Deprecated
	public StatefulKnowledgeSession loadStatefulKnowledgeSession(int id,
			KnowledgeBase kbase, KnowledgeSessionConfiguration configuration,
			Environment environment) {
		return new StatefulKnowledgeSessionAdapter(JPAKnowledgeService.loadStatefulKnowledgeSession(
			id,
			(KieBase) ((KnowledgeBaseAdapter) kbase).delegate, 
			configuration == null ? null : ((KnowledgeSessionConfigurationAdapter) configuration).getDelegate(), 
			((EnvironmentAdapter) environment).delegate));
	}

    public StatefulKnowledgeSession loadStatefulKnowledgeSession(Long id,
            KnowledgeBase kbase, KnowledgeSessionConfiguration configuration,
            Environment environment) {
        return new StatefulKnowledgeSessionAdapter(JPAKnowledgeService.loadStatefulKnowledgeSession(
                id,
                (KieBase) ((KnowledgeBaseAdapter) kbase).delegate,
                configuration == null ? null : ((KnowledgeSessionConfigurationAdapter) configuration).getDelegate(),
                ((EnvironmentAdapter) environment).delegate));
    }

}

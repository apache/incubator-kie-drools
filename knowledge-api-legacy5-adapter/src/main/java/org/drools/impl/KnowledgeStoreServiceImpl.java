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

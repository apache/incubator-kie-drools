package org.drools.impl;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseProvider;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;

public class KnowledgeBaseProviderImpl implements KnowledgeBaseProvider {

    public KnowledgeBase newKnowledgeBase(KnowledgeBaseConfiguration conf) {
        return new KnowledgeBaseImpl( RuleBaseFactory.newRuleBase( ( RuleBaseConfiguration ) conf ) );
    }
    
	public KnowledgeBase newKnowledgeBase() {		
		return new KnowledgeBaseImpl( RuleBaseFactory.newRuleBase() );		
	}

}

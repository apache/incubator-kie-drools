package org.drools.impl;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseProvider;
import org.drools.RuleBaseFactory;

public class KnowledgeBaseProviderImpl implements KnowledgeBaseProvider {

	public KnowledgeBase newKnowledgeBase() {		
		return new KnowledgeBaseImpl( RuleBaseFactory.newRuleBase() );		
	}

}

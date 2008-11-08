package org.drools.impl;

import java.util.Properties;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseProvider;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;

public class KnowledgeBaseProviderImpl implements KnowledgeBaseProvider {

    public KnowledgeBaseConfiguration newKnowledgeBaseConfiguration() {
        return new RuleBaseConfiguration();
    }
        
    public KnowledgeBaseConfiguration newKnowledgeBaseConfiguration(Properties properties, ClassLoader classLoader) {
        return new RuleBaseConfiguration(classLoader, properties);
    }        
    
	public KnowledgeBase newKnowledgeBase() {		
		return new KnowledgeBaseImpl( RuleBaseFactory.newRuleBase() );		
	}	
	
    public KnowledgeBase newKnowledgeBase(KnowledgeBaseConfiguration conf) {
        return new KnowledgeBaseImpl( RuleBaseFactory.newRuleBase( ( RuleBaseConfiguration ) conf ) );
    }	
}

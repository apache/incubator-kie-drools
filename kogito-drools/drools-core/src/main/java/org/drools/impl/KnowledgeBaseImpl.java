package org.drools.impl;

import java.util.Collection;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeSessionFactory;
import org.drools.RuleBase;
import org.drools.StatefulKnowledgeSession;
import org.drools.knowledge.definitions.KnowledgePackage;
import org.drools.knowledge.definitions.impl.KnowledgePackageImp;
import org.drools.reteoo.ReteooStatefulSession;

public class KnowledgeBaseImpl implements KnowledgeBase {
	private RuleBase ruleBase;
	
	public KnowledgeBaseImpl(RuleBase ruleBase) {
		this.ruleBase = ruleBase;
	}
	
	public void addKnowledgePackage(KnowledgePackage knowledgePackage) {
		ruleBase.addPackage( ((KnowledgePackageImp)knowledgePackage).pkg );		
	}
	
	public void addKnowledgePackages(Collection<KnowledgePackage> knowledgePackages) {
		for ( KnowledgePackage knowledgePackage : knowledgePackages ) {
			ruleBase.addPackage( ((KnowledgePackageImp)knowledgePackage).pkg );
		}		
	}	
	
	public StatefulKnowledgeSession newStatefulKnowledgeSession() {
		ReteooStatefulSession session = ( ReteooStatefulSession ) this.ruleBase.newStatefulSession();			
		return new StatefulKnowledgeSessionImpl( session );
	}

}

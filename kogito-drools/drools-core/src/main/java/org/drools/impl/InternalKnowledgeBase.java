package org.drools.impl;

import org.drools.KnowledgeBase;
import org.drools.RuleBase;

public interface InternalKnowledgeBase extends KnowledgeBase {
	
	RuleBase getRuleBase();
	
}

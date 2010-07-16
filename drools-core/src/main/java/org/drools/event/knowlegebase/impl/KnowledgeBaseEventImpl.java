package org.drools.event.knowlegebase.impl;

import org.drools.KnowledgeBase;
import org.drools.event.knowledgebase.KnowledgeBaseEvent;

public class KnowledgeBaseEventImpl implements KnowledgeBaseEvent {
    private KnowledgeBase knowledgeBase;
    
    public KnowledgeBaseEventImpl(KnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }
    
    public KnowledgeBase getKnowledgeBase() {
        return this.knowledgeBase;
    }

	@Override
	public String toString() {
		return "==>[KnowledgeBaseEventImpl: getKnowledgeBase()=" + getKnowledgeBase() + "]";
	}

}

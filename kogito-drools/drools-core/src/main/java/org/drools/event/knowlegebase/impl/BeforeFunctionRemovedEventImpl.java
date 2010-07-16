package org.drools.event.knowlegebase.impl;

import org.drools.KnowledgeBase;
import org.drools.event.knowledgebase.BeforeFunctionRemovedEvent;

public class BeforeFunctionRemovedEventImpl extends KnowledgeBaseEventImpl implements BeforeFunctionRemovedEvent {
    String function;
    
    public BeforeFunctionRemovedEventImpl(KnowledgeBase knowledgeBase, String function) {
        super( knowledgeBase );
    }

    public String getFunction() {
        return this.function;
    }

	@Override
	public String toString() {
		return "==>[BeforeFunctionRemovedEventImpl: getFunction()=" + getFunction() + ", getKnowledgeBase()="
				+ getKnowledgeBase() + "]";
	}

}

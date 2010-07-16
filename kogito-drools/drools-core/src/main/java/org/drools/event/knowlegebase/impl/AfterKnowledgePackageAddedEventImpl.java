package org.drools.event.knowlegebase.impl;

import org.drools.KnowledgeBase;
import org.drools.definition.KnowledgePackage;
import org.drools.event.knowledgebase.AfterKnowledgePackageAddedEvent;

public class AfterKnowledgePackageAddedEventImpl extends KnowledgeBaseEventImpl implements AfterKnowledgePackageAddedEvent {
    private KnowledgePackage knowledgePackage;
    
    public AfterKnowledgePackageAddedEventImpl(KnowledgeBase knowledgeBase, KnowledgePackage knowledgePackage) {
        super( knowledgeBase );
        this.knowledgePackage = knowledgePackage;
    }

    public KnowledgePackage getKnowledgePackage() {
        return this.knowledgePackage;
    }

	@Override
	public String toString() {
		return "==>[AfterKnowledgePackageAddedEventImpl: getKnowledgePackage()=" + getKnowledgePackage()
				+ ", getKnowledgeBase()=" + getKnowledgeBase() + "]";
	}
    
}

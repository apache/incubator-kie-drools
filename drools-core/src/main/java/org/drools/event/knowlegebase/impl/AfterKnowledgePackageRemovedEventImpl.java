package org.drools.event.knowlegebase.impl;

import org.drools.KnowledgeBase;
import org.drools.definition.KnowledgePackage;
import org.drools.event.knowledgebase.AfterKnowledgePackageRemovedEvent;

public class AfterKnowledgePackageRemovedEventImpl extends KnowledgeBaseEventImpl implements AfterKnowledgePackageRemovedEvent {
    private KnowledgePackage knowledgePackage;
    
    public AfterKnowledgePackageRemovedEventImpl(KnowledgeBase knowledgeBase, KnowledgePackage knowledgePackage) {
        super( knowledgeBase );
        this.knowledgePackage = knowledgePackage;
    }

    public KnowledgePackage getKnowledgePackage() {
        return this.knowledgePackage;
    }

	@Override
	public String toString() {
		return "==>[AfterKnowledgePackageRemovedEventImpl: getKnowledgePackage()=" + getKnowledgePackage()
				+ ", getKnowledgeBase()=" + getKnowledgeBase() + "]";
	}
    
}

package org.drools.event.knowlegebase.impl;

import org.drools.KnowledgeBase;
import org.drools.definition.KnowledgePackage;
import org.drools.event.knowledgebase.BeforeKnowledgePackageRemovedEvent;


public class BeforeKnowledgePackageRemovedEventImpl extends KnowledgeBaseEventImpl implements BeforeKnowledgePackageRemovedEvent {
    private KnowledgePackage knowledgePackage;
    
    public BeforeKnowledgePackageRemovedEventImpl(KnowledgeBase knowledgeBase, KnowledgePackage knowledgePackage) {
        super( knowledgeBase );
        this.knowledgePackage = knowledgePackage;
    }

    public KnowledgePackage getKnowledgePackage() {
        return this.knowledgePackage;
    }

	@Override
	public String toString() {
		return "==>[BeforeKnowledgePackageRemovedEventImpl: getKnowledgePackage()=" + getKnowledgePackage()
				+ ", getKnowledgeBase()=" + getKnowledgeBase() + "]";
	}

}

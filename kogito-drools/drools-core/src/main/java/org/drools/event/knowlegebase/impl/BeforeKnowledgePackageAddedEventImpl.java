package org.drools.event.knowlegebase.impl;

import org.drools.KnowledgeBase;
import org.drools.definition.KnowledgePackage;
import org.drools.event.knowledgebase.BeforeKnowledgePackageAddedEvent;

public class BeforeKnowledgePackageAddedEventImpl extends KnowledgeBaseEventImpl implements BeforeKnowledgePackageAddedEvent {
    private KnowledgePackage knowledgePackage;
    
    public BeforeKnowledgePackageAddedEventImpl(KnowledgeBase knowledgeBase, KnowledgePackage knowledgePackage) {
        super( knowledgeBase );
    }

    public KnowledgePackage getKnowledgePackage() {
        return this.knowledgePackage;
    }

	@Override
	public String toString() {
		return "==>[BeforeKnowledgePackageAddedEventImpl: getKnowledgePackage()=" + getKnowledgePackage()
				+ ", getKnowledgeBase()=" + getKnowledgeBase() + "]";
	}

}

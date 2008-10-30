package org.drools.event.knowlegebase.impl;

import org.drools.KnowledgeBase;
import org.drools.event.knowledgebase.BeforeKnowledgePackageAddedEvent;
import org.drools.knowledge.definitions.KnowledgePackage;

public class BeforeKnowledgePackageAddedEventImpl extends KnowledgeBaseEventImpl implements BeforeKnowledgePackageAddedEvent {
    private KnowledgePackage knowledgePackage;
    
    public BeforeKnowledgePackageAddedEventImpl(KnowledgeBase knowledgeBase, KnowledgePackage knowledgePackage) {
        super( knowledgeBase );
    }

    public KnowledgePackage getKnowledgePackage() {
        return this.knowledgePackage;
    }

}

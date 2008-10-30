package org.drools.event.knowlegebase.impl;

import org.drools.KnowledgeBase;
import org.drools.event.knowledgebase.AfterKnowledgePackageAddedEvent;
import org.drools.knowledge.definitions.KnowledgePackage;

public class AfterKnowledgePackageAddedEventImpl extends KnowledgeBaseEventImpl implements AfterKnowledgePackageAddedEvent {
    private KnowledgePackage knowledgePackage;
    
    public AfterKnowledgePackageAddedEventImpl(KnowledgeBase knowledgeBase, KnowledgePackage knowledgePackage) {
        super( knowledgeBase );
        this.knowledgePackage = knowledgePackage;
    }

    public KnowledgePackage getKnowledgePackage() {
        return this.knowledgePackage;
    }
}

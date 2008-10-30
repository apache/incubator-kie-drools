package org.drools.event.knowlegebase.impl;

import org.drools.KnowledgeBase;
import org.drools.event.knowledgebase.AfterKnowledgePackageRemovedEvent;
import org.drools.knowledge.definitions.KnowledgePackage;

public class AfterKnowledgePackageRemovedEventImpl extends KnowledgeBaseEventImpl implements AfterKnowledgePackageRemovedEvent {
    private KnowledgePackage knowledgePackage;
    
    public AfterKnowledgePackageRemovedEventImpl(KnowledgeBase knowledgeBase, KnowledgePackage knowledgePackage) {
        super( knowledgeBase );
        this.knowledgePackage = knowledgePackage;
    }

    public KnowledgePackage getKnowledgePackage() {
        return this.knowledgePackage;
    }
}

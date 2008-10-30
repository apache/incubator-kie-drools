package org.drools.event.knowlegebase.impl;

import org.drools.KnowledgeBase;
import org.drools.event.knowledgebase.BeforeKnowledgePackageRemovedEvent;
import org.drools.knowledge.definitions.KnowledgePackage;


public class BeforeKnowledgePackageRemovedEventImpl extends KnowledgeBaseEventImpl implements BeforeKnowledgePackageRemovedEvent {
    private KnowledgePackage knowledgePackage;
    
    public BeforeKnowledgePackageRemovedEventImpl(KnowledgeBase knowledgeBase, KnowledgePackage knowledgePackage) {
        super( knowledgeBase );
        this.knowledgePackage = knowledgePackage;
    }

    public KnowledgePackage getKnowledgePackage() {
        return this.knowledgePackage;
    }

}

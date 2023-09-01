package org.drools.core.event.knowlegebase.impl;

import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.event.kiebase.AfterKiePackageAddedEvent;

public class AfterKiePackageAddedEventImpl extends KnowledgeBaseEventImpl implements AfterKiePackageAddedEvent {
    private KiePackage knowledgePackage;
    
    public AfterKiePackageAddedEventImpl(KieBase knowledgeBase, KiePackage knowledgePackage) {
        super( knowledgeBase );
        this.knowledgePackage = knowledgePackage;
    }

    public KiePackage getKiePackage() {
        return this.knowledgePackage;
    }

    @Override
    public String toString() {
        return "==>[AfterKiePackageAddedEventImpl: getKiePackage()=" + getKiePackage()
                + ", getKieBase()=" + getKieBase() + "]";
    }
    
}

package org.drools.core.event.knowlegebase.impl;

import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.event.kiebase.AfterKiePackageRemovedEvent;

public class AfterKiePackageRemovedEventImpl extends KnowledgeBaseEventImpl implements AfterKiePackageRemovedEvent {
    private KiePackage knowledgePackage;
    
    public AfterKiePackageRemovedEventImpl(KieBase knowledgeBase, KiePackage knowledgePackage) {
        super( knowledgeBase );
        this.knowledgePackage = knowledgePackage;
    }

    public KiePackage getKiePackage() {
        return this.knowledgePackage;
    }

    @Override
    public String toString() {
        return "==>[AfterKiePackageRemovedEventImpl: getKiePackage()=" + getKiePackage()
                + ", getKieBase()=" + getKieBase() + "]";
    }
    
}

package org.drools.core.event.knowlegebase.impl;

import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.event.kiebase.BeforeKiePackageRemovedEvent;


public class BeforeKiePackageRemovedEventImpl extends KnowledgeBaseEventImpl implements BeforeKiePackageRemovedEvent {
    private KiePackage knowledgePackage;
    
    public BeforeKiePackageRemovedEventImpl(KieBase knowledgeBase, KiePackage knowledgePackage) {
        super( knowledgeBase );
        this.knowledgePackage = knowledgePackage;
    }

    public KiePackage getKiePackage() {
        return this.knowledgePackage;
    }

    @Override
    public String toString() {
        return "==>[BeforeKiePackageRemovedEventImpl: getKiePackage()=" + getKiePackage()
                + ", getKieBase()=" + getKieBase() + "]";
    }

}

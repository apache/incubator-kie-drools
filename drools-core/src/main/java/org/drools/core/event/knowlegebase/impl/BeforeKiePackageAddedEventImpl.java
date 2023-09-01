package org.drools.core.event.knowlegebase.impl;

import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.event.kiebase.BeforeKiePackageAddedEvent;

public class BeforeKiePackageAddedEventImpl extends KnowledgeBaseEventImpl implements BeforeKiePackageAddedEvent {
    private KiePackage knowledgePackage;
    
    public BeforeKiePackageAddedEventImpl(KieBase knowledgeBase, KiePackage knowledgePackage) {
        super( knowledgeBase );
        this.knowledgePackage = knowledgePackage;
    }

    public KiePackage getKiePackage() {
        return this.knowledgePackage;
    }

    @Override
    public String toString() {
        return "==>[BeforeKiePackageAddedEventImpl: getKiePackage()=" + getKiePackage()
                + ", getKieBase()=" + getKieBase() + "]";
    }

}

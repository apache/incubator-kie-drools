package org.drools.core.factmodel.traits;

import org.drools.core.impl.InternalKnowledgeBase;

public interface TraitFactory {

    void setRuleBase( InternalKnowledgeBase kBase );

    TraitTypeEnum determineTraitType( Object object );
}

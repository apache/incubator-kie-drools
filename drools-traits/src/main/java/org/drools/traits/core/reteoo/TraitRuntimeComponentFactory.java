package org.drools.traits.core.reteoo;

import org.drools.traits.core.factmodel.TraitRegistry;
import org.drools.core.reteoo.RuntimeComponentFactory;
import org.drools.base.RuleBase;

public interface TraitRuntimeComponentFactory extends RuntimeComponentFactory {
    TraitRegistry getTraitRegistry(RuleBase knowledgeBase);
}

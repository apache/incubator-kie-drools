package org.drools.traits.core.common;

import org.drools.kiesession.entrypoints.NamedEntryPoint;
import org.drools.core.common.EntryPointFactory;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.base.rule.EntryPointId;
import org.drools.kiesession.entrypoints.NamedEntryPointFactory;

public class TraitEntryPointFactory extends NamedEntryPointFactory implements EntryPointFactory {

    @Override
    public NamedEntryPoint createEntryPoint(EntryPointNode addedNode, EntryPointId id, ReteEvaluator reteEvaluator) {
        return new TraitNamedEntryPoint(id, addedNode, reteEvaluator);
    }
}

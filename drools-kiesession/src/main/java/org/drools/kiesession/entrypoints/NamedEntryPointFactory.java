package org.drools.kiesession.entrypoints;

import org.drools.core.common.EntryPointFactory;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.base.rule.EntryPointId;

public class NamedEntryPointFactory implements EntryPointFactory {

    @Override
    public NamedEntryPoint createEntryPoint(EntryPointNode addedNode, EntryPointId id, ReteEvaluator reteEvaluator) {
        return new NamedEntryPoint(id, addedNode, reteEvaluator);
    }

    public NamedEntryPointsManager createEntryPointsManager(ReteEvaluator reteEvaluator) {
        return new NamedEntryPointsManager(reteEvaluator);
    }
}

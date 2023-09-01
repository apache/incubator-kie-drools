package org.drools.reliability.core;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.base.rule.EntryPointId;
import org.drools.kiesession.entrypoints.NamedEntryPoint;
import org.drools.kiesession.entrypoints.NamedEntryPointFactory;

public class ReliableNamedEntryPointFactory extends NamedEntryPointFactory {

    @Override
    public NamedEntryPoint createEntryPoint(EntryPointNode addedNode, EntryPointId id, ReteEvaluator reteEvaluator) {
        if (!reteEvaluator.getSessionConfiguration().hasPersistedSessionOption()) {
            return super.createEntryPoint(addedNode, id, reteEvaluator);
        }
        return new ReliableNamedEntryPoint(id, addedNode, reteEvaluator);
    }
}

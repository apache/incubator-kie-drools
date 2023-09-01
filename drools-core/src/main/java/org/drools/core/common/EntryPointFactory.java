package org.drools.core.common;

import org.drools.core.EntryPointsManager;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.base.rule.EntryPointId;

public interface EntryPointFactory {

    InternalWorkingMemoryEntryPoint createEntryPoint(EntryPointNode addedNode, EntryPointId id, ReteEvaluator reteEvaluator);

    EntryPointsManager createEntryPointsManager(ReteEvaluator reteEvaluator);
}

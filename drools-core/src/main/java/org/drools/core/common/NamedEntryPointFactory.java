package org.drools.core.common;

import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.rule.EntryPointId;

public interface NamedEntryPointFactory {

    public NamedEntryPoint createNamedEntryPoint(EntryPointNode addedNode, EntryPointId id, StatefulKnowledgeSessionImpl wm);

}

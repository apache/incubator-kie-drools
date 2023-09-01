package org.drools.traits.core.reteoo;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.base.base.ObjectType;
import org.drools.core.common.PropagationContext;

public class TraitProxyObjectTypeNode extends ObjectTypeNode {

    public TraitProxyObjectTypeNode(int id, EntryPointNode source, ObjectType objectType, BuildContext context) {
        super(id, source, objectType, context);
    }

    /**
     * Do not use this constructor! It should be used just by deserialization.
     */
    public TraitProxyObjectTypeNode() {
    }

    @Override
    public void modifyObject(InternalFactHandle factHandle,
                             ModifyPreviousTuples modifyPreviousTuples,
                             PropagationContext context,
                             ReteEvaluator reteEvaluator) {
        checkDirty();
        // node can't have sinks. Avoid mask recalculations and other operations on updates
    }
}

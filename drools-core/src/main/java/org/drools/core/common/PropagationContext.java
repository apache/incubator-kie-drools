package org.drools.core.common;

import org.drools.base.base.ObjectType;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.rule.EntryPointId;
import org.drools.core.marshalling.MarshallerReaderContext;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.runtime.rule.FactHandle;

import java.io.Externalizable;

public interface PropagationContext extends Externalizable {

    enum Type {
        INSERTION, DELETION, MODIFICATION, RULE_ADDITION, RULE_REMOVAL, EXPIRATION
    }

    long getPropagationNumber();

    Type getType();

    RuleImpl getRuleOrigin();

    TerminalNode getTerminalNodeOrigin();

    /**
     * @return fact handle that was inserted, updated or retracted that created the PropagationContext
     */
    FactHandle getFactHandle();
    void setFactHandle(FactHandle factHandle);

    EntryPointId getEntryPoint();
    
    BitMask getModificationMask();
    PropagationContext adaptModificationMaskForObjectType(ObjectType type, ReteEvaluator reteEvaluator);

    MarshallerReaderContext getReaderContext();

    void cleanReaderContext();

    void setEntryPoint(EntryPointId entryPoint);
}

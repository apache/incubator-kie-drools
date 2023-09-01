package org.drools.base.rule;

import java.io.Externalizable;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.kie.api.runtime.rule.FactHandle;

public interface ContextEntry
    extends
    Externalizable {

    ContextEntry getNext();

    void setNext(ContextEntry entry);

    void updateFromTuple(ValueResolver valueResolver, BaseTuple tuple);

    void updateFromFactHandle(ValueResolver valueResolver, FactHandle handle);

    void resetTuple();

    void resetFactHandle();

}

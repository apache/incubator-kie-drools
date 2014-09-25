package org.drools.core.beliefsystem.jtms;


import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.util.LinkedListEntry;
import org.drools.core.util.LinkedListNode;
import org.kie.internal.runtime.beliefs.Mode;

public interface JTMSBeliefSet<M extends ModedAssertion<M>> extends BeliefSet<M> {

//    void setNegativeFactHandle(InternalFactHandle insert);
//
//    InternalFactHandle getNegativeFactHandle();
//
//    void setPositiveFactHandle(InternalFactHandle fh);
//
//    InternalFactHandle getPositiveFactHandle();

    Object getLast();
}

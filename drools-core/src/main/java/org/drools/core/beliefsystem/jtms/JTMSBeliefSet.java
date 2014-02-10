package org.drools.core.beliefsystem.jtms;


import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.util.LinkedListEntry;

public interface JTMSBeliefSet extends BeliefSet {

    void setNegativeFactHandle(InternalFactHandle insert);

    InternalFactHandle getNegativeFactHandle();

    void setPositiveFactHandle(InternalFactHandle fh);

    InternalFactHandle getPositiveFactHandle();

    Object getLast();
}

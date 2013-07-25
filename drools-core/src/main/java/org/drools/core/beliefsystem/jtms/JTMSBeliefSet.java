package org.drools.core.beliefsystem.jtms;


import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.common.InternalFactHandle;

public interface JTMSBeliefSet extends BeliefSet {
    boolean isNegated();

    boolean isConflicting();

    boolean isPositive();

    void setNegativeFactHandle(InternalFactHandle insert);

    InternalFactHandle getNegativeFactHandle();

    void setPositiveFactHandle(InternalFactHandle fh);

    InternalFactHandle getPositiveFactHandle();
}

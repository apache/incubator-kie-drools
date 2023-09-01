package org.drools.tms.beliefsystem.jtms;


import org.drools.tms.beliefsystem.BeliefSet;
import org.drools.tms.beliefsystem.ModedAssertion;

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

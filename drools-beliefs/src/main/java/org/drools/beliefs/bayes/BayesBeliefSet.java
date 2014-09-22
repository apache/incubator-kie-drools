package org.drools.beliefs.bayes;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.simple.SimpleLogicalDependency;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.Entry;
import org.drools.core.util.LinkedListNode;

import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListEntry;

import java.util.Arrays;

public class BayesBeliefSet extends LinkedList implements BeliefSet {
    private BayesBeliefSystem beliefSystem;
    private InternalFactHandle rootHandle;

    private int conflictCounter;

    public BayesBeliefSet(InternalFactHandle rootHandle, BayesBeliefSystem beliefSystem) {
        this.beliefSystem = beliefSystem;
        this.rootHandle = rootHandle;
    }

    @Override
    public BeliefSystem getBeliefSystem() {
        return beliefSystem;
    }

    @Override
    public InternalFactHandle getFactHandle() {
        return rootHandle;
    }

    @Override
    public void add( LinkedListNode mode ) {
        BayesHardEvidence evidence = (BayesHardEvidence) mode;
        if ( !isEmpty() ) {
            BayesHardEvidence firstEvidence = (BayesHardEvidence) getFirst();
            if ( !Arrays.equals( evidence.getDistribution(), firstEvidence.getDistribution()) ) {
                conflictCounter++;
            }
        }
        super.addLast( mode );
    }

    @Override
    public void remove( LinkedListNode mode ) {
        boolean wasFirst = getFirst() == mode;
        super.remove(mode);

        if ( isEmpty() ) {
            conflictCounter = 0;
            return;
        }

        BayesHardEvidence firstEvidence = (BayesHardEvidence) getFirst();



        if ( wasFirst ) {
            // the first node was removed, reset the conflictCounter and recalculate the nodes in conflict
            conflictCounter = 0;
            for ( BayesHardEvidence currentEvidence = (BayesHardEvidence) mode.getNext(); currentEvidence != null; currentEvidence = currentEvidence.getNext() ) {
                if (  !Arrays.equals( firstEvidence.getDistribution(), currentEvidence.getDistribution() )  ) {
                    conflictCounter++;
                }
            }
        } else if ( !Arrays.equals( ((BayesHardEvidence) mode).getDistribution(), firstEvidence.getDistribution() ) ) {
            // The removing Mode conflicted with first, so decrement the counter
            conflictCounter--;
        }
    }

    @Override
    public void cancel(PropagationContext propagationContext) {

    }

    @Override
    public void clear(PropagationContext propagationContext) {

    }

    @Override
    public void setWorkingMemoryAction(WorkingMemoryAction wmAction) {

    }

    @Override
    public boolean isNegated() {
        return false;
    }

    @Override
    public boolean isUndecided() {
        return conflictCounter > 0 ;
    }

    @Override
    public boolean isPositive() {
        return ! isEmpty();
    }
}

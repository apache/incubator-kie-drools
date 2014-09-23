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

public class BayesBeliefSet<M extends BayesHardEvidence<M>> extends LinkedList<M> implements BeliefSet<M> {
    private BayesBeliefSystem<M> beliefSystem;
    private InternalFactHandle rootHandle;

    private int conflictCounter;

    public BayesBeliefSet(InternalFactHandle rootHandle, BayesBeliefSystem<M> beliefSystem) {
        this.beliefSystem = beliefSystem;
        this.rootHandle = rootHandle;
    }

    @Override
    public BeliefSystem<M> getBeliefSystem() {
        return beliefSystem;
    }

    @Override
    public InternalFactHandle getFactHandle() {
        return rootHandle;
    }

    @Override
    public void add( M mode ) {
        BayesHardEvidence<M> evidence = mode;
        if ( !isEmpty() ) {
            BayesHardEvidence firstEvidence = getFirst();
            if ( !Arrays.equals( evidence.getDistribution(), firstEvidence.getDistribution()) ) {
                conflictCounter++;
            }
        }
        super.addLast( mode );
    }

    @Override
    public void remove( M mode ) {
        boolean wasFirst = getFirst() == mode;
        super.remove(mode);

        if ( isEmpty() ) {
            conflictCounter = 0;
            return;
        }

        BayesHardEvidence<M> firstEvidence = getFirst();

        if ( wasFirst ) {
            // the first node was removed, reset the conflictCounter and recalculate the nodes in conflict
            conflictCounter = 0;
            for ( BayesHardEvidence<M> currentEvidence = mode.getNext(); currentEvidence != null; currentEvidence = currentEvidence.getNext() ) {
                if (  !Arrays.equals( firstEvidence.getDistribution(), currentEvidence.getDistribution() )  ) {
                    conflictCounter++;
                }
            }
        } else if ( !Arrays.equals( mode.getDistribution(), firstEvidence.getDistribution() ) ) {
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
        return isConflicting();
    }

    @Override
    public boolean isConflicting() {
        return conflictCounter > 0;
    }

    @Override
    public boolean isPositive() {
        return ! isEmpty();
    }
}

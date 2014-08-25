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
        LinkedListNode node = getFirst();
        SimpleLogicalDependency ld = (SimpleLogicalDependency) ((LinkedListEntry) node).getObject();
        return rootHandle;
    }

    @Override
    public void add( LinkedListNode node ) {
        SimpleLogicalDependency ld = (SimpleLogicalDependency) ((LinkedListEntry) node).getObject();
        BayesHardEvidence evidence = (BayesHardEvidence) ld.getValue();
        if ( !isEmpty() ) {
            BayesHardEvidence firstEvidence = (BayesHardEvidence) ((SimpleLogicalDependency)((LinkedListEntry) getFirst() ).getObject()).getValue();
            if ( !Arrays.equals( evidence.getDistribution(), firstEvidence.getDistribution()) ) {
                conflictCounter++;
            }
        }
        super.addLast( node );
    }

    @Override
    public void remove( LinkedListNode node ) {
        boolean wasFirst = getFirst() == node;
        super.remove(node);

        if ( isEmpty() ) {
            conflictCounter = 0;
            return;
        }

        LogicalDependency ld = (LogicalDependency) ((LinkedListEntry) node).getObject();
        BayesHardEvidence evidence = (BayesHardEvidence) ld.getValue();
        BayesHardEvidence firstEvidence = (BayesHardEvidence) ((SimpleLogicalDependency)((LinkedListEntry) getFirst() ).getObject()).getValue();

        if ( wasFirst ) {
            // the first node was removed, reset the conflictCounter and recalculate the nodes in conflict
            conflictCounter = 0;
            for ( Entry entry = node.getNext(); entry != null; entry = entry.getNext() ) {
                LogicalDependency currentDep = (LogicalDependency) ((LinkedListEntry) entry).getObject();
                BayesHardEvidence currentEvidence = (BayesHardEvidence) ((SimpleLogicalDependency)((LinkedListEntry) currentDep ).getObject()).getValue();
                if (  !Arrays.equals( firstEvidence.getDistribution(), currentEvidence.getDistribution() )  ) {
                    conflictCounter++;
                }
            }
        } else if ( !Arrays.equals( evidence.getDistribution(), firstEvidence.getDistribution() ) ) {
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

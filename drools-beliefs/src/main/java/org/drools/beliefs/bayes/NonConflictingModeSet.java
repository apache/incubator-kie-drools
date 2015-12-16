/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.beliefs.bayes;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.ModedAssertion;
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

public class NonConflictingModeSet<M extends ModedAssertion<M>> extends LinkedList<M> implements BeliefSet<M> {
    BayesBeliefSystem<? extends ModedAssertion> beliefSystem;
    private InternalFactHandle rootHandle;

    private int conflictCounter;

    public NonConflictingModeSet(InternalFactHandle rootHandle, BayesBeliefSystem<? extends ModedAssertion> beliefSystem) {
        this.rootHandle = rootHandle;
        this.beliefSystem = beliefSystem;
    }

    @Override
    public BeliefSystem<? extends ModedAssertion> getBeliefSystem() {
        return beliefSystem;
    }

    @Override
    public InternalFactHandle getFactHandle() {
        return rootHandle;
    }

    @Override
    public void add( M mode ) {
        if ( !isEmpty() ) {
            M first = getFirst();
            if ( !first.equals( mode )) {
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

        M first = getFirst();

        if ( wasFirst ) {
            // the first node was removed, reset the conflictCounter and recalculate the nodes in conflict
            conflictCounter = 0;
            for ( M current = mode.getNext(); current != null; current = current.getNext() ) {
                if ( !first.equals( current )) {
                    conflictCounter++;
                }
            }
        } else if ( !first.equals( mode )) {
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
    public boolean isDecided() {
        return !isConflicting();
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

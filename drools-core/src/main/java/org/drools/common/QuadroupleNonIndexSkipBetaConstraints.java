/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.common;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.RuleBaseConfiguration;
import org.drools.base.evaluators.Operator;
import org.drools.core.util.LeftTupleIndexHashTable;
import org.drools.core.util.LeftTupleList;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListEntry;
import org.drools.core.util.RightTupleIndexHashTable;
import org.drools.core.util.RightTupleList;
import org.drools.core.util.AbstractHashTable.FieldIndex;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleMemory;
import org.drools.reteoo.RightTupleMemory;
import org.drools.rule.ContextEntry;
import org.drools.rule.UnificationRestriction;
import org.drools.rule.VariableConstraint;
import org.drools.spi.BetaNodeFieldConstraint;

public class QuadroupleNonIndexSkipBetaConstraints 
    implements
    BetaConstraints {
    private QuadroupleBetaConstraints constraints;
    
    private BetaNodeFieldConstraint constraint0;
    private BetaNodeFieldConstraint constraint1;
    private BetaNodeFieldConstraint constraint2;
    private BetaNodeFieldConstraint constraint3;
    
    public QuadroupleNonIndexSkipBetaConstraints() {

    }
    
    public QuadroupleNonIndexSkipBetaConstraints(QuadroupleBetaConstraints constraints) {
        this.constraints = constraints;
        LinkedList list = constraints.getConstraints();
        this.constraint0 = (BetaNodeFieldConstraint) ((LinkedListEntry)list.getFirst()).getObject();
        this.constraint1 = (BetaNodeFieldConstraint) ((LinkedListEntry)list.getFirst().getNext()).getObject();
        this.constraint2 = (BetaNodeFieldConstraint) ((LinkedListEntry)list.getFirst().getNext().getNext()).getObject();
        this.constraint3 = (BetaNodeFieldConstraint) ((LinkedListEntry)list.getFirst().getNext().getNext().getNext()).getObject();
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException( );
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        throw new UnsupportedOperationException( );
    }
    
    public BetaConstraints getOriginalConstraint() {
        return this.constraints;
    }

    public ContextEntry[] createContext() {
        return constraints.createContext();
    }

    public void updateFromTuple(ContextEntry[] context,
                                InternalWorkingMemory workingMemory,
                                LeftTuple tuple) {
        constraints.updateFromTuple( context,
                                     workingMemory,
                                     tuple );
    }

    public void updateFromFactHandle(ContextEntry[] context,
                                     InternalWorkingMemory workingMemory,
                                     InternalFactHandle handle) {
        constraints.updateFromFactHandle( context,
                                          workingMemory,
                                          handle );
    }

    public boolean isIndexed() {
        return constraints.isIndexed();
    }

    public int getIndexCount() {
        return constraints.getIndexCount();
    }

    public boolean isEmpty() {
        return constraints.isEmpty();
    }

    public BetaMemory createBetaMemory(RuleBaseConfiguration config) {
        return constraints.createBetaMemory( config );
    }

    public int hashCode() {
        return constraints.hashCode();
    }

    public LinkedList getConstraints() {
        return constraints.getConstraints();
    }

    public boolean equals(Object object) {
        return constraints.equals( object );
    }

    public void resetFactHandle(ContextEntry[] context) {
        constraints.resetFactHandle( context );
    }

    public void resetTuple(ContextEntry[] context) {
        constraints.resetTuple( context );
    }

    public String toString() {
        return constraints.toString();
    }

    public boolean isAllowedCachedLeft(final ContextEntry[] context,
                                       final InternalFactHandle handle) {
        return this.constraint0.isAllowedCachedLeft( context[0],
                                                     handle ) && this.constraint1.isAllowedCachedLeft( context[1],
                                                                                                       handle ) && this.constraint2.isAllowedCachedLeft( context[2],
                                                                                                                                                         handle )  && this.constraint3.isAllowedCachedLeft( context[3],
                                                                                                                                                                                                            handle )
               && this.constraint3.isAllowedCachedLeft( context[3],
                                                        handle );
    }

    public boolean isAllowedCachedRight(ContextEntry[] context,
                                        LeftTuple tuple) {
        return this.constraints.isAllowedCachedRight( context, tuple );
    }

    public long getListenedPropertyMask(Class<?> nodeClass) {
        return Long.MAX_VALUE;
    }
}

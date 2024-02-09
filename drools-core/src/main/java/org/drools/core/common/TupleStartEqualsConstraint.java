/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.common;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.ContextEntry;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.constraint.BetaConstraint;
import org.drools.core.reteoo.Tuple;
import org.drools.core.reteoo.TupleImpl;
import org.kie.api.runtime.rule.FactHandle;

/**
 * Checks if one tuple is the start subtuple of other tuple.
 * For instance, if we have two tuples:
 *
 * T1 = [ a, b, c ]
 * T2 = [ a, b, c, d, e]
 *
 * This constraint will evaluate to true as T1 is the starting subtuple
 * of T2. On the other hand, if we have:
 *
 * T1 = [ a, c, b ]
 * T2 = [ a, b, c, d, e ]
 *
 * This constraint will evaluate to false, as T1 is not the starting subtuple
 * of T2. Besides having the same elements, the order is different.
 *
 * This constraint is used when joining subnetworks back into the main
 * network.
 */
public class TupleStartEqualsConstraint
    implements
    BetaConstraint<ContextEntry> {

    private static final long                       serialVersionUID = 510l;

    private Declaration[]                     declarations     = new Declaration[0];

    private static final TupleStartEqualsConstraint INSTANCE         = new TupleStartEqualsConstraint();

    // this is a stateless constraint, so we can make it a singleton
    public TupleStartEqualsConstraint() {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        declarations  = (Declaration[])in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(declarations);
    }

    public static TupleStartEqualsConstraint getInstance() {
        return INSTANCE;
    }

    public Declaration[] getRequiredDeclarations() {
        return this.declarations;
    }

    public void replaceDeclaration(Declaration oldDecl,
                                   Declaration newDecl) {
    }

    public boolean isTemporal() {
        return false;
    }

    public ContextEntry createContext() {
        return new TupleStartEqualsConstraintContextEntry();
    }

    public boolean isAllowedCachedLeft(final ContextEntry context,
                                       final FactHandle handle) {
        // object MUST be a ReteTuple
        int size = ((TupleStartEqualsConstraintContextEntry) context).compareSize;
        final Tuple tuple = ((Tuple) handle.getObject()).getSubTuple( size );
        return ((TupleStartEqualsConstraintContextEntry) context).leftTuple.getSubTuple(size).equals(tuple);
    }

    public boolean isAllowedCachedRight(final BaseTuple tuple,
                                        final ContextEntry context) {
        TupleImpl nonEmptyLeftTuple = (TupleImpl) tuple.skipEmptyHandles();
        return nonEmptyLeftTuple.equals( ((TupleStartEqualsConstraintContextEntry) context).rightTuple.getSubTuple(nonEmptyLeftTuple.size()));
    }

    public String toString() {
        return "[ TupleStartEqualsConstraint ]";
    }

    public int hashCode() {
        return 10;
    }

    public boolean equals(final Object object) {
        if ( object instanceof TupleStartEqualsConstraint ) {
            return true;
        }
        return false;
    }

    public TupleStartEqualsConstraint clone() {
        return INSTANCE;
    }

    public static class TupleStartEqualsConstraintContextEntry
        implements
        ContextEntry {

        private static final long serialVersionUID = 510l;

        public Tuple leftTuple;
        public Tuple rightTuple;

        // the size of the tuple to compare
        public int                compareSize;

        private ContextEntry      entry;

        public TupleStartEqualsConstraintContextEntry() {
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            leftTuple = (Tuple)in.readObject();
            rightTuple = (Tuple)in.readObject();
            compareSize = in.readInt();
            entry       = (ContextEntry)in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(leftTuple);
            out.writeObject(rightTuple);
            out.writeInt(compareSize);
            out.writeObject(entry);
        }

        public ContextEntry getNext() {
            return this.entry;
        }

        public void setNext(final ContextEntry entry) {
            this.entry = entry;
        }

        public void updateFromTuple(final ValueResolver valueResolver,
                                    final BaseTuple tuple) {
            this.leftTuple = (Tuple) tuple.skipEmptyHandles();
            this.compareSize = leftTuple.size();
        }

        public void updateFromFactHandle(final ValueResolver valueResolver,
                                         final FactHandle handle) {
            // if it is not a rete tuple, then there is a bug in the engine...
            // it MUST be a rete tuple
            this.rightTuple = ((TupleImpl) handle.getObject()).skipEmptyHandles();
        }

        public void resetTuple() {
            this.leftTuple = null;
        }

        public void resetFactHandle() {
            this.rightTuple = null;
        }
    }

    public ConstraintType getType() {
        return ConstraintType.BETA;
    }

    public BetaConstraint cloneIfInUse() {
        return this;
    }
}

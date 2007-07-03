/*
 * Copyright 2006 JBoss Inc
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

import org.drools.reteoo.ReteTuple;
import org.drools.rule.ContextEntry;
import org.drools.rule.Declaration;
import org.drools.spi.BetaNodeFieldConstraint;

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
 *
 * @author etirelli
 *
 */
public class TupleStartEqualsConstraint
    implements
    BetaNodeFieldConstraint {

    private static final long                       serialVersionUID = 400L;

    private final Declaration[]                     declarations     = new Declaration[0];

    private static final TupleStartEqualsConstraint INSTANCE         = new TupleStartEqualsConstraint();

    // this is a stateless constraint, so we can make it a singleton
    private TupleStartEqualsConstraint() {
    }

    public static TupleStartEqualsConstraint getInstance() {
        return INSTANCE;
    }

    public Declaration[] getRequiredDeclarations() {
        return this.declarations;
    }

    public ContextEntry getContextEntry() {
        return new TupleStartEqualsConstraintContextEntry();
    }

    public boolean isAllowedCachedLeft(final ContextEntry context,
                                       final Object object) {
        // object MUST be a ReteTuple
        final ReteTuple tuple = ((ReteTuple) object).getSubTuple( ((TupleStartEqualsConstraintContextEntry) context).compareSize );
        return ((TupleStartEqualsConstraintContextEntry) context).left.equals( tuple );
    }

    public boolean isAllowedCachedRight(final ReteTuple tuple,
                                        final ContextEntry context) {
        return tuple.equals( ((TupleStartEqualsConstraintContextEntry) context).right.getSubTuple( tuple.size() ) );
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

    public static class TupleStartEqualsConstraintContextEntry
        implements
        ContextEntry {

        private static final long serialVersionUID = 400L;

        public ReteTuple          left;
        public ReteTuple          right;

        // the size of the tuple to compare
        public int                compareSize;

        private ContextEntry      entry;

        public TupleStartEqualsConstraintContextEntry() {
        }

        public ContextEntry getNext() {
            return this.entry;
        }

        public void setNext(final ContextEntry entry) {
            this.entry = entry;
        }

        public void updateFromTuple(final InternalWorkingMemory workingMemory,
                                    final ReteTuple tuple) {
            this.left = tuple;
            this.compareSize = tuple.size();
        }

        public void updateFromFactHandle(final InternalWorkingMemory workingMemory,
                                         final InternalFactHandle handle) {
            // if it is not a rete tuple, then there is a bug in the engine...
            // it MUST be a rete tuple
            this.right = (ReteTuple) handle.getObject();
        }
    }
}

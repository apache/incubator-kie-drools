package org.drools.common;

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

import java.io.Serializable;

import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.util.FactHashTable;
import org.drools.util.LinkedList;
import org.drools.util.TupleHashTable;

public class EmptyBetaConstraints
    implements
    Serializable,
    BetaConstraints {

    private static final BetaConstraints INSTANCE = new EmptyBetaConstraints();

    public static BetaConstraints getInstance() {
        return EmptyBetaConstraints.INSTANCE;
    }

    /**
     * 
     */
    private static final long serialVersionUID = 320L;

    private EmptyBetaConstraints() {
    }

    /* (non-Javadoc)
     * @see org.drools.common.BetaNodeConstraints#updateFromTuple(org.drools.reteoo.ReteTuple)
     */
    public void updateFromTuple(final InternalWorkingMemory workingMemory, final ReteTuple tuple) {
    }

    /* (non-Javadoc)
     * @see org.drools.common.BetaNodeConstraints#updateFromFactHandle(org.drools.common.InternalFactHandle)
     */
    public void updateFromFactHandle(final InternalWorkingMemory workingMemory, final InternalFactHandle handle) {
    }

    /* (non-Javadoc)
     * @see org.drools.common.BetaNodeConstraints#isAllowedCachedLeft(java.lang.Object)
     */
    public boolean isAllowedCachedLeft(final Object object) {
        return true;
    }

    /* (non-Javadoc)
     * @see org.drools.common.BetaNodeConstraints#isAllowedCachedRight(org.drools.reteoo.ReteTuple)
     */
    public boolean isAllowedCachedRight(final ReteTuple tuple) {
        return true;
    }

    public boolean isIndexed() {
        return false;
    }

    public boolean isEmpty() {
        return true;
    }

    public BetaMemory createBetaMemory() {
        final BetaMemory memory = new BetaMemory( new TupleHashTable(),
                                            new FactHashTable() );

        return memory;
    }

    public int hashCode() {
        return 1;
    }

    /* (non-Javadoc)
     * @see org.drools.common.BetaNodeConstraints#getConstraints()
     */
    public LinkedList getConstraints() {
        final LinkedList list = new LinkedList();
        return list;
    }

    /**
     * Determine if another object is equal to this.
     * 
     * @param object
     *            The object to test.
     * 
     * @return <code>true</code> if <code>object</code> is equal to this,
     *         otherwise <code>false</code>.
     */
    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        return (object != null && ( object instanceof EmptyBetaConstraints ) );
    }

}
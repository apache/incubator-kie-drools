/*
 * Copyright 2010 JBoss Inc
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

package org.drools.reteoo;

import org.drools.common.InternalFactHandle;
import org.drools.core.util.Entry;
import org.drools.core.util.index.LeftTupleList;

public class JoinNodeLeftTuple extends BaseLeftTuple {

    private static final long serialVersionUID = 540l;

    public JoinNodeLeftTuple() {
        // constructor needed for serialisation
    }

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public JoinNodeLeftTuple(final InternalFactHandle factHandle,
            final LeftTupleSink sink,
            final boolean leftTupleMemoryEnabled) {
        super( factHandle, sink, leftTupleMemoryEnabled );
    }

    public JoinNodeLeftTuple(final LeftTuple leftTuple,
            final LeftTupleSink sink,
            final boolean leftTupleMemoryEnabled) {
        super( leftTuple, sink, leftTupleMemoryEnabled );
    }

    public JoinNodeLeftTuple(final LeftTuple leftTuple,
            final RightTuple rightTuple,
            final LeftTupleSink sink) {
        super( leftTuple, rightTuple, sink );
    }

    public JoinNodeLeftTuple(final LeftTuple leftTuple,
            final RightTuple rightTuple,
            final LeftTupleSink sink,
            final boolean leftTupleMemoryEnabled) {
        this( leftTuple,
              rightTuple,
              null,
              null,
              sink,
              leftTupleMemoryEnabled );
    }

    public JoinNodeLeftTuple(final LeftTuple leftTuple,
            final RightTuple rightTuple,
            final LeftTuple currentLeftChild,
            final LeftTuple currentRightChild,
            final LeftTupleSink sink,
            final boolean leftTupleMemoryEnabled) {
        super( leftTuple, 
               rightTuple, 
               currentLeftChild, 
               currentRightChild, 
               sink, 
               leftTupleMemoryEnabled );
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#getMemory()
     */
    public LeftTupleList getMemory() {
        return this.memory;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#setMemory(org.kie.core.util.index.LeftTupleList)
     */
    public void setMemory( LeftTupleList memory ) {
        this.memory = memory;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#getPrevious()
     */
    public Entry getPrevious() {
        return previous;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#setPrevious(org.kie.core.util.Entry)
     */
    public void setPrevious( Entry previous ) {
        this.previous = previous;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#setNext(org.kie.core.util.Entry)
     */
    public void setNext( final Entry next ) {
        this.next = next;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.LeftTuple#getNext()
     */
    public Entry getNext() {
        return this.next;
    }

}

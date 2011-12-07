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

public class QueryElementNodeLeftTuple extends BaseLeftTuple {
    private static final long  serialVersionUID = 540l;

    private Object             object;

    public QueryElementNodeLeftTuple() {
        // constructor needed for serialisation
    }

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public QueryElementNodeLeftTuple(final InternalFactHandle factHandle,
                             LeftTupleSink sink,
                             boolean leftTupleMemoryEnabled) {
        super( factHandle, 
               sink, 
               leftTupleMemoryEnabled );
    }

    public QueryElementNodeLeftTuple(final LeftTuple leftTuple,
            final LeftTupleSink sink,
            final boolean leftTupleMemoryEnabled) {
        super( leftTuple, 
               sink, 
               leftTupleMemoryEnabled );
    }

    public QueryElementNodeLeftTuple(final LeftTuple leftTuple,
            RightTuple rightTuple,
            LeftTupleSink sink) {
        super( leftTuple, 
               rightTuple, 
               sink );
    }

    public QueryElementNodeLeftTuple(final LeftTuple leftTuple,
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

    public QueryElementNodeLeftTuple(final LeftTuple leftTuple,
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
     * @see org.drools.reteoo.LeftTuple#getObject()
     */
    public Object getObject() {
        return this.object;
    }
    
    /* (non-Javadoc)
     * @see org.drools.reteoo.LeftTuple#setObject(java.lang.Object)
     */
    public void setObject(final Object object) {
        this.object = object;
    }

}

/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.drools.core.base.accumulators.JavaAccumulatorFunctionExecutor.JavaAccumulatorFunctionContext;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.spi.PropagationContext;

public class FromNodeLeftTuple extends BaseLeftTuple {
    private static final long  serialVersionUID = 540l;

    public FromNodeLeftTuple() {
        // constructor needed for serialisation
    }

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public FromNodeLeftTuple(final InternalFactHandle factHandle,
                             Sink sink,
                             boolean leftTupleMemoryEnabled) {
        super( factHandle, sink, leftTupleMemoryEnabled );
    }
    
    public FromNodeLeftTuple(final InternalFactHandle factHandle,
                             final LeftTuple leftTuple,
                             final Sink sink) {
        super( factHandle, leftTuple, sink );
    }    

    public FromNodeLeftTuple(final LeftTuple leftTuple,
                             final Sink sink,
                             final PropagationContext pctx,
                             final boolean leftTupleMemoryEnabled) {
        super( leftTuple, sink, pctx, leftTupleMemoryEnabled );
    }
    
    public FromNodeLeftTuple(final LeftTuple leftTuple,
                             RightTuple rightTuple,
                             Sink sink) {
        super( leftTuple, rightTuple, sink );
    }    

    public FromNodeLeftTuple(final LeftTuple leftTuple,
                             final RightTuple rightTuple,
                             final Sink sink,
                             final boolean leftTupleMemoryEnabled) {
        this( leftTuple,
              rightTuple,
              null,
              null,
              sink,
              leftTupleMemoryEnabled );
    }
    
    public FromNodeLeftTuple(final LeftTuple leftTuple,
                             final RightTuple rightTuple,
                             final LeftTuple currentLeftChild,
                             final LeftTuple currentRightChild,
                             final Sink sink,
                             final boolean leftTupleMemoryEnabled) {
        super( leftTuple, 
               rightTuple, 
               currentLeftChild, 
               currentRightChild, 
               sink, 
               leftTupleMemoryEnabled );
    }

    @Override
    public Collection<Object> getAccumulatedObjects() {
        if (getContextObject() instanceof ContextOwner) {
            Collection<Object> result = new ArrayList<>();
            JavaAccumulatorFunctionContext accContext = ( (ContextOwner) getContextObject() ).getContext( JavaAccumulatorFunctionContext.class );
            if (accContext != null) {
                result.addAll( accContext.getAccumulatedObjects() );
            }
            if (getFirstChild().getRightParent() instanceof SubnetworkTuple) {
                LeftTuple leftParent = ( (SubnetworkTuple) getFirstChild().getRightParent() ).getLeftParent();
                result.addAll( leftParent.getAccumulatedObjects() );
            }
            return result;
        }
        return Collections.emptyList();
    }
}

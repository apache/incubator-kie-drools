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

package org.drools.spi;

import java.io.Externalizable;
import java.util.LinkedList;

import org.drools.FactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.WorkingMemoryAction;
import org.drools.core.util.ObjectHashSet;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.rule.EntryPoint;
import org.drools.rule.Rule;

public interface PropagationContext
    extends
    Externalizable,
    org.drools.runtime.rule.PropagationContext {

    public Rule getRuleOrigin();
    
    public FactHandle getFactHandleOrigin();

    public LeftTuple getLeftTupleOrigin();

    /**
     * Returns the offset of the fact that initiated this propagation
     * in the current propagation context. This attribute is mutable
     * as the same fact might have different offsets in different rules
     * or logical branches.
     * 
     * @return -1 for not set, and from 0 to the tuple length-1.
     */
    public int getOriginOffset();
    
    /**
     * Sets the origin offset to the given offset.
     * 
     * @param offset -1 to unset or from 0 to tuple length-1
     */
    public void setOriginOffset( int offset );

    public int getActiveActivations();

    public int getDormantActivations();

    public void releaseResources();

    public EntryPoint getEntryPoint();
    
    /** When L&R unlinking is active, we need to keep 
     * track of the OTN that triggered this propagation. */
    public void setCurrentPropagatingOTN(ObjectTypeNode otn);

    public boolean isPropagating(ObjectTypeNode otn);

    public boolean shouldPropagateAll();

    public void setShouldPropagateAll(Object node);

    /** Keeps a list of nodes to which a propagation attempt fail 
     *  because the node was unlinked. */
    public ObjectHashSet getPropagationAttemptsMemory();
    
    public LinkedList<WorkingMemoryAction> getQueue1();

    public LinkedList<WorkingMemoryAction> getQueue2();    

    public void evaluateActionQueue(InternalWorkingMemory workingMemory); 

}

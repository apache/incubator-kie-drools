/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.spi;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.EntryPointId;
import org.drools.core.util.bitmask.BitMask;

import java.io.Externalizable;
import java.util.LinkedList;

public interface PropagationContext extends Externalizable {

    enum Type {
        INSERTION, DELETION, MODIFICATION, RULE_ADDITION, RULE_REMOVAL, EXPIRATION
    }

    long getPropagationNumber();


    Type getType();

    RuleImpl getRuleOrigin();

    TerminalNode getTerminalNodeOrigin();

    /**
     * @return fact handle that was inserted, updated or retracted that created the PropagationContext
     */
    InternalFactHandle getFactHandle();
    void setFactHandle(InternalFactHandle factHandle);

    Tuple getLeftTupleOrigin();

    /**
     * Returns the offset of the fact that initiated this propagation
     * in the current propagation context. This attribute is mutable
     * as the same fact might have different offsets in different rules
     * or logical branches.
     * 
     * @return -1 for not set, and from 0 to the tuple length-1.
     */
    int getOriginOffset();
    
    /**
     * Sets the origin offset to the given offset.
     * 
     * @param offset -1 to unset or from 0 to tuple length-1
     */
    void setOriginOffset( int offset );

    void releaseResources();

    EntryPointId getEntryPoint();
    
    void addInsertAction(WorkingMemoryAction action);
    void removeInsertAction(WorkingMemoryAction action);

    LinkedList<WorkingMemoryAction> getQueue1();


    LinkedList<WorkingMemoryAction> getQueue2();

    void evaluateActionQueue(InternalWorkingMemory workingMemory);

    BitMask getModificationMask();
    PropagationContext adaptModificationMaskForObjectType(ObjectType type, InternalWorkingMemory workingMemory);
    void setModificationMask(BitMask mask);

    MarshallerReaderContext getReaderContext();

    void cleanReaderContext();

    void setEntryPoint(EntryPointId entryPoint);

    boolean isMarshalling();
    void setMarshalling( boolean marshalling );
}

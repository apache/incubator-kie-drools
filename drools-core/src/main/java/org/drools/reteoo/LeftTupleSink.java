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

package org.drools.reteoo;

import java.io.Externalizable;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

/**
 * Receiver of propagated <code>ReteTuple</code>s from a
 * <code>TupleSource</code>.
 *
 * @see LeftTupleSource
 */
public interface LeftTupleSink
    extends
    Externalizable,
    Sink {

    public short getType();
    
    /**
     * Assert a new <code>ReteTuple</code>.
     *
     * @param leftTuple
     *            The <code>ReteTuple</code> to propagate.
     * @param context
     *             The <code>PropagationContext</code> of the <code>WorkingMemory<code> action
     * @param workingMemory
     *            the <code>WorkingMemory</code> session.
     */
    void assertLeftTuple(LeftTuple leftTuple,
                         PropagationContext context,
                         InternalWorkingMemory workingMemory);

    void retractLeftTuple(LeftTuple leftTuple,
                          PropagationContext context,
                          InternalWorkingMemory workingMemory);

    public boolean isLeftTupleMemoryEnabled();

    public void setLeftTupleMemoryEnabled(boolean tupleMemoryEnabled);
    
    public void modifyLeftTuple(InternalFactHandle factHandle,
                                ModifyPreviousTuples modifyPreviousTuples,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory);
    
    public LeftTupleSource getLeftTupleSource();

    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory);
    
    public LeftTuple createLeftTuple(final InternalFactHandle factHandle,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled);
    
    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled);   
    
    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTupleSink sink);    
    
    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTuple currentLeftChild,
                                     LeftTuple currentRightChild,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled);   
    
    int getLeftInputOtnId();

    void setLeftInputOtnId(int leftInputOtnId);    
}

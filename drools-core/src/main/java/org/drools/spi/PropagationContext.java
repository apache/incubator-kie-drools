/**
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

import org.drools.reteoo.LeftTuple;
import org.drools.rule.EntryPoint;
import org.drools.rule.Rule;
import org.drools.FactHandle;

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
    

}

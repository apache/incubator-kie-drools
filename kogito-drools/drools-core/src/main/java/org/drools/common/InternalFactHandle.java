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

package org.drools.common;

import org.drools.FactHandle;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RightTuple;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

public interface InternalFactHandle
    extends
    FactHandle, Cloneable {
    public int getId();

    public long getRecency();

    public Object getObject();

    public void setObject(Object object);

    public void setEqualityKey(EqualityKey key);

    public EqualityKey getEqualityKey();

    public void setRecency(long recency);

    public void invalidate();
    
    public boolean isValid();
    
    public int getIdentityHashCode();

    public int getObjectHashCode();
    
    public boolean isDisconnected();
    
    /**
     * Returns true if this FactHandle represents
     * and Event or false if this FactHandle represents
     * a regular Fact
     * 
     * @return
     */
    public boolean isEvent();
    
    public RightTuple getFirstRightTuple();

    public void setFirstRightTuple(RightTuple rightTuple);

    public RightTuple getLastRightTuple();

    public void setLastRightTuple(RightTuple rightTuple);
        
    public void setFirstLeftTuple(LeftTuple leftTuple);
    
    public LeftTuple getFirstLeftTuple();
    
    public void setLastLeftTuple(LeftTuple leftTuple);
    
    public LeftTuple getLastLeftTuple();
    
    public WorkingMemoryEntryPoint getEntryPoint();
    
    public void setEntryPoint( WorkingMemoryEntryPoint ep );
    
    public InternalFactHandle clone();
    
    public String toExternalForm();
    
    public String toTupleTree( int indent );
    
    public void disconnect();
    
}

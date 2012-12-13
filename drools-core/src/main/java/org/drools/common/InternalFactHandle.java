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
import org.kie.runtime.rule.SessionEntryPoint;

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

    public RightTuple getLastRightTuple();

    public LeftTuple getFirstLeftTuple();
    
    public LeftTuple getLastLeftTuple();
    
    public SessionEntryPoint getEntryPoint();
    
    public void setEntryPoint( SessionEntryPoint ep );
    
    public InternalFactHandle clone();
    
    public String toExternalForm();
    
    public String toTupleTree( int indent );
    
    public void disconnect();

    public void addLastLeftTuple( LeftTuple leftTuple );

    public void removeLeftTuple( LeftTuple leftTuple );

    public void clearLeftTuples();

    public void clearRightTuples();

    public void addFirstRightTuple( RightTuple rightTuple );

    public void addLastRightTuple( RightTuple rightTuple );

    public void removeRightTuple( RightTuple rightTuple );
    
    public InternalFactHandle quickClone();
    
}

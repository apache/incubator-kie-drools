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

package org.drools.core.common;

import org.drools.core.factmodel.traits.TraitTypeEnum;
import org.kie.api.runtime.rule.FactHandle;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;
import org.kie.api.runtime.rule.EntryPoint;

public interface InternalFactHandle
    extends
    FactHandle, Cloneable {
    int getId();

    long getRecency();

    Object getObject();

    String getObjectClassName();

    void setObject(Object object);

    void setEqualityKey(EqualityKey key);

    EqualityKey getEqualityKey();

    void setRecency(long recency);

    void invalidate();
    
    boolean isValid();
    
    int getIdentityHashCode();

    int getObjectHashCode();
    
    boolean isDisconnected();
    
    /**
     * Returns true if this FactHandle represents
     * and Event or false if this FactHandle represents
     * a regular Fact
     * 
     * @return
     */
    boolean isEvent();

    boolean isTraitOrTraitable();

    boolean isTraitable();

    boolean isTraiting();

    TraitTypeEnum getTraitType();
    
    RightTuple getFirstRightTuple();

    RightTuple getLastRightTuple();

    LeftTuple getFirstLeftTuple();
    
    LeftTuple getLastLeftTuple();
    
    EntryPoint getEntryPoint();
    
    void setEntryPoint( EntryPoint ep );
    
    InternalFactHandle clone();
    
    String toExternalForm();
    
    String toTupleTree( int indent );
    
    void disconnect();

    void addFirstLeftTuple(LeftTuple leftTuple);

    void addLastLeftTuple( LeftTuple leftTuple );

    void addLeftTupleInPosition( LeftTuple leftTuple );

    void setFirstLeftTuple(LeftTuple leftTuple);

    void setLastLeftTuple( LeftTuple leftTuple );

    void removeLeftTuple( LeftTuple leftTuple );

    void clearLeftTuples();

    void clearRightTuples();

    void addFirstRightTuple( RightTuple rightTuple );

    void addLastRightTuple( RightTuple rightTuple );

    void addRightTupleInPosition( RightTuple rightTuple );

    void removeRightTuple( RightTuple rightTuple );
    
    InternalFactHandle quickClone();

    public boolean isNegated();

    public void setNegated(boolean negated);
}

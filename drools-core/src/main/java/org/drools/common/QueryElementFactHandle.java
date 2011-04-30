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

package org.drools.common;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RightTuple;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

@XmlAccessorType(XmlAccessType.NONE)
public class QueryElementFactHandle
    implements
    InternalFactHandle {
    private Object object;

    protected QueryElementFactHandle() {}

    public QueryElementFactHandle(Object object) {
        this.object = object;
    }

    public int getId() {
        return -1;
    }

    public int getIdentityHashCode() {
        return this.object.hashCode();
    }

    public int getObjectHashCode() {
        return this.object.hashCode();
    }

    public long getRecency() {
        return 0;
    }

    public LeftTuple getLastLeftTuple() {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public Object getObject() {
        if ( this.object != null ) {
            return this.object;
        }
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public WorkingMemoryEntryPoint getEntryPoint() {
        return null;
        //throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public EqualityKey getEqualityKey() {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public RightTuple getRightTuple() {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public void invalidate() {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public boolean isEvent() {
        return false;
    }

    public boolean isValid() {
        return true;
    }

    public void setEntryPoint(WorkingMemoryEntryPoint ep) {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public void setEqualityKey(EqualityKey key) {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public void setFirstLeftTuple(LeftTuple leftTuple) {
    }

    public void setObject(Object object) {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public void setRecency(long recency) {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public void setRightTuple(RightTuple rightTuple) {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public DefaultFactHandle clone() {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public String toExternalForm() {
        return "QueryElementFactHandl: " + this.object;
    }
    
    @XmlAttribute(name="external-form")
    public String getExternalForm() {
        return toExternalForm();
    }

    
    public LeftTuple getFirstLeftTuple() {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    
    public RightTuple getFirstRightTuple() {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    
    public RightTuple getLastRightTuple() {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    
    public void setFirstRightTuple(RightTuple rightTuple) {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    
    public void setLastLeftTuple(LeftTuple leftTuple) {
    }

    
    public void setLastRightTuple(RightTuple rightTuple) {
        throw new UnsupportedOperationException( "DisonnectedFactHandle does not support this method" );
    }

    public String toTupleTree(int indent) {
        return null;
    }

    public boolean isDisconnected() {
        return true;
    }
    
    public String toString() {
        return "results: " + Arrays.asList( (Object[]) this.object ).toString();
    }

}

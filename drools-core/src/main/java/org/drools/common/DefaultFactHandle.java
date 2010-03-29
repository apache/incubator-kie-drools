package org.drools.common;

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

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.drools.FactHandle;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RightTuple;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

/**
 * Implementation of <code>FactHandle</code>.
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 */
@XmlRootElement(name="fact-handle")
@XmlAccessorType(XmlAccessType.NONE)
public class DefaultFactHandle
    implements
    InternalFactHandle {
    // ----------------------------------------------------------------------
    // Instance members
    // ----------------------------------------------------------------------

    /**
     *
     */
    private static final long       serialVersionUID = 400L;
    /** Handle id. */
    private int                     id;
    private long                    recency;
    private Object                  object;
    private EqualityKey             key;
    private int                     objectHashCode;
    
    public RightTuple              firstRightTuple;
    public RightTuple              lastRightTuple;
    
    public LeftTuple                firstLeftTuple;
    public LeftTuple                lastLeftTuple;
    private WorkingMemoryEntryPoint entryPoint;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    public DefaultFactHandle() {
    }

    public DefaultFactHandle(final int id,
                             final Object object) {
        this( id,
              object,
              id );
    }

    /**
     * Construct.
     *
     * @param id
     *            Handle id.
     */
    public DefaultFactHandle(final int id,
                             final Object object,
                             final long recency) {
        this.id = id;
        this.recency = recency;
        this.object = object;
        this.objectHashCode = object.hashCode();
    }
    
    public DefaultFactHandle(final int id,
                             final int objectHashCode,
                             final long recency) {
        this.id = id;
        this.recency = recency;
        this.objectHashCode = objectHashCode;
    }
        
    
    

    // ----------------------------------------------------------------------
    // Instance members
    // ----------------------------------------------------------------------

    /**
     * @see Object
     */
    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || !(object instanceof DefaultFactHandle) ) {
            return false;
        }

        return this.id == ((DefaultFactHandle) object).id;
    }

    public int getObjectHashCode() {
        return this.objectHashCode;
    }
    
    public int getIdentityHashCode() {
        return System.identityHashCode( this.object );
    }    
    
    protected void setObjectHashCode( int hashCode ) {
        this.objectHashCode = hashCode;
    }

    /**
     * @see Object
     */
    public int hashCode() {
        return this.id;
    }

    /**
     * format_version:id:identity:hashcode:recency
     * 
     * @see FactHandle
     */
    public String toExternalForm() {
        return "0:" + this.id + ":" + getIdentityHashCode() + ":" + getObjectHashCode() + ":" + getRecency();
    }

    @XmlAttribute(name="external-form")
    public String getExternalForm() {
    	return toExternalForm();
    }
    
    /**
     * @see Object
     */
    public String toString() {
        return "[fact " + toExternalForm() + ":" + this.object + "]";
    }

    public long getRecency() {
        return this.recency;
    }

    public void setRecency(final long recency) {
        this.recency = recency;
    }

    public int getId() {
        return this.id;
    }

    public void invalidate() {
        this.id = -1;
        this.object = null;
        this.entryPoint = null;
    }

    public boolean isValid() {
        return ( this.id != -1 );
    }
    
    public Object getObject() {
        return this.object;
    }

    public void setObject(final Object object) {
        this.object = object;
    }

    /**
     * @return the key
     */
    public EqualityKey getEqualityKey() {
        return this.key;
    }

    /**
     * @param key the key to set
     */
    public void setEqualityKey(final EqualityKey key) {
        this.key = key;
    }

    /**
     * Always returns false, since the DefaultFactHandle is
     * only used for regular Facts, and not for Events
     */
    public boolean isEvent() {
        return false;
    }

    public RightTuple getFirstRightTuple() {
        return this.firstRightTuple;
    }

    public void setFirstRightTuple(RightTuple firstRightTuple) {
        this.firstRightTuple = firstRightTuple;
    }
    
    public RightTuple getLastRightTuple() {
        return this.lastRightTuple;
    }

    public void setLastRightTuple(RightTuple lastRightTuple) {
        this.lastRightTuple = lastRightTuple;
    }    

    public void setFirstLeftTuple(LeftTuple firstLeftTuple) {
        this.firstLeftTuple = firstLeftTuple;
    }

    public LeftTuple getFirstLeftTuple() {
        return this.firstLeftTuple;
    }
    
    public void setLastLeftTuple(LeftTuple lastLeftTuple) {
        this.lastLeftTuple = lastLeftTuple;
    }

    public LeftTuple getLastLeftTuple() {
        return this.lastLeftTuple;
    }

    public WorkingMemoryEntryPoint getEntryPoint() {
        return entryPoint;
    }

    public void setEntryPoint(WorkingMemoryEntryPoint sourceNode) {
        this.entryPoint = sourceNode;
    }
    
    public DefaultFactHandle clone() {
        DefaultFactHandle clone =  new DefaultFactHandle(this.id, this.object, this.recency);
        clone.entryPoint = this.entryPoint;
        clone.key = this.key;
        clone.firstLeftTuple = this.firstLeftTuple;
        clone.lastLeftTuple = this.lastLeftTuple;
        
        clone.firstRightTuple = this.firstRightTuple;
        clone.lastRightTuple = this.lastRightTuple;
        
        clone.objectHashCode = this.objectHashCode;
        return clone;
    }

    public String toTupleTree(int indent) {
        StringBuilder buf = new StringBuilder();
        char[] spaces = new char[indent];
        Arrays.fill( spaces, ' ' );
        String istr = new String( spaces );
        buf.append( istr );
        buf.append( this.toExternalString() );
        buf.append( "\n" );
        for( LeftTuple leftTuple = this.firstLeftTuple; leftTuple != null; leftTuple = leftTuple.getLeftParentNext() ) {
            buf.append( leftTuple.toTupleTree( indent+4 ) );
        }
        return buf.toString();
    }

    private Object toExternalString() {
        return "[F:"+this.getId()+" first="+System.identityHashCode( firstLeftTuple )+" last="+System.identityHashCode( lastLeftTuple )+" ]";
    }
}

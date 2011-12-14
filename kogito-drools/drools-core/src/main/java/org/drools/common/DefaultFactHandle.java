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

import java.io.Serializable;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.drools.FactHandle;
import org.drools.core.util.StringUtils;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RightTuple;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

/**
 * Implementation of <code>FactHandle</code>.
 */
@XmlRootElement(name = "fact-handle")
@XmlAccessorType(XmlAccessType.NONE)
public class DefaultFactHandle
                              implements
                              InternalFactHandle, Serializable {

    // ----------------------------------------------------------------------
    // Instance members
    // ----------------------------------------------------------------------

    private static final long       serialVersionUID = 510l;
    /** Handle id. */
    private int                     id;
    private long                    recency;
    private Object                  object;
    private EqualityKey             key;
    private int                     objectHashCode;
    private int                     identityHashCode;

    private RightTuple              firstRightTuple;
    private RightTuple              lastRightTuple;

    private LeftTuple               firstLeftTuple;
    private LeftTuple               lastLeftTuple;

    private WorkingMemoryEntryPoint entryPoint;

    private boolean                 disconnected;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    public DefaultFactHandle() {
    }

    public DefaultFactHandle(final int id,
            final Object object) {
        // this is only used by tests, left as legacy as so many test rely on it.
        this( id,
              object,
              id,
              null );
    }

    /**
     * Construct.
     *
     * @param id
     *            Handle id.
     */
    public DefaultFactHandle(final int id,
            final Object object,
            final long recency,
            final WorkingMemoryEntryPoint wmEntryPoint) {
        this( id, determineIdentityHashCode( object ), object, recency, wmEntryPoint );
    }

    public DefaultFactHandle(final int id,
            final int identityHashCode,
            final Object object,
            final long recency,
            final WorkingMemoryEntryPoint wmEntryPoint) {
        this.id = id;
        this.entryPoint = wmEntryPoint;
        this.recency = recency;
        this.object = object;
        this.objectHashCode = ( object != null ) ? object.hashCode() : 0;
        this.identityHashCode = identityHashCode;
    }

    public DefaultFactHandle(int id,
            String wmEntryPointId,
            int identityHashCode,
            int objectHashCode,
            long recency,
            Object object) {
        this.id = id;
        this.entryPoint = ( wmEntryPointId == null ) ? null : new DisconnectedWorkingMemoryEntryPoint( wmEntryPointId );
        this.identityHashCode = identityHashCode;
        this.objectHashCode = objectHashCode;
        this.recency = recency;
        this.object = object;
        this.disconnected = true;
    }

    public DefaultFactHandle(String externalFormat) {
        createFromExternalFormat( externalFormat );
    }

    // ----------------------------------------------------------------------
    // Instance members
    // ----------------------------------------------------------------------

    /**
     * @see Object
     */
    public boolean equals( final Object object ) {
        if (this == object) {
            return true;
        }

        if (object == null || !( object instanceof DefaultFactHandle )) {
            return false;
        }

        return this.id == ( (DefaultFactHandle) object ).id;
    }

    public void disconnect() {
        this.key = null;
        this.firstLeftTuple = null;
        this.firstRightTuple = null;
        this.lastLeftTuple = null;
        this.lastRightTuple = null;
        this.entryPoint = ( this.entryPoint == null ) ? null
                                                     : new DisconnectedWorkingMemoryEntryPoint(
                                                                                                this.entryPoint.getEntryPointId() );
        this.disconnected = true;
    }

    public boolean isDisconnected() {
        return disconnected;
    }

    public int getObjectHashCode() {
        return this.objectHashCode;
    }

    public int getIdentityHashCode() {
        return this.identityHashCode;
    }

    public static int determineIdentityHashCode( Object object ) {
        return System.identityHashCode( object );
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
        return "0:" + this.id +
               ":" +
               getIdentityHashCode() +
               ":" +
               getObjectHashCode() +
               ":" +
               getRecency() +
               ":" +
               ( ( this.entryPoint != null ) ? this.entryPoint.getEntryPointId() : "null" );
    }

    @XmlAttribute(name = "external-form")
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

    public void setRecency( final long recency ) {
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

    public void setObject( final Object object ) {
        this.object = object;
        this.objectHashCode = ( object != null ) ? object.hashCode() : 0;
        this.identityHashCode = determineIdentityHashCode( object );
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
    public void setEqualityKey( final EqualityKey key ) {
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

    protected void setFirstRightTuple( RightTuple firstRightTuple ) {
        this.firstRightTuple = firstRightTuple;
    }

    public RightTuple getLastRightTuple() {
        return this.lastRightTuple;
    }

    protected void setLastRightTuple( RightTuple lastRightTuple ) {
        this.lastRightTuple = lastRightTuple;
    }

    protected void setFirstLeftTuple( LeftTuple firstLeftTuple ) {
        this.firstLeftTuple = firstLeftTuple;
    }

    public LeftTuple getFirstLeftTuple() {
        return this.firstLeftTuple;
    }

    protected void setLastLeftTuple( LeftTuple lastLeftTuple ) {
        this.lastLeftTuple = lastLeftTuple;
    }

    public LeftTuple getLastLeftTuple() {
        return this.lastLeftTuple;
    }

    public WorkingMemoryEntryPoint getEntryPoint() {
        return entryPoint;
    }

    public void setEntryPoint( WorkingMemoryEntryPoint sourceNode ) {
        this.entryPoint = sourceNode;
    }
    
    public void addFirstLeftTuple( LeftTuple leftTuple ) {
        LeftTuple previous = this.getFirstLeftTuple();
        if ( previous == null ) {
            // no other LeftTuples, just add.
            leftTuple.setLeftParentPrevious( null );
            leftTuple.setLeftParentNext( null );
            setFirstLeftTuple( leftTuple );
            setLastLeftTuple( leftTuple );
        } else {
            leftTuple.setLeftParentPrevious( null );
            leftTuple.setLeftParentNext( previous );
            previous.setLeftParentPrevious( leftTuple );
            setFirstLeftTuple( leftTuple );
        }
    }
    
    public void addLastLeftTuple( LeftTuple leftTuple ) {
        LeftTuple previous = this.getLastLeftTuple();
        if ( previous == null ) {
            // no other LeftTuples, just add.
            leftTuple.setLeftParentPrevious( null );
            leftTuple.setLeftParentNext( null );
            setFirstLeftTuple( leftTuple );
            setLastLeftTuple( leftTuple );
        } else {
            leftTuple.setLeftParentPrevious( previous );
            leftTuple.setLeftParentNext( null );
            previous.setLeftParentNext( leftTuple );
            setLastLeftTuple( leftTuple );
        }
    }
    
    public void removeLeftTuple( LeftTuple leftTuple ) {
        LeftTuple previous = leftTuple.getLeftParentPrevious();
        LeftTuple next = leftTuple.getLeftParentNext();
        
        if ( previous != null && next != null ) {
            // remove  from middle
            previous.setLeftParentNext( next );
            next.setLeftParentPrevious( previous );
        } else if ( next != null ) {
            // remove from first
            next.setLeftParentPrevious( null );
            setFirstLeftTuple( next );
        } else if ( previous != null ) {
            // remove from end
            previous.setLeftParentNext( null );
            setLastLeftTuple( previous );
        } else {
            // single remaining item, no previous or next
            setFirstLeftTuple( null );
            setLastLeftTuple( null );
        }
        leftTuple.setLeftParentPrevious( null );
        leftTuple.setLeftParentNext( null );
    }
    
    public void addFirstRightTuple( RightTuple rightTuple ) {
        RightTuple previousFirst = getFirstRightTuple();
        setFirstRightTuple( rightTuple );
        if ( previousFirst == null ) {
            rightTuple.setHandlePrevious( null );
            rightTuple.setHandleNext( null );
            setLastRightTuple( rightTuple );
        } else {
            rightTuple.setHandlePrevious( null );
            rightTuple.setHandleNext( previousFirst );
            previousFirst.setHandlePrevious( rightTuple );
        }
    }

    public void addLastRightTuple( RightTuple rightTuple ) {
        RightTuple previousLast = getLastRightTuple();
        if( previousLast == null ){
            rightTuple.setHandlePrevious( null );
            rightTuple.setHandleNext( null );
            setFirstRightTuple( rightTuple );
            setLastRightTuple( rightTuple );
        } else {
            rightTuple.setHandlePrevious( previousLast );
            rightTuple.setHandleNext( null );
            previousLast.setHandleNext( rightTuple );
            setLastRightTuple( rightTuple );
        }
    }
    
    public void removeRightTuple( RightTuple rightTuple ) {
        RightTuple previous = rightTuple.getHandlePrevious();
        RightTuple next = rightTuple.getHandleNext();

        if ( previous != null && next != null ) {
            // remove  from middle
            previous.setHandleNext( next );
            next.setHandlePrevious( previous );
        } else if ( next != null ) {
            // remove from first
            next.setHandlePrevious( null );
            setFirstRightTuple( next );
        } else if ( previous != null ) {
            // remove from end
            previous.setHandleNext( null );
            setLastRightTuple( previous );
        } else {
            // single remaining item, no previous or next
            setFirstRightTuple( null );
            setLastRightTuple( null );
        }
        rightTuple.setHandlePrevious( null );
        rightTuple.setHandleNext( null );
    }

    public void clearLeftTuples() {
        setFirstLeftTuple( null );
        setLastLeftTuple( null );
    }

    public void clearRightTuples() {
        setFirstRightTuple( null );
        setLastRightTuple( null );
    }

    public DefaultFactHandle clone() {
        DefaultFactHandle clone = new DefaultFactHandle( this.id, this.object, this.recency, this.entryPoint );
        clone.key = this.key;
        clone.firstLeftTuple = this.firstLeftTuple;
        clone.lastLeftTuple = this.lastLeftTuple;

        clone.firstRightTuple = this.firstRightTuple;
        clone.lastRightTuple = this.lastRightTuple;

        clone.objectHashCode = this.objectHashCode;
        clone.identityHashCode = System.identityHashCode( clone.object );
        clone.disconnected = this.disconnected;
        return clone;
    }

    public String toTupleTree( int indent ) {
        StringBuilder buf = new StringBuilder();
        char[] spaces = new char[indent];
        Arrays.fill( spaces,
                     ' ' );
        String istr = new String( spaces );
        buf.append( istr );
        buf.append( this.toExternalString() );
        buf.append( "\n" );
        for (LeftTuple leftTuple = this.firstLeftTuple; leftTuple != null; leftTuple = leftTuple.getLeftParentNext()) {
            buf.append( leftTuple.toTupleTree( indent + 4 ) );
        }
        return buf.toString();
    }

    private Object toExternalString() {
        return "[F:" + this.getId() +
               " first=" +
               System.identityHashCode( firstLeftTuple ) +
               " last=" +
               System.identityHashCode( lastLeftTuple ) +
               " ]";
    }

    private void createFromExternalFormat( String externalFormat ) {
        String[] elements = externalFormat.split( ":" );
        if (elements.length != 6) {
            throw new IllegalArgumentException( "externalFormat did not have enough elements" );
        }

        this.id = Integer.parseInt( elements[1] );
        this.identityHashCode = Integer.parseInt( elements[2] );
        this.objectHashCode = Integer.parseInt( elements[3] );
        this.recency = Long.parseLong( elements[4] );
        this.entryPoint = ( StringUtils.isEmpty( elements[5] ) || "null".equals( elements[5].trim() ) ) ? null
                                                                                                       : new DisconnectedWorkingMemoryEntryPoint(
                                                                                                                                                  elements[5].trim() );
        this.disconnected = true;
    }

}

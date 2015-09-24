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

import org.drools.core.base.TraitHelper;
import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.TraitTypeEnum;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.drools.core.util.StringUtils;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;

/**
 * Implementation of <code>FactHandle</code>.
 */
@XmlRootElement(name = "fact-handle")
@XmlAccessorType(XmlAccessType.NONE)
public class DefaultFactHandle extends AbstractBaseLinkedListNode<DefaultFactHandle>
                              implements
                              InternalFactHandle {

    // ----------------------------------------------------------------------
    // Instance members
    // ----------------------------------------------------------------------

    private static final long       serialVersionUID = 510l;
    /** Handle id. */

    static final String     FACT_FORMAT_VERSION = "0";

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

    private EntryPoint entryPoint;

    private boolean                 disconnected;

    private TraitTypeEnum           traitType;

    private boolean                 valid = true;

    private boolean                 negated;

    private String                  objectClassName;

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
              null,
              false);
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
                             final EntryPoint wmEntryPoint) {
        this( id, determineIdentityHashCode( object ), object, recency, wmEntryPoint, false );
    }

    public DefaultFactHandle(final int id,
                             final Object object,
                             final long recency,
                             final EntryPoint wmEntryPoint,
                             final boolean isTraitOrTraitable ) {
        this( id, determineIdentityHashCode( object ), object, recency, wmEntryPoint, isTraitOrTraitable );
    }

    public DefaultFactHandle(final int id,
                             final int identityHashCode,
                             final Object object,
                             final long recency,
                             final EntryPoint wmEntryPoint,
                             final boolean isTraitOrTraitable ) {
        this.id = id;
        this.entryPoint = wmEntryPoint;
        this.recency = recency;
        setObject( object );
        this.identityHashCode = identityHashCode;
        this.traitType = isTraitOrTraitable ? determineTraitType() : TraitTypeEnum.NON_TRAIT;
    }

    public DefaultFactHandle(int id,
            String wmEntryPointId,
            int identityHashCode,
            int objectHashCode,
            long recency,
            Object object) {
        this.id = id;
        this.entryPoint = ( wmEntryPointId == null ) ? null : new DisconnectedWorkingMemoryEntryPoint( wmEntryPointId );
        this.recency = recency;
        setObject( object );
        this.identityHashCode = identityHashCode;
        this.objectHashCode = objectHashCode;
        this.disconnected = true;
        this.traitType = TraitTypeEnum.NON_TRAIT;
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

    public boolean isNegated() {
        return negated;
    }

    public void setNegated(boolean negated) {
        this.negated = negated;
    }

    @Override
    public <K> K as( Class<K> klass ) throws ClassCastException {
        if ( klass.isAssignableFrom( object.getClass() ) ) {
            return (K) object;
        } else if ( this.isTraitOrTraitable() ) {
            K k = TraitHelper.extractTrait( this, klass );
            if ( k != null ) {
                return  k;
            }
        }
        throw new ClassCastException( "The Handle's Object can't be cast to " + klass );
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
    public final String toExternalForm() {
        return getFormatVersion() + ":" + this.id +
               ":" +
               getIdentityHashCode() +
               ":" +
               getObjectHashCode() +
               ":" +
               getRecency() +
               ":" +
               ( ( this.entryPoint != null ) ? this.entryPoint.getEntryPointId() : "null" ) +
               ":" +
               this.traitType.name() +
               ":" +
               this.objectClassName;
    }

    protected String getFormatVersion() {
        return FACT_FORMAT_VERSION;
    }

    @XmlAttribute(name = "external-form")
    public String getExternalForm() {
        return toExternalForm();
    }

    public void setExternalForm(String externalForm) {
        populateFactHandleFromExternalForm( externalForm, this );
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
        valid = false;
    }

    public boolean isValid() {
        return valid;
    }

    public Object getObject() {
        return this.object;
    }

    public String getObjectClassName() {
        return this.objectClassName;
    }

    public void setObject( final Object object ) {
        this.object = object;
        if (object != null) {
            this.objectClassName = object.getClass().getName();
            this.objectHashCode = object.hashCode();
        } else {
            this.objectHashCode = 0;
        }

        if ( isTraitOrTraitable() ) {
            TraitTypeEnum newType = determineTraitType();
            if ( ! ( this.traitType == TraitTypeEnum.LEGACY_TRAITABLE && newType != TraitTypeEnum.LEGACY_TRAITABLE ) ) {
                this.identityHashCode = determineIdentityHashCode( object );
            } else {
                // we are replacing a non-traitable object with its proxy, so we need to preserve the identity hashcode
            }
            this.traitType = newType;
        } else {
            this.identityHashCode = determineIdentityHashCode( object );
        }
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

    public boolean isTraitOrTraitable() {
        return traitType != TraitTypeEnum.NON_TRAIT;
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

    public void setFirstLeftTuple( LeftTuple firstLeftTuple ) {
        this.firstLeftTuple = firstLeftTuple;
    }

    public LeftTuple getFirstLeftTuple() {
        return this.firstLeftTuple;
    }

    public void setLastLeftTuple( LeftTuple lastLeftTuple ) {
        this.lastLeftTuple = lastLeftTuple;
    }

    public LeftTuple getLastLeftTuple() {
        return this.lastLeftTuple;
    }

    public EntryPoint getEntryPoint() {
        return entryPoint;
    }

    public void setEntryPoint( EntryPoint sourceNode ) {
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

    public void addLeftTupleInPosition( LeftTuple leftTuple ) {
        ObjectTypeNode.Id otnId = leftTuple.getLeftTupleSink() == null ? null : leftTuple.getLeftTupleSink().getLeftInputOtnId();
        if (otnId == null) { // can happen only in tests
            addLastLeftTuple( leftTuple );
            return;
        }

        LeftTuple previous = this.getLastLeftTuple();
        if ( previous == null ) {
            // no other LeftTuples, just add.
            leftTuple.setLeftParentPrevious( null );
            leftTuple.setLeftParentNext( null );
            setFirstLeftTuple( leftTuple );
            setLastLeftTuple( leftTuple );
            return;
        } else if ( previous.getLeftTupleSink() == null || !otnId.before( previous.getLeftTupleSink().getLeftInputOtnId() ) ) {
            // the last LeftTuple comes before the new one so just add it at the end
            leftTuple.setLeftParentPrevious( previous );
            leftTuple.setLeftParentNext( null );
            previous.setLeftParentNext( leftTuple );
            setLastLeftTuple( leftTuple );
            return;
        }

        LeftTuple next = previous;
        previous = previous.getLeftParentPrevious();
        while (previous != null && otnId.before( previous.getLeftTupleSink().getLeftInputOtnId() ) ) {
            next = previous;
            previous = previous.getLeftParentPrevious();
        }
        leftTuple.setLeftParentNext( next );
        next.setLeftParentPrevious( leftTuple );
        leftTuple.setLeftParentPrevious( previous );
        if ( previous != null ) {
            previous.setLeftParentNext( leftTuple );
        } else {
            setFirstLeftTuple( leftTuple );
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

    public void addRightTupleInPosition( RightTuple rightTuple ) {
        ObjectTypeNode.Id otnId = rightTuple.getRightTupleSink() == null ? null : rightTuple.getRightTupleSink().getRightInputOtnId();
        if (otnId == null) { // can happen only in tests
            addLastRightTuple( rightTuple );
            return;
        }

        RightTuple previous = getLastRightTuple();
        if ( previous == null ) {
            // no other RightTuples, just add.
            rightTuple.setHandlePrevious( null );
            rightTuple.setHandleNext( null );
            setFirstRightTuple( rightTuple );
            setLastRightTuple( rightTuple );
            return;
        } else if ( previous.getRightTupleSink() == null || !otnId.before( previous.getRightTupleSink().getRightInputOtnId() ) ) {
            // the last RightTuple comes before the new one so just add it at the end
            rightTuple.setHandlePrevious( previous );
            rightTuple.setHandleNext( null );
            previous.setHandleNext( rightTuple );
            setLastRightTuple( rightTuple );
            return;
        }

        RightTuple next = previous;
        previous = previous.getHandlePrevious();
        while (previous != null && otnId.before( previous.getRightTupleSink().getRightInputOtnId() ) ) {
            next = previous;
            previous = previous.getHandlePrevious();
        }
        rightTuple.setHandleNext( next );
        next.setHandlePrevious( rightTuple );
        rightTuple.setHandlePrevious( previous );
        if ( previous != null ) {
            previous.setHandleNext( rightTuple );
        } else {
            setFirstRightTuple( rightTuple );
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

    public DefaultFactHandle quickClone() {
        DefaultFactHandle clone = new DefaultFactHandle( this.id, this.object, this.recency, this.entryPoint );
        clone.key = this.key;

        clone.objectHashCode = this.objectHashCode;
        clone.identityHashCode = this.identityHashCode;
        clone.disconnected = this.disconnected;
        clone.traitType = this.traitType;
        clone.negated = this.negated;
        return clone;
    }

    public void quickCloneUpdate(DefaultFactHandle clone) {
        clone.object = this.object;
        clone.recency  = this.recency;
        clone.key = this.key;

        clone.objectHashCode = this.objectHashCode;
        clone.identityHashCode = this.identityHashCode;
        clone.traitType = this.traitType;
        clone.disconnected = this.disconnected;
        clone.negated = this.negated;
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
		clone.traitType = this.traitType;
        clone.negated = this.negated;
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

    public static DefaultFactHandle createFromExternalFormat( String externalFormat ) {
        String[] elements = splitExternalForm( externalFormat );
        DefaultFactHandle handle;
        if (FACT_FORMAT_VERSION.equals( elements[0]) ) {
            handle = new DefaultFactHandle();
        } else if (EventFactHandle.EVENT_FORMAT_VERSION.equals( elements[0])) {
            handle = new EventFactHandle();
        } else {
            throw new RuntimeException( "Unknown fact handle version format: " + elements[0]);
        }
        populateFactHandleFromExternalForm( elements, handle );
        return handle;
    }

    private static String[] splitExternalForm( String externalFormat ) {
        String[] elements = externalFormat.split( ":" );
        if (elements.length < 6) {
            throw new IllegalArgumentException( "externalFormat did not have enough elements ["+externalFormat+"]" );
        }
        return elements;
    }

    private static void populateFactHandleFromExternalForm( String externalFormat, DefaultFactHandle handle ) {
        populateFactHandleFromExternalForm( splitExternalForm( externalFormat ), handle );
    }

    private static void populateFactHandleFromExternalForm( String[] elements, DefaultFactHandle handle ) {
        handle.id = Integer.parseInt( elements[1] );
        handle.identityHashCode = Integer.parseInt( elements[2] );
        handle.objectHashCode = Integer.parseInt( elements[3] );
        handle.recency = Long.parseLong( elements[4] );
        handle.entryPoint = ( StringUtils.isEmpty( elements[5] ) || "null".equals( elements[5].trim() ) ) ? null
                                                                                                       : new DisconnectedWorkingMemoryEntryPoint(
                elements[5].trim() );
        handle.disconnected = true;
        handle.traitType = elements.length > 6 ? TraitTypeEnum.valueOf( elements[6] ) : TraitTypeEnum.NON_TRAIT;
        handle.objectClassName = elements.length > 7 ? elements[7] : null;
    }

    private TraitTypeEnum determineTraitType() {
        if ( isTraitOrTraitable() ) {
            return TraitFactory.determineTraitType( object );
        } else {
            return TraitTypeEnum.NON_TRAIT;
        }
    }

    public boolean isTraitable() {
        return traitType == TraitTypeEnum.TRAITABLE || traitType == TraitTypeEnum.WRAPPED_TRAITABLE;
    }

    public boolean isTraiting() {
        return traitType == TraitTypeEnum.TRAIT;
    }

    public TraitTypeEnum getTraitType() {
        return traitType;
    }

}

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

package org.drools.core.common;

import org.drools.core.base.TraitHelper;
import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.TraitTypeEnum;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.spi.Tuple;
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.drools.core.util.StringUtils;
import org.kie.api.runtime.rule.FactHandle;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

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

    protected InternalWorkingMemoryEntryPoint entryPoint;

    private boolean                 disconnected;

    private TraitTypeEnum           traitType;

    private boolean                 valid = true;

    private boolean                 negated;

    private String                  objectClassName;

    protected LinkedTuples linkedTuples;

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
                             final InternalWorkingMemoryEntryPoint wmEntryPoint) {
        this( id, determineIdentityHashCode( object ), object, recency, wmEntryPoint, false );
    }

    public DefaultFactHandle(final int id,
                             final Object object,
                             final long recency,
                             final InternalWorkingMemoryEntryPoint wmEntryPoint,
                             final boolean isTraitOrTraitable ) {
        this( id, determineIdentityHashCode( object ), object, recency, wmEntryPoint, isTraitOrTraitable );
    }

    public DefaultFactHandle(final int id,
                             final int identityHashCode,
                             final Object object,
                             final long recency,
                             final InternalWorkingMemoryEntryPoint wmEntryPoint,
                             final boolean isTraitOrTraitable ) {
        this.id = id;
        setEntryPoint( wmEntryPoint );
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
        setEntryPoint( ( wmEntryPointId == null ) ? null : new DisconnectedWorkingMemoryEntryPoint( wmEntryPointId ) );
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
        return this == object || ( object instanceof DefaultFactHandle && this.id == ( (DefaultFactHandle) object ).id );
    }

    public void disconnect() {
        this.key = null;
        this.linkedTuples = null;
        this.entryPoint = ( this.entryPoint == null ) ?
                          null :
                          new DisconnectedWorkingMemoryEntryPoint( this.entryPoint.getEntryPointId() );
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

    public InternalWorkingMemoryEntryPoint getEntryPoint() {
        return entryPoint;
    }

    private void setEntryPoint( InternalWorkingMemoryEntryPoint entryPoint ) {
        this.entryPoint = entryPoint;
        setLinkedTuples( entryPoint != null ? entryPoint.getKnowledgeBase() : null );
    }

    private void setLinkedTuples( InternalKnowledgeBase kbase ) {
        linkedTuples = kbase != null && kbase.getConfiguration().isMultithreadEvaluation() ?
                       new CompositeLinkedTuples() :
                       new SingleLinkedTuples();
    }

    public void addFirstLeftTuple( LeftTuple leftTuple ) {
        linkedTuples.addFirstLeftTuple( leftTuple );
    }

    public void addLastLeftTuple( LeftTuple leftTuple ) {
        linkedTuples.addLastLeftTuple( leftTuple );
    }

    public void addTupleInPosition( Tuple tuple ) {
        linkedTuples.addTupleInPosition( tuple );
    }

    public void removeLeftTuple( LeftTuple leftTuple ) {
        linkedTuples.removeLeftTuple( leftTuple );
    }

    public void addFirstRightTuple( RightTuple rightTuple ) {
        linkedTuples.addFirstRightTuple( rightTuple );
    }

    public void addLastRightTuple( RightTuple rightTuple ) {
        linkedTuples.addLastRightTuple( rightTuple );
    }

    public void removeRightTuple( RightTuple rightTuple ) {
        linkedTuples.removeRightTuple( rightTuple );
    }

    public void clearLeftTuples() {
        linkedTuples.clearLeftTuples();
    }

    public void clearRightTuples() {
        linkedTuples.clearRightTuples();
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
        clone.linkedTuples = this.linkedTuples.clone();

        clone.objectHashCode = this.objectHashCode;
        clone.identityHashCode = System.identityHashCode( clone.object );
        clone.disconnected = this.disconnected;
		clone.traitType = this.traitType;
        clone.negated = this.negated;
        return clone;
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
        handle.setEntryPoint( ( StringUtils.isEmpty( elements[5] ) || "null".equals( elements[5].trim() ) ) ?
                            null :
                            new DisconnectedWorkingMemoryEntryPoint( elements[5].trim() ) );
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

    public boolean isExpired() {
        return false;
    }

    public boolean isPendingRemoveFromStore() {
        return false;
    }

    protected static class SingleLinkedTuples implements LinkedTuples {
        private RightTuple firstRightTuple;
        private RightTuple lastRightTuple;

        private LeftTuple  firstLeftTuple;
        private LeftTuple  lastLeftTuple;

        public SingleLinkedTuples clone() {
            SingleLinkedTuples clone = new SingleLinkedTuples();
            clone.firstLeftTuple = this.firstLeftTuple;
            clone.lastLeftTuple = this.lastLeftTuple;
            clone.firstRightTuple = this.firstRightTuple;
            clone.lastRightTuple = this.lastRightTuple;
            return clone;
        }

        public void addFirstLeftTuple( LeftTuple leftTuple ) {
            LeftTuple previous = firstLeftTuple;
            if ( previous == null ) {
                // no other LeftTuples, just add.
                leftTuple.setHandlePrevious( null );
                leftTuple.setHandleNext( null );
                firstLeftTuple = leftTuple;
                lastLeftTuple = leftTuple;
            } else {
                leftTuple.setHandlePrevious( null );
                leftTuple.setHandleNext( previous );
                previous.setHandlePrevious( leftTuple );
                firstLeftTuple = leftTuple;
            }
        }

        public void addLastLeftTuple( LeftTuple leftTuple ) {
            LeftTuple previous = lastLeftTuple;
            if ( previous == null ) {
                // no other LeftTuples, just add.
                leftTuple.setHandlePrevious( null );
                leftTuple.setHandleNext( null );
                firstLeftTuple = leftTuple;
                lastLeftTuple = leftTuple;
            } else {
                leftTuple.setHandlePrevious( previous );
                leftTuple.setHandleNext( null );
                previous.setHandleNext( leftTuple );
                lastLeftTuple = leftTuple;
            }
        }

        public void addTupleInPosition( Tuple tuple ) {
            boolean left = tuple instanceof LeftTuple;
            ObjectTypeNode.Id otnId = tuple.getInputOtnId();
            if (otnId == null) { // can happen only in tests
                addLastTuple( tuple, left );
                return;
            }

            Tuple previous = left ? lastLeftTuple : lastRightTuple;
            if ( previous == null ) {
                // no other LeftTuples, just add.
                tuple.setHandlePrevious( null );
                tuple.setHandleNext( null );
                setFirstTuple( tuple, left );
                setLastTuple( tuple, left );
                return;
            } else if ( previous.getTupleSink() == null || !otnId.before( previous.getInputOtnId() ) ) {
                // the last LeftTuple comes before the new one so just add it at the end
                tuple.setHandlePrevious( previous );
                tuple.setHandleNext( null );
                previous.setHandleNext( tuple );
                setLastTuple( tuple, left );
                return;
            }

            Tuple next = previous;
            previous = previous.getHandlePrevious();
            while (previous != null && otnId.before( previous.getInputOtnId() ) ) {
                next = previous;
                previous = previous.getHandlePrevious();
            }
            tuple.setHandleNext( next );
            next.setHandlePrevious( tuple );
            tuple.setHandlePrevious( previous );
            if ( previous != null ) {
                previous.setHandleNext( tuple );
            } else {
                setFirstTuple( tuple, left );
            }
        }

        private void addLastTuple(Tuple tuple, boolean left) {
            if (left) {
                addLastLeftTuple( ( (LeftTuple) tuple ) );
            } else {
                addLastRightTuple( ( (RightTuple) tuple ) );
            }
        }

        private void setFirstTuple(Tuple tuple, boolean left) {
            if (left) {
                firstLeftTuple = ( (LeftTuple) tuple );
            } else {
                firstRightTuple = ( (RightTuple) tuple );
            }
        }

        private void setLastTuple(Tuple tuple, boolean left) {
            if (left) {
                lastLeftTuple = ( (LeftTuple) tuple );
            } else {
                lastRightTuple = ( (RightTuple) tuple );
            }
        }

        public void removeLeftTuple( LeftTuple leftTuple ) {
            LeftTuple previous = leftTuple.getHandlePrevious();
            LeftTuple next = leftTuple.getHandleNext();

            if ( previous != null && next != null ) {
                // remove  from middle
                previous.setHandleNext( next );
                next.setHandlePrevious( previous );
            } else if ( next != null ) {
                // remove from first
                next.setHandlePrevious( null );
                firstLeftTuple = next;
            } else if ( previous != null ) {
                // remove from end
                previous.setHandleNext( null );
                lastLeftTuple = previous;
            } else {
                // single remaining item, no previous or next
                firstLeftTuple = null;
                lastLeftTuple = null;
            }
            leftTuple.setHandlePrevious( null );
            leftTuple.setHandleNext( null );
        }

        public void addFirstRightTuple( RightTuple rightTuple ) {
            RightTuple previousFirst = firstRightTuple;
            firstRightTuple = rightTuple;
            if ( previousFirst == null ) {
                rightTuple.setHandlePrevious( null );
                rightTuple.setHandleNext( null );
                lastRightTuple = rightTuple;
            } else {
                rightTuple.setHandlePrevious( null );
                rightTuple.setHandleNext( previousFirst );
                previousFirst.setHandlePrevious( rightTuple );
            }
        }

        public void addLastRightTuple( RightTuple rightTuple ) {
            RightTuple previousLast = lastRightTuple;
            if( previousLast == null ){
                rightTuple.setHandlePrevious( null );
                rightTuple.setHandleNext( null );
                firstRightTuple = rightTuple;
                lastRightTuple = rightTuple;
            } else {
                rightTuple.setHandlePrevious( previousLast );
                rightTuple.setHandleNext( null );
                previousLast.setHandleNext( rightTuple );
                lastRightTuple = rightTuple;
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
                firstRightTuple = next;
            } else if ( previous != null ) {
                // remove from end
                previous.setHandleNext( null );
                lastRightTuple = previous;
            } else {
                // single remaining item, no previous or next
                firstRightTuple = null;
                lastRightTuple = null;
            }
            rightTuple.setHandlePrevious( null );
            rightTuple.setHandleNext( null );
        }

        public void clearLeftTuples() {
            firstLeftTuple = null;
            lastLeftTuple = null;
        }

        public void clearRightTuples() {
            firstRightTuple = null;
            lastRightTuple = null;
        }

        public void forEachRightTuple(Consumer<RightTuple> rightTupleConsumer) {
            for (RightTuple rightTuple = firstRightTuple; rightTuple != null; ) {
                RightTuple nextRightTuple = rightTuple.getHandleNext();
                rightTupleConsumer.accept( rightTuple );
                rightTuple = nextRightTuple;
            }
        }

        public RightTuple findFirstRightTuple(Predicate<RightTuple> rightTuplePredicate ) {
            for (RightTuple rightTuple = firstRightTuple; rightTuple != null; ) {
                RightTuple nextRightTuple = rightTuple.getHandleNext();
                if (rightTuplePredicate.test( rightTuple )) {
                    return rightTuple;
                }
                rightTuple = nextRightTuple;
            }
            return null;
        }

        public void forEachLeftTuple(Consumer<LeftTuple> leftTupleConsumer) {
            for ( LeftTuple leftTuple = firstLeftTuple; leftTuple != null; ) {
                LeftTuple nextLeftTuple = leftTuple.getHandleNext();
                leftTupleConsumer.accept( leftTuple );
                leftTuple = nextLeftTuple;
            }
        }

        public LeftTuple findFirstLeftTuple(Predicate<LeftTuple> lefttTuplePredicate ) {
            for ( LeftTuple leftTuple = firstLeftTuple; leftTuple != null; ) {
                LeftTuple nextLeftTuple = leftTuple.getHandleNext();
                if (lefttTuplePredicate.test( leftTuple )) {
                    return leftTuple;
                }
                leftTuple = nextLeftTuple;
            }
            return null;
        }

        public LeftTuple getFirstLeftTuple() {
            return firstLeftTuple;
        }

        public void setFirstLeftTuple( LeftTuple firstLeftTuple ) {
            this.firstLeftTuple = firstLeftTuple;
        }

        public RightTuple getFirstRightTuple() {
            return firstRightTuple;
        }
    }

    public static class CompositeLinkedTuples implements LinkedTuples {

        private final LinkedTuples[] partitionedTuples = new LinkedTuples[RuleBasePartitionId.PARALLEL_PARTITIONS_NUMBER];

        public CompositeLinkedTuples() {
            for (int i = 0; i < partitionedTuples.length; i++) {
                partitionedTuples[i] = new SingleLinkedTuples();
            }
        }

        @Override
        public LinkedTuples clone() {
            CompositeLinkedTuples clone = new CompositeLinkedTuples();
            for (int i = 0; i < partitionedTuples.length; i++) {
                clone.partitionedTuples[i] = partitionedTuples[i].clone();
            }
            return clone;
        }

        private LinkedTuples getPartitionTuples(Tuple tuple) {
            return partitionedTuples[tuple.getTupleSink().getPartitionId().getParallelEvaluationSlot()];
        }

        @Override
        public void addFirstLeftTuple( LeftTuple leftTuple ) {
            getPartitionTuples(leftTuple).addFirstLeftTuple( leftTuple );
        }

        @Override
        public void addLastLeftTuple( LeftTuple leftTuple ) {
            getPartitionTuples(leftTuple).addLastLeftTuple( leftTuple );
        }

        @Override
        public void addTupleInPosition( Tuple tuple ) {
            getPartitionTuples(tuple).addTupleInPosition( tuple );
        }

        @Override
        public void removeLeftTuple( LeftTuple leftTuple ) {
            getPartitionTuples(leftTuple).removeLeftTuple( leftTuple );
        }

        @Override
        public void addFirstRightTuple( RightTuple rightTuple ) {
            getPartitionTuples(rightTuple).addFirstRightTuple( rightTuple );
        }

        @Override
        public void addLastRightTuple( RightTuple rightTuple ) {
            getPartitionTuples(rightTuple).addLastRightTuple( rightTuple );
        }

        @Override
        public void removeRightTuple( RightTuple rightTuple ) {
            getPartitionTuples(rightTuple).removeRightTuple( rightTuple );
        }

        @Override
        public void clearLeftTuples() {
            for (int i = 0; i < partitionedTuples.length; i++) {
                clearLeftTuples(i);
            }
        }

        public void clearLeftTuples(int partition) {
            partitionedTuples[partition].clearLeftTuples();
        }

        @Override
        public void clearRightTuples() {
            for (int i = 0; i < partitionedTuples.length; i++) {
                clearRightTuples(i);
            }
        }

        public void clearRightTuples(int partition) {
            partitionedTuples[partition].clearRightTuples();
        }

        @Override
        public void forEachRightTuple( Consumer<RightTuple> rightTupleConsumer ) {
            for (int i = 0; i < partitionedTuples.length; i++) {
                forEachRightTuple( i, rightTupleConsumer );
            }
        }

        public void forEachRightTuple( int partition, Consumer<RightTuple> rightTupleConsumer ) {
            partitionedTuples[partition].forEachRightTuple( rightTupleConsumer );
        }

        @Override
        public RightTuple findFirstRightTuple( Predicate<RightTuple> rightTuplePredicate ) {
            return Stream.of( partitionedTuples )
                         .map( t -> t.findFirstRightTuple( rightTuplePredicate ) )
                         .filter( rt -> rt != null )
                         .findFirst()
                         .orElse( null );
        }

        @Override
        public void forEachLeftTuple( Consumer<LeftTuple> leftTupleConsumer ) {
            for (int i = 0; i < partitionedTuples.length; i++) {
                forEachLeftTuple( i, leftTupleConsumer );
            }
        }

        public void forEachLeftTuple( int partition, Consumer<LeftTuple> leftTupleConsumer ) {
            partitionedTuples[partition].forEachLeftTuple( leftTupleConsumer );
        }

        @Override
        public LeftTuple findFirstLeftTuple( Predicate<LeftTuple> lefttTuplePredicate ) {
            return Stream.of( partitionedTuples )
                         .map( t -> t.findFirstLeftTuple( lefttTuplePredicate ) )
                         .filter( lt -> lt != null )
                         .findFirst()
                         .orElse( null );
        }

        @Override
        public LeftTuple getFirstLeftTuple() {
            throw new UnsupportedOperationException( );
        }

        @Override
        public void setFirstLeftTuple( LeftTuple firstLeftTuple ) {
            throw new UnsupportedOperationException( );
        }

        @Override
        public RightTuple getFirstRightTuple() {
            throw new UnsupportedOperationException( );
        }
    }

    @Override
    public void forEachRightTuple(Consumer<RightTuple> rightTupleConsumer) {
        linkedTuples.forEachRightTuple( rightTupleConsumer );
    }

    @Override
    public RightTuple findFirstRightTuple(Predicate<RightTuple> rightTuplePredicate ) {
        return linkedTuples.findFirstRightTuple( rightTuplePredicate );
    }

    @Override
    public void forEachLeftTuple(Consumer<LeftTuple> leftTupleConsumer) {
        linkedTuples.forEachLeftTuple( leftTupleConsumer );
    }

    @Override
    public LeftTuple findFirstLeftTuple(Predicate<LeftTuple> lefttTuplePredicate ) {
        return linkedTuples.findFirstLeftTuple( lefttTuplePredicate );
    }

    @Override
    public LeftTuple getFirstLeftTuple() {
        return linkedTuples.getFirstLeftTuple();
    }

    @Override
    public void setFirstLeftTuple( LeftTuple firstLeftTuple ) {
        linkedTuples.setFirstLeftTuple( firstLeftTuple );
    }

    @Override
    public RightTuple getFirstRightTuple() {
        return linkedTuples.getFirstRightTuple();
    }

    @Override
    public LinkedTuples getLinkedTuples() {
        return linkedTuples;
    }

    @Override
    public LinkedTuples detachLinkedTuples() {
        LinkedTuples detached = linkedTuples;
        linkedTuples = new SingleLinkedTuples();
        return detached;
    }

    @Override
    public LinkedTuples detachLinkedTuplesForPartition(int i) {
        LinkedTuples detached = ( (CompositeLinkedTuples) linkedTuples ).partitionedTuples[i];
        ( (CompositeLinkedTuples) linkedTuples ).partitionedTuples[i] = new SingleLinkedTuples();
        return detached;
    }
}

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

import org.drools.base.factmodel.traits.TraitTypeEnum;
import org.drools.base.rule.EntryPointId;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.impl.InternalRuleBase;
import org.drools.core.reteoo.AbstractLeftTuple;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.Tuple;
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.drools.util.StringUtils;
import org.kie.api.runtime.rule.FactHandle;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;
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

    private static final long       serialVersionUID = 510l;
    /** Handle id. */

    static final String     FACT_FORMAT_VERSION = "0";

    protected long id;
    protected long recency;
    protected Object object;
    private EqualityKey key;
    protected int objectHashCode;
    protected int identityHashCode;

    protected EntryPointId entryPointId;

    private boolean disconnected;

    private boolean valid = true;

    private boolean negated;

    protected String objectClassName;

    protected LinkedTuples linkedTuples;

    protected transient WorkingMemoryEntryPoint wmEntryPoint;

    public DefaultFactHandle() {
    }

    public DefaultFactHandle(final Object object) {
        this.object = object;
    }

    public DefaultFactHandle(final long id, final Object object) {
        // this is only used by tests, left as legacy as so many test rely on it.
        this( id, object, id, null );
    }

    /**
     * Construct.
     *
     * @param id
     *            Handle id.
     */
    public DefaultFactHandle(final long id,
                             final Object object,
                             final long recency,
                             final WorkingMemoryEntryPoint wmEntryPoint) {
        this( id, determineIdentityHashCode( object ), object, recency, wmEntryPoint );
    }

    public DefaultFactHandle(final long id,
                             final int identityHashCode,
                             final Object object,
                             final long recency,
                             final WorkingMemoryEntryPoint wmEntryPoint ) {
        this(id, identityHashCode, object, recency, wmEntryPoint == null ? null : wmEntryPoint.getEntryPoint());
        if (wmEntryPoint != null) {
            setLinkedTuples( wmEntryPoint.getKnowledgeBase() );
            this.wmEntryPoint = wmEntryPoint;
        } else {
            this.linkedTuples = new SingleLinkedTuples();
        }
    }

    protected DefaultFactHandle(final long id,
                                final int identityHashCode,
                                final Object object,
                                final long recency,
                                final EntryPointId entryPointId ) {
        this.id = id;
        this.entryPointId = entryPointId;
        this.recency = recency;
        setObject( object );
        this.identityHashCode = identityHashCode;
    }

    public DefaultFactHandle(long id,
                             String wmEntryPointId,
                             int identityHashCode,
                             int objectHashCode,
                             long recency,
                             Object object) {
        this.id = id;
        this.entryPointId = new EntryPointId( wmEntryPointId );
        this.recency = recency;
        setObject( object );
        this.identityHashCode = identityHashCode;
        this.objectHashCode = objectHashCode;
        this.disconnected = true;
    }

    /**
     * @see Object
     */
    public boolean equals( final Object object ) {
        return this == object || ( object instanceof DefaultFactHandle && this.id == ( (DefaultFactHandle) object ).id );
    }

    public void disconnect() {
        this.key = null;
        this.linkedTuples = null;
        this.entryPointId = null;
        this.disconnected = true;
    }

    public boolean isNegated() {
        return negated;
    }

    public void setNegated(boolean negated) {
        this.negated = negated;
    }

    @Override
    public <K> K as(Class<K> klass) throws ClassCastException {
        if ( klass.isAssignableFrom( object.getClass() ) ) {
            return (K) object;
        }
        throw new ClassCastException( "The Handle's Object can't be cast to " + klass );
    }

    @Override
    public boolean isDisconnected() {
        return disconnected;
    }

    @Override
    public void setDisconnected( boolean disconnected ) {
        this.disconnected = disconnected;
    }

    public int getObjectHashCode() {
        if (this.objectHashCode == 0 && this.object != null) {
            this.objectHashCode = object.hashCode();
        }
        return objectHashCode;
    }

    public int getIdentityHashCode() {
        if (this.identityHashCode == 0 && this.object != null) {
            this.identityHashCode = determineIdentityHashCode( object );
        }
        return this.identityHashCode;
    }

    public static int determineIdentityHashCode( Object object ) {
        return System.identityHashCode( object );
    }

    protected void setIdentityHashCode( int identityHashCode ) {
        this.identityHashCode = identityHashCode;
    }

    protected void setObjectHashCode( int hashCode ) {
        this.objectHashCode = hashCode;
    }

    /**
     * @see Object
     */
    public int hashCode() {
        return Long.hashCode(this.id);
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
                ( ( this.entryPointId != null ) ? this.entryPointId.getEntryPointId() : "null" ) +
               ":" +
               getTraitType().name() +
               ":" +
                getObjectClassName();
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

    public long getId() {
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
        if (object != null) {
            this.objectClassName = object.getClass().getName();
        }
        return this.objectClassName;
    }

    public void setObject( final Object object ) {
        this.object = object;
        this.objectClassName = null;
        this.objectHashCode = 0;
        this.identityHashCode = 0;
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
    @Override
    public boolean isEvent() {
        return false;
    }

    @Override
    public boolean isTraitOrTraitable() {
        return false;
    }

    @Override
    public boolean isTraitable() {
        return false;
    }

    @Override
    public boolean isTraiting() {
        return false;
    }

    @Override
    public TraitTypeEnum getTraitType() {
        return TraitTypeEnum.NON_TRAIT;
    }

    protected void setTraitType(TraitTypeEnum traitType) { }

    public ReteEvaluator getReteEvaluator() {
        return wmEntryPoint.getReteEvaluator();
    }

    public EntryPointId getEntryPointId() {
        return entryPointId;
    }

    @Override
    public WorkingMemoryEntryPoint getEntryPoint(ReteEvaluator reteEvaluator) {
        if (wmEntryPoint == null) {
            wmEntryPoint = reteEvaluator.getEntryPoint( entryPointId.getEntryPointId() );
        }
        return wmEntryPoint;
    }

    protected void setLinkedTuples(InternalRuleBase kbase) {
        linkedTuples = kbase != null && kbase.hasParallelEvaluation() ?
                       new CompositeLinkedTuples(kbase.getParallelEvaluationSlotsCount()) :
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

    public DefaultFactHandle clone() {
        DefaultFactHandle clone = new DefaultFactHandle( this.id, this.identityHashCode, this.object, this.recency, this.entryPointId );
        clone.key = this.key;
        clone.linkedTuples = this.linkedTuples.clone();

        clone.objectHashCode = this.objectHashCode;
        clone.disconnected = this.disconnected;
        clone.negated = this.negated;
        clone.wmEntryPoint = this.wmEntryPoint;
        return clone;
    }

    public static DefaultFactHandle createFromExternalFormat( String externalFormat ) {
        String[] elements = splitExternalForm( externalFormat );
        DefaultFactHandle handle;
        if (FACT_FORMAT_VERSION.equals( elements[0]) ) {
            handle = new DefaultFactHandle();
        } else if (DefaultEventHandle.EVENT_FORMAT_VERSION.equals(elements[0])) {
            handle = new DefaultEventHandle();
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
        handle.id = Long.parseLong( elements[1] );
        handle.identityHashCode = Integer.parseInt( elements[2] );
        handle.objectHashCode = Integer.parseInt( elements[3] );
        handle.recency = Long.parseLong( elements[4] );
        handle.entryPointId = StringUtils.isEmpty( elements[5] ) || "null".equals( elements[5].trim() ) ?
                            null :
                            new EntryPointId( elements[5].trim() );
        handle.disconnected = true;
        handle.setTraitType( elements.length > 6 ? TraitTypeEnum.valueOf( elements[6] ) : TraitTypeEnum.NON_TRAIT );
        handle.objectClassName = elements.length > 7 ? elements[7] : null;
    }

    public boolean isExpired() {
        return false;
    }

    public boolean isPendingRemoveFromStore() {
        return false;
    }

    public static class SingleLinkedTuples implements LinkedTuples {
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

        @Override
        public LinkedTuples cloneEmpty() {
            return new SingleLinkedTuples();
        }

        @Override
        public boolean hasTuples() {
            return firstLeftTuple != null || firstRightTuple != null;
        }

        @Override
        public void addFirstLeftTuple( LeftTuple leftTuple ) {
            LeftTuple previous = firstLeftTuple;
            leftTuple.setHandlePrevious( null );
            if ( previous == null ) {
                // no other LeftTuples, just add.
                leftTuple.setHandleNext( null );
                firstLeftTuple = leftTuple;
                lastLeftTuple = leftTuple;
            } else {
                leftTuple.setHandleNext( previous );
                previous.setHandlePrevious( leftTuple );
                firstLeftTuple = leftTuple;
            }
        }

        @Override
        public void addLastLeftTuple( LeftTuple leftTuple ) {
            LeftTuple previous = lastLeftTuple;
            if ( previous == null ) {
                // no other LeftTuples, just add.
                leftTuple.setHandlePrevious( null );
                leftTuple.setHandleNext( null );
                firstLeftTuple = leftTuple;
            } else {
                leftTuple.setHandlePrevious( previous );
                leftTuple.setHandleNext( null );
                previous.setHandleNext( leftTuple );
            }
            lastLeftTuple = leftTuple;
        }

        @Override
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

        @Override
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

        @Override
        public void addFirstRightTuple( RightTuple rightTuple ) {
            RightTuple previousFirst = firstRightTuple;
            firstRightTuple = rightTuple;
            rightTuple.setHandlePrevious( null );
            if ( previousFirst == null ) {
                rightTuple.setHandleNext( null );
                lastRightTuple = rightTuple;
            } else {
                rightTuple.setHandleNext( previousFirst );
                previousFirst.setHandlePrevious( rightTuple );
            }
        }

        @Override
        public void addLastRightTuple( RightTuple rightTuple ) {
            RightTuple previousLast = lastRightTuple;
            if ( previousLast == null ) {
                rightTuple.setHandlePrevious( null );
                rightTuple.setHandleNext( null );
                firstRightTuple = rightTuple;
            } else {
                rightTuple.setHandlePrevious( previousLast );
                rightTuple.setHandleNext( null );
                previousLast.setHandleNext( rightTuple );
            }
            lastRightTuple = rightTuple;
        }

        @Override
        public void removeRightTuple( RightTuple rightTuple ) {
            RightTuple previous = rightTuple.getHandlePrevious();
            RightTuple next = rightTuple.getHandleNext();

            if ( previous != null && next != null ) {
                // remove from middle
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

        @Override
        public void clearLeftTuples() {
            firstLeftTuple = null;
            lastLeftTuple = null;
        }

        @Override
        public void clearRightTuples() {
            firstRightTuple = null;
            lastRightTuple = null;
        }

        @Override
        public void forEachRightTuple(Consumer<RightTuple> rightTupleConsumer) {
            for (RightTuple rightTuple = firstRightTuple; rightTuple != null; ) {
                RightTuple nextRightTuple = rightTuple.getHandleNext();
                rightTupleConsumer.accept( rightTuple );
                rightTuple = nextRightTuple;
            }
        }

        @Override
        public void forEachLeftTuple(Consumer<AbstractLeftTuple> leftTupleConsumer) {
            for ( LeftTuple leftTuple = firstLeftTuple; leftTuple != null; ) {
                LeftTuple nextLeftTuple = leftTuple.getHandleNext();
                leftTupleConsumer.accept( (AbstractLeftTuple) leftTuple );
                leftTuple = nextLeftTuple;
            }
        }

        public AbstractLeftTuple findFirstLeftTuple(Predicate<AbstractLeftTuple> lefttTuplePredicate ) {
            for ( LeftTuple leftTuple = firstLeftTuple; leftTuple != null; ) {
                LeftTuple nextLeftTuple = leftTuple.getHandleNext();
                if (lefttTuplePredicate.test( (AbstractLeftTuple) leftTuple )) {
                    return (AbstractLeftTuple) leftTuple;
                }
                leftTuple = nextLeftTuple;
            }
            return null;
        }

        @Override
        public LeftTuple getFirstLeftTuple(int partition) {
            return getFirstLeftTuple();
        }

        LeftTuple getFirstLeftTuple() {
            return firstLeftTuple;
        }

        @Override
        public RightTuple getFirstRightTuple(int partition) {
            return getFirstRightTuple();
        }

        RightTuple getFirstRightTuple() {
            return firstRightTuple;
        }
    }

    public static class CompositeLinkedTuples implements LinkedTuples {

        private final LinkedTuples[] partitionedTuples;

        public CompositeLinkedTuples(int parallelEvaluationSlotsCount) {
            this.partitionedTuples = new LinkedTuples[parallelEvaluationSlotsCount];
        }

        private LinkedTuples getPartitionedTuple(int partition) {
            LinkedTuples tuples = partitionedTuples[partition];
            return tuples != null ? tuples : DummyLinkedTuples.INSTANCE;
        }

        private LinkedTuples getOrCreatePartitionedTuple(int partition) {
            LinkedTuples tuples = partitionedTuples[partition];
            if (tuples == null) {
                tuples = ( partitionedTuples[partition] = new SingleLinkedTuples() );
            }
            return tuples;
        }

        @Override
        public LinkedTuples cloneEmpty() {
            return new CompositeLinkedTuples(partitionedTuples.length);
        }

        @Override
        public boolean hasTuples() {
            for (int i = 0; i < partitionedTuples.length; i++) {
                if (getPartitionedTuple(i).hasTuples()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public LinkedTuples clone() {
            CompositeLinkedTuples clone = new CompositeLinkedTuples(partitionedTuples.length);
            for (int i = 0; i < partitionedTuples.length; i++) {
                clone.partitionedTuples[i] = partitionedTuples[i] == null ? null : partitionedTuples[i].clone();
            }
            return clone;
        }

        private LinkedTuples getPartitionedTuple(Tuple tuple) {
            return getPartitionedTuple(tuple.getTupleSink().getPartitionId().getParallelEvaluationSlot());
        }

        private LinkedTuples getOrCreatePartitionedTuple(Tuple tuple) {
            return getOrCreatePartitionedTuple(tuple.getTupleSink().getPartitionId().getParallelEvaluationSlot());
        }

        @Override
        public void addFirstLeftTuple( LeftTuple leftTuple ) {
            getOrCreatePartitionedTuple(leftTuple).addFirstLeftTuple( leftTuple );
        }

        @Override
        public void addLastLeftTuple( LeftTuple leftTuple ) {
            getOrCreatePartitionedTuple(leftTuple).addLastLeftTuple( leftTuple );
        }

        @Override
        public void addTupleInPosition( Tuple tuple ) {
            getOrCreatePartitionedTuple(tuple).addTupleInPosition( tuple );
        }

        @Override
        public void removeLeftTuple( LeftTuple leftTuple ) {
            getPartitionedTuple(leftTuple).removeLeftTuple( leftTuple );
        }

        @Override
        public void addFirstRightTuple( RightTuple rightTuple ) {
            getOrCreatePartitionedTuple(rightTuple).addFirstRightTuple( rightTuple );
        }

        @Override
        public void addLastRightTuple( RightTuple rightTuple ) {
            getOrCreatePartitionedTuple(rightTuple).addLastRightTuple( rightTuple );
        }

        @Override
        public void removeRightTuple( RightTuple rightTuple ) {
            if (rightTuple.getTupleSink() != null) {
                getPartitionedTuple( rightTuple ).removeRightTuple( rightTuple );
            }
        }

        @Override
        public void clearLeftTuples() {
            for (int i = 0; i < partitionedTuples.length; i++) {
                clearLeftTuples(i);
            }
        }

        public void clearLeftTuples(int partition) {
            getPartitionedTuple(partition).clearLeftTuples();
        }

        @Override
        public void clearRightTuples() {
            for (int i = 0; i < partitionedTuples.length; i++) {
                clearRightTuples(i);
            }
        }

        public void clearRightTuples(int partition) {
            getPartitionedTuple(partition).clearRightTuples();
        }

        @Override
        public void forEachRightTuple( Consumer<RightTuple> rightTupleConsumer ) {
            for (int i = 0; i < partitionedTuples.length; i++) {
                forEachRightTuple( i, rightTupleConsumer );
            }
        }

        public void forEachRightTuple( int partition, Consumer<RightTuple> rightTupleConsumer ) {
            getPartitionedTuple(partition).forEachRightTuple( rightTupleConsumer );
        }

        @Override
        public void forEachLeftTuple( Consumer<AbstractLeftTuple> leftTupleConsumer ) {
            for (int i = 0; i < partitionedTuples.length; i++) {
                forEachLeftTuple( i, leftTupleConsumer );
            }
        }

        public void forEachLeftTuple( int partition, Consumer<AbstractLeftTuple> leftTupleConsumer ) {
            getPartitionedTuple(partition).forEachLeftTuple( leftTupleConsumer );
        }

        @Override
        public AbstractLeftTuple findFirstLeftTuple( Predicate<AbstractLeftTuple> lefttTuplePredicate ) {
            return Stream.of( partitionedTuples )
                         .map( t -> t.findFirstLeftTuple( lefttTuplePredicate ) )
                         .filter( Objects::nonNull )
                         .findFirst()
                         .orElse( null );
        }

        @Override
        public LeftTuple getFirstLeftTuple(int partition) {
            return ((SingleLinkedTuples) partitionedTuples[partition]).getFirstLeftTuple();
        }

        @Override
        public RightTuple getFirstRightTuple(int partition) {
            return ((SingleLinkedTuples) partitionedTuples[partition]).getFirstRightTuple();
        }
    }

    public static class DummyLinkedTuples implements LinkedTuples {

        private static final DummyLinkedTuples INSTANCE = new DummyLinkedTuples();

        @Override
        public LinkedTuples clone() {
            return this;
        }

        @Override
        public LinkedTuples cloneEmpty() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasTuples() {
            return false;
        }

        @Override
        public void addFirstLeftTuple(LeftTuple leftTuple) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addLastLeftTuple(LeftTuple leftTuple) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addTupleInPosition(Tuple tuple) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeLeftTuple(LeftTuple leftTuple) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addFirstRightTuple(RightTuple rightTuple) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addLastRightTuple(RightTuple rightTuple) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeRightTuple(RightTuple rightTuple) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clearLeftTuples() { }

        @Override
        public void clearRightTuples() { }

        @Override
        public void forEachRightTuple(Consumer<RightTuple> rightTupleConsumer) { }

        @Override
        public void forEachLeftTuple(Consumer<AbstractLeftTuple> leftTupleConsumer) { }

        @Override
        public AbstractLeftTuple findFirstLeftTuple(Predicate<AbstractLeftTuple> leftTuplePredicate) {
            return null;
        }

        @Override
        public LeftTuple getFirstLeftTuple(int partition) {
            return null;
        }

        @Override
        public RightTuple getFirstRightTuple(int partition) {
            return null;
        }
    }

    @Override
    public void forEachRightTuple(Consumer<RightTuple> rightTupleConsumer) {
        linkedTuples.forEachRightTuple( rightTupleConsumer );
    }

    @Override
    public void forEachLeftTuple(Consumer<AbstractLeftTuple> leftTupleConsumer) {
        linkedTuples.forEachLeftTuple( leftTupleConsumer );
    }

    @Override
    public LeftTuple findFirstLeftTuple(Predicate<AbstractLeftTuple> lefttTuplePredicate ) {
        return linkedTuples.findFirstLeftTuple( lefttTuplePredicate );
    }

    @Override
    public LeftTuple getFirstLeftTuple() {
        if (linkedTuples instanceof SingleLinkedTuples) {
            return ( (SingleLinkedTuples) linkedTuples ).getFirstLeftTuple();
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public RightTuple getFirstRightTuple() {
        if (linkedTuples instanceof SingleLinkedTuples) {
            return ( (SingleLinkedTuples) linkedTuples ).getFirstRightTuple();
        }
        throw new UnsupportedOperationException();
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
        LinkedTuples detached = ( (CompositeLinkedTuples) linkedTuples ).getPartitionedTuple(i);
        ( (CompositeLinkedTuples) linkedTuples ).partitionedTuples[i] = null;
        return detached;
    }
}

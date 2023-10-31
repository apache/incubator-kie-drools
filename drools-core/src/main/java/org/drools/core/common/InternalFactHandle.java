/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.common;

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.drools.base.common.RuleBasePartitionId;
import org.drools.base.factmodel.traits.TraitTypeEnum;
import org.drools.base.rule.EntryPointId;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.Tuple;
import org.kie.api.runtime.rule.FactHandle;

public interface InternalFactHandle
    extends
    FactHandle, Cloneable, Serializable {
    long getId();

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
    default void setDisconnected( boolean disconnected ) {
        throw new UnsupportedOperationException();
    }

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

    LeftTuple getFirstLeftTuple();

    default ReteEvaluator getReteEvaluator() {
        return null;
    }

    EntryPointId getEntryPointId();
    default String getEntryPointName() {
        return getEntryPointId() != null ? getEntryPointId().getEntryPointId() : null;
    }
    WorkingMemoryEntryPoint getEntryPoint(ReteEvaluator reteEvaluator);

    InternalFactHandle clone();
    
    String toExternalForm();
    
    void disconnect();

    void addFirstLeftTuple(LeftTuple leftTuple);

    void addLastLeftTuple( LeftTuple leftTuple );

    void removeLeftTuple( LeftTuple leftTuple );

    void clearLeftTuples();

    void clearRightTuples();

    void addLastRightTuple( RightTuple rightTuple );

    void removeRightTuple( RightTuple rightTuple );

    void addTupleInPosition( Tuple tuple );

    boolean isNegated();
    void setNegated(boolean negated);

    boolean isExpired();
    boolean isPendingRemoveFromStore();

    void forEachRightTuple(Consumer<RightTuple> rightTupleConsumer );
    void forEachLeftTuple(Consumer<LeftTuple> leftTupleConsumer);

    LeftTuple findFirstLeftTuple(Predicate<LeftTuple> lefttTuplePredicate );

    LinkedTuples detachLinkedTuples();
    LinkedTuples detachLinkedTuplesForPartition(int i);

    LinkedTuples getLinkedTuples();

    default boolean hasMatches() {
        return getLinkedTuples().hasTuples();
    }

    interface LinkedTuples extends Serializable {
        LinkedTuples clone();
        LinkedTuples cloneEmpty();

        boolean hasTuples();

        void addFirstLeftTuple( LeftTuple leftTuple );
        void addLastLeftTuple( LeftTuple leftTuple );

        void addTupleInPosition( Tuple tuple );

        void removeLeftTuple( LeftTuple leftTuple );

        void addFirstRightTuple( RightTuple rightTuple );
        void addLastRightTuple( RightTuple rightTuple );

        void removeRightTuple( RightTuple rightTuple );

        void clearLeftTuples();
        void clearRightTuples();

        void forEachRightTuple(Consumer<RightTuple> rightTupleConsumer);

        void forEachLeftTuple(Consumer<LeftTuple> leftTupleConsumer);
        LeftTuple findFirstLeftTuple(Predicate<LeftTuple> leftTuplePredicate );

        LeftTuple getFirstLeftTuple(int partition);

        default LeftTuple getFirstLeftTuple(RuleBasePartitionId partitionId) {
            return getFirstLeftTuple( partitionId.getParallelEvaluationSlot() );
        }

        RightTuple getFirstRightTuple(int partition);

        default RightTuple getFirstRightTuple(RuleBasePartitionId partitionId) {
            return getFirstRightTuple( partitionId.getParallelEvaluationSlot() );
        }
    }

    static InternalFactHandle dummyFactHandleOf(Object object) {
        return new DummyFactHandle( object );
    }

    class DummyFactHandle implements InternalFactHandle {
        private final Object object;

        private DummyFactHandle( Object object ) {
            this.object = object;
        }

        @Override
        public Object getObject() {
            return object;
        }

        @Override
        public long getId() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getRecency() {
            throw new UnsupportedOperationException();
        }


        @Override
        public String getObjectClassName() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setObject( Object object ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setEqualityKey( EqualityKey key ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public EqualityKey getEqualityKey() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setRecency( long recency ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void invalidate() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isValid() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getIdentityHashCode() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getObjectHashCode() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isDisconnected() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isEvent() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isTraitOrTraitable() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isTraitable() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isTraiting() {
            throw new UnsupportedOperationException();
        }

        @Override
        public TraitTypeEnum getTraitType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public RightTuple getFirstRightTuple() {
            throw new UnsupportedOperationException();
        }

        @Override
        public LeftTuple getFirstLeftTuple() {
            throw new UnsupportedOperationException();
        }

        @Override
        public EntryPointId getEntryPointId() {
            throw new UnsupportedOperationException();
        }

        @Override
        public WorkingMemoryEntryPoint getEntryPoint( ReteEvaluator reteEvaluator ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public InternalFactHandle clone() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toExternalForm() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void disconnect() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addFirstLeftTuple( LeftTuple leftTuple ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addLastLeftTuple( LeftTuple leftTuple ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeLeftTuple( LeftTuple leftTuple ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clearLeftTuples() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clearRightTuples() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addLastRightTuple( RightTuple rightTuple ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeRightTuple( RightTuple rightTuple ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addTupleInPosition( Tuple tuple ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isNegated() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setNegated( boolean negated ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <K> K as(Class<K> klass) throws ClassCastException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isExpired() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isPendingRemoveFromStore() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void forEachRightTuple( Consumer<RightTuple> rightTupleConsumer ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void forEachLeftTuple( Consumer<LeftTuple> leftTupleConsumer ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public LeftTuple findFirstLeftTuple(Predicate<LeftTuple> lefttTuplePredicate ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public LinkedTuples detachLinkedTuples() {
            throw new UnsupportedOperationException();
        }

        @Override
        public LinkedTuples detachLinkedTuplesForPartition( int i ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public LinkedTuples getLinkedTuples() {
            throw new UnsupportedOperationException();
        }
    }
}

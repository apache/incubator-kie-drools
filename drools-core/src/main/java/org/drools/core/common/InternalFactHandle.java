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
import org.drools.core.reteoo.ObjectTypeNodeId;
import org.drools.core.reteoo.TupleImpl;
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

    TupleImpl getFirstRightTuple();

    TupleImpl getFirstLeftTuple();

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

    void addFirstLeftTuple(TupleImpl leftTuple);

    void addLastLeftTuple( TupleImpl leftTuple);

    void removeLeftTuple( TupleImpl leftTuple);

    void clearLeftTuples();

    void clearRightTuples();

    void addLastRightTuple( TupleImpl rightTuple);

    void removeRightTuple( TupleImpl rightTuple);

    boolean isNegated();
    void setNegated(boolean negated);

    boolean isExpired();
    boolean isPendingRemoveFromStore();

    void forEachRightTuple(Consumer<TupleImpl> rightTupleConsumer);
    void forEachLeftTuple(Consumer<TupleImpl> leftTupleConsumer);

    TupleImpl findFirstLeftTuple(Predicate<TupleImpl> lefttTuplePredicate);

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

        void addFirstLeftTuple( TupleImpl leftTuple);
        void addLastLeftTuple( TupleImpl leftTuple);
        
        void removeLeftTuple( TupleImpl leftTuple);

        void addFirstRightTuple( TupleImpl rightTuple);
        void addLastRightTuple( TupleImpl rightTuple);

        void removeRightTuple( TupleImpl rightTuple);

        void clearLeftTuples();
        void clearRightTuples();

        void forEachRightTuple(Consumer<TupleImpl> rightTupleConsumer);

        void forEachLeftTuple(Consumer<TupleImpl> leftTupleConsumer);
        TupleImpl findFirstLeftTuple(Predicate<TupleImpl> leftTuplePredicate);

        TupleImpl getFirstLeftTuple(int partition);

        default TupleImpl getFirstLeftTuple(RuleBasePartitionId partitionId) {
            return getFirstLeftTuple( partitionId.getParallelEvaluationSlot() );
        }

        TupleImpl getFirstRightTuple(int partition);

        default TupleImpl getFirstRightTuple(RuleBasePartitionId partitionId) {
            return getFirstRightTuple( partitionId.getParallelEvaluationSlot() );
        }

        default TupleImpl detachLeftTupleAfter(RuleBasePartitionId partitionId, ObjectTypeNodeId otnId) {
            throw new UnsupportedOperationException();
        }

        default TupleImpl detachRightTupleAfter(RuleBasePartitionId partitionId, ObjectTypeNodeId otnId) {
            throw new UnsupportedOperationException();
        }

        default void reattachToLeft(TupleImpl tuple) {
            throw new UnsupportedOperationException();
        }

        default void reattachToRight(TupleImpl tuple) {
            throw new UnsupportedOperationException();
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
        public TupleImpl getFirstRightTuple() {
            throw new UnsupportedOperationException();
        }

        @Override
        public TupleImpl getFirstLeftTuple() {
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
        public void addFirstLeftTuple( TupleImpl leftTuple) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addLastLeftTuple( TupleImpl leftTuple) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeLeftTuple( TupleImpl leftTuple) {
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
        public void addLastRightTuple( TupleImpl rightTuple) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeRightTuple( TupleImpl rightTuple) {
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
        public void forEachRightTuple( Consumer<TupleImpl> rightTupleConsumer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void forEachLeftTuple( Consumer<TupleImpl> leftTupleConsumer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public TupleImpl findFirstLeftTuple(Predicate<TupleImpl> lefttTuplePredicate) {
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

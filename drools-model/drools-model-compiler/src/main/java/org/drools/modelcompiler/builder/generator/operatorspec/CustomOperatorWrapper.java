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

package org.drools.modelcompiler.builder.generator.operatorspec;

import java.util.function.Consumer;
import java.util.function.Predicate;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.factmodel.traits.TraitTypeEnum;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.spi.Evaluator;
import org.drools.core.spi.Tuple;
import org.drools.model.functions.Operator;

public class CustomOperatorWrapper implements Operator.SingleValue<Object, Object> {

    private final Evaluator evaluator;
    private final String name;

    public CustomOperatorWrapper( Evaluator evaluator, String name ) {
        this.evaluator = evaluator;
        this.name = name;
    }

    @Override
    public boolean eval( Object o1, Object o2 ) {
        return evaluator.evaluate(null, null, new DummyFactHandle(o2), null, new DummyFactHandle(o1));
    }

    @Override
    public String getOperatorName() {
        return name;
    }

    private static class DummyFactHandle implements InternalFactHandle {
        private final Object object;

        private DummyFactHandle( Object object ) {
            this.object = object;
        }

        @Override
        public Object getObject() {
            return object;
        }

        @Override
        public int getId() {
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
        public WorkingMemoryEntryPoint getEntryPoint() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setEntryPoint( WorkingMemoryEntryPoint ep ) {
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
        public void addFirstRightTuple( RightTuple rightTuple ) {
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
        public <K> K as( Class<K> klass ) throws ClassCastException {
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
        public RightTuple findFirstRightTuple( Predicate<RightTuple> rightTuplePredicate ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public LeftTuple findFirstLeftTuple( Predicate<LeftTuple> lefttTuplePredicate ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setFirstLeftTuple( LeftTuple firstLeftTuple ) {
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

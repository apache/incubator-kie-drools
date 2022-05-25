/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.bavet.quad;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.NodeBuildHelper;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;

public final class BavetFilterQuadConstraintStream<Solution_, A, B, C, D>
        extends BavetAbstractQuadConstraintStream<Solution_, A, B, C, D> {

    private final BavetAbstractQuadConstraintStream<Solution_, A, B, C, D> parent;
    private final QuadPredicate<A, B, C, D> predicate;

    public BavetFilterQuadConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractQuadConstraintStream<Solution_, A, B, C, D> parent,
            QuadPredicate<A, B, C, D> predicate) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.parent = parent;
        this.predicate = predicate;
        if (predicate == null) {
            throw new IllegalArgumentException("The predicate (null) cannot be null.");
        }
    }

    @Override
    public boolean guaranteesDistinct() {
        return parent.guaranteesDistinct();
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    public void collectActiveConstraintStreams(Set<BavetAbstractConstraintStream<Solution_>> constraintStreamSet) {
        parent.collectActiveConstraintStreams(constraintStreamSet);
        constraintStreamSet.add(this);
    }

    @Override
    public ConstraintStream getTupleSource() {
        return parent.getTupleSource();
    }

    @Override
    public <Score_ extends Score<Score_>> void buildNode(NodeBuildHelper<Score_> buildHelper) {
        Consumer<QuadTuple<A, B, C, D>> insert = buildHelper.getAggregatedInsert(childStreamList);
        Consumer<QuadTuple<A, B, C, D>> retract = buildHelper.getAggregatedRetract(childStreamList);
        buildHelper.putInsertRetract(this,
                new ConditionalQuadConsumer<>(predicate, insert),
                retract);
    }

    private static final class ConditionalQuadConsumer<A, B, C, D> implements Consumer<QuadTuple<A, B, C, D>> {
        private final QuadPredicate<A, B, C, D> predicate;
        private final Consumer<QuadTuple<A, B, C, D>> consumer;

        public ConditionalQuadConsumer(QuadPredicate<A, B, C, D> predicate, Consumer<QuadTuple<A, B, C, D>> consumer) {
            this.predicate = predicate;
            this.consumer = consumer;
        }

        @Override
        public void accept(QuadTuple<A, B, C, D> tuple) {
            if (predicate.test(tuple.factA, tuple.factB, tuple.factC, tuple.factD)) {
                consumer.accept(tuple);
            }
        }

    }

    // ************************************************************************
    // Equality for node sharing
    // ************************************************************************

    @Override
    public int hashCode() {
        return Objects.hash(parent, predicate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof BavetFilterQuadConstraintStream) {
            BavetFilterQuadConstraintStream<?, ?, ?, ?, ?> other = (BavetFilterQuadConstraintStream<?, ?, ?, ?, ?>) o;
            return parent == other.parent
                    && predicate == other.predicate;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Filter() with " + childStreamList.size() + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}

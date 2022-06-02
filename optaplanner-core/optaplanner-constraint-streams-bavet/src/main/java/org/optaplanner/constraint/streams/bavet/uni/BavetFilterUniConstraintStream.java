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

package org.optaplanner.constraint.streams.bavet.uni;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.common.AbstractInserter;
import org.optaplanner.constraint.streams.bavet.common.AbstractUpdater;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.NodeBuildHelper;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;

public final class BavetFilterUniConstraintStream<Solution_, A> extends BavetAbstractUniConstraintStream<Solution_, A> {

    private final BavetAbstractUniConstraintStream<Solution_, A> parent;
    private final Predicate<A> predicate;

    public BavetFilterUniConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractUniConstraintStream<Solution_, A> parent, Predicate<A> predicate) {
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
        buildHelper.<UniTuple<A>> putInsertUpdateRetract(this, childStreamList,
                insert -> new ConditionalUniInserter<>(predicate, insert),
                (update, retract) -> new ConditionalUniUpdater<>(predicate, update, retract));
    }

    private static final class ConditionalUniInserter<A> extends AbstractInserter<UniTuple<A>> {
        private final Predicate<A> predicate;

        public ConditionalUniInserter(Predicate<A> predicate, Consumer<UniTuple<A>> insert) {
            super(insert);
            this.predicate = predicate;
        }

        @Override
        protected boolean test(UniTuple<A> tuple) {
            return predicate.test(tuple.factA);
        }
    }

    private static final class ConditionalUniUpdater<A> extends AbstractUpdater<UniTuple<A>> {
        private final Predicate<A> predicate;

        public ConditionalUniUpdater(Predicate<A> predicate, Consumer<UniTuple<A>> update,
                Consumer<UniTuple<A>> retract) {
            super(update, retract);
            this.predicate = predicate;
        }

        @Override
        protected boolean test(UniTuple<A> tuple) {
            return predicate.test(tuple.factA);
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
        } else if (o instanceof BavetFilterUniConstraintStream) {
            BavetFilterUniConstraintStream<?, ?> other = (BavetFilterUniConstraintStream<?, ?>) o;
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

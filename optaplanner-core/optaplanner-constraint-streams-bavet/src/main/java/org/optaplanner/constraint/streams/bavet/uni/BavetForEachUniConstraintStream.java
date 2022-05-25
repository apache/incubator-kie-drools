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

import java.util.Set;
import java.util.function.Consumer;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.NodeBuildHelper;
import org.optaplanner.constraint.streams.common.RetrievalSemantics;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;

public final class BavetForEachUniConstraintStream<Solution_, A> extends BavetAbstractUniConstraintStream<Solution_, A> {

    private final Class<A> forEachClass;

    public BavetForEachUniConstraintStream(BavetConstraintFactory<Solution_> constraintFactory, Class<A> forEachClass,
                                           RetrievalSemantics retrievalSemantics) {
        super(constraintFactory, retrievalSemantics);
        this.forEachClass = forEachClass;
        if (forEachClass == null) {
            throw new IllegalArgumentException("The forEachClass (null) cannot be null.");
        }
    }

    @Override
    public boolean guaranteesDistinct() {
        return true;
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    public void collectActiveConstraintStreams(Set<BavetAbstractConstraintStream<Solution_>> constraintStreamSet) {
        constraintStreamSet.add(this);
    }

    @Override
    public ConstraintStream getTupleSource() {
        return this;
    }

    @Override
    public <Score_ extends Score<Score_>> void buildNode(NodeBuildHelper<Score_> buildHelper) {
        Consumer<UniTuple<A>> insert = buildHelper.getAggregatedInsert(childStreamList);
        Consumer<UniTuple<A>> retract = buildHelper.getAggregatedRetract(childStreamList);
        int outputStoreSize = buildHelper.extractTupleStoreSize(this);
        buildHelper.addNode(new ForEachUniNode<>(forEachClass, insert, retract, outputStoreSize));
    }

    // ************************************************************************
    // Equality for node sharing
    // ************************************************************************

    @Override
    public int hashCode() {
        return forEachClass.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof BavetForEachUniConstraintStream) {
            BavetForEachUniConstraintStream<?, ?> other = (BavetForEachUniConstraintStream<?, ?>) o;
            return forEachClass.equals(other.forEachClass);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "ForEach(" + forEachClass.getSimpleName() + ") with " + childStreamList.size() + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public Class<A> getForEachClass() {
        return forEachClass;
    }

}

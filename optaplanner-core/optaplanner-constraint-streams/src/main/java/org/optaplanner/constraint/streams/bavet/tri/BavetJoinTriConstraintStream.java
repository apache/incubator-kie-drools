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

package org.optaplanner.constraint.streams.bavet.tri;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.bi.BavetJoinBridgeBiConstraintStream;
import org.optaplanner.constraint.streams.bavet.bi.BiTuple;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.BavetJoinConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.NodeBuildHelper;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;
import org.optaplanner.constraint.streams.bavet.common.index.IndexerFactory;
import org.optaplanner.constraint.streams.bavet.common.index.JoinerUtils;
import org.optaplanner.constraint.streams.bavet.uni.BavetJoinBridgeUniConstraintStream;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.constraint.streams.tri.DefaultTriJoiner;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;

public final class BavetJoinTriConstraintStream<Solution_, A, B, C>
        extends BavetAbstractTriConstraintStream<Solution_, A, B, C>
        implements BavetJoinConstraintStream<Solution_> {

    private final BavetJoinBridgeBiConstraintStream<Solution_, A, B> leftParent;
    private final BavetJoinBridgeUniConstraintStream<Solution_, C> rightParent;

    private final DefaultTriJoiner<A, B, C> joiner;

    public BavetJoinTriConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetJoinBridgeBiConstraintStream<Solution_, A, B> leftParent,
            BavetJoinBridgeUniConstraintStream<Solution_, C> rightParent,
            DefaultTriJoiner<A, B, C> joiner) {
        super(constraintFactory, leftParent.getRetrievalSemantics());
        this.leftParent = leftParent;
        this.rightParent = rightParent;
        this.joiner = joiner;
    }

    @Override
    public boolean guaranteesDistinct() {
        return leftParent.guaranteesDistinct() && rightParent.guaranteesDistinct();
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    public void collectActiveConstraintStreams(Set<BavetAbstractConstraintStream<Solution_>> constraintStreamSet) {
        leftParent.collectActiveConstraintStreams(constraintStreamSet);
        rightParent.collectActiveConstraintStreams(constraintStreamSet);
        constraintStreamSet.add(this);
    }

    @Override
    public ConstraintStream getTupleSource() {
        return this;
    }

    @Override
    public <Score_ extends Score<Score_>> void buildNode(NodeBuildHelper<Score_> buildHelper) {
        int inputStoreIndexAB = buildHelper.reserveTupleStoreIndex(leftParent.getTupleSource());
        int inputStoreIndexC = buildHelper.reserveTupleStoreIndex(rightParent.getTupleSource());
        Consumer<TriTuple<A, B, C>> insert = buildHelper.getAggregatedInsert(childStreamList);
        Consumer<TriTuple<A, B, C>> retract = buildHelper.getAggregatedRetract(childStreamList);
        int outputStoreSize = buildHelper.extractTupleStoreSize(this);
        IndexerFactory indexerFactory = new IndexerFactory(joiner);
        Indexer<BiTuple<A, B>, Map<UniTuple<C>, TriTuple<A, B, C>>> indexerAB = indexerFactory.buildIndexer(true);
        Indexer<UniTuple<C>, Map<BiTuple<A, B>, TriTuple<A, B, C>>> indexerC = indexerFactory.buildIndexer(false);
        JoinTriNode<A, B, C> node = new JoinTriNode<>(
                JoinerUtils.combineLeftMappings(joiner), JoinerUtils.combineRightMappings(joiner),
                inputStoreIndexAB, inputStoreIndexC, insert, retract,
                outputStoreSize, indexerAB, indexerC);
        buildHelper.addNode(node);
        buildHelper.putInsertRetract(leftParent, node::insertLeft, node::retractLeft);
        buildHelper.putInsertRetract(rightParent, node::insertRight, node::retractRight);
    }

    // ************************************************************************
    // Equality for node sharing
    // ************************************************************************

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BavetJoinTriConstraintStream<?, ?, ?, ?> other = (BavetJoinTriConstraintStream<?, ?, ?, ?>) o;
        /*
         * Bridge streams do not implement equality because their equals() would have to point back to this stream,
         * resulting in StackOverflowError.
         * Therefore we need to check bridge parents to see where this join node comes from.
         */
        return Objects.equals(leftParent.getParent(), other.leftParent.getParent())
                && Objects.equals(rightParent.getParent(), other.rightParent.getParent())
                && Objects.equals(joiner, other.joiner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftParent.getParent(), rightParent.getParent(), joiner);
    }

    @Override
    public String toString() {
        return "TriJoin() with " + childStreamList.size() + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}

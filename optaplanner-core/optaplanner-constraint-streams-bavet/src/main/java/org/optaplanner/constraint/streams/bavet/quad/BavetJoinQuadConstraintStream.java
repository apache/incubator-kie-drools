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

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.BavetJoinConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.NodeBuildHelper;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;
import org.optaplanner.constraint.streams.bavet.common.index.IndexerFactory;
import org.optaplanner.constraint.streams.bavet.common.index.JoinerUtils;
import org.optaplanner.constraint.streams.bavet.tri.BavetJoinBridgeTriConstraintStream;
import org.optaplanner.constraint.streams.bavet.tri.TriTuple;
import org.optaplanner.constraint.streams.bavet.uni.BavetJoinBridgeUniConstraintStream;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.constraint.streams.quad.DefaultQuadJoiner;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;

public final class BavetJoinQuadConstraintStream<Solution_, A, B, C, D>
        extends BavetAbstractQuadConstraintStream<Solution_, A, B, C, D>
        implements BavetJoinConstraintStream<Solution_> {

    private final BavetJoinBridgeTriConstraintStream<Solution_, A, B, C> leftParent;
    private final BavetJoinBridgeUniConstraintStream<Solution_, D> rightParent;

    private final DefaultQuadJoiner<A, B, C, D> joiner;

    public BavetJoinQuadConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
                                         BavetJoinBridgeTriConstraintStream<Solution_, A, B, C> leftParent,
                                         BavetJoinBridgeUniConstraintStream<Solution_, D> rightParent,
                                         DefaultQuadJoiner<A, B, C, D> joiner) {
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
        Consumer<QuadTuple<A, B, C, D>> insert = buildHelper.getAggregatedInsert(childStreamList);
        Consumer<QuadTuple<A, B, C, D>> retract = buildHelper.getAggregatedRetract(childStreamList);
        int outputStoreSize = buildHelper.extractTupleStoreSize(this);
        IndexerFactory indexerFactory = new IndexerFactory(joiner);
        Indexer<TriTuple<A, B, C>, Map<UniTuple<D>, QuadTuple<A, B, C, D>>> indexerABC = indexerFactory.buildIndexer(true);
        Indexer<UniTuple<D>, Map<TriTuple<A, B, C>, QuadTuple<A, B, C, D>>> indexerD = indexerFactory.buildIndexer(false);
        JoinQuadNode<A, B, C, D> node = new JoinQuadNode<>(
                JoinerUtils.combineLeftMappings(joiner), JoinerUtils.combineRightMappings(joiner),
                inputStoreIndexAB, inputStoreIndexC, insert, retract,
                outputStoreSize, indexerABC, indexerD);
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
        BavetJoinQuadConstraintStream<?, ?, ?, ?, ?> other = (BavetJoinQuadConstraintStream<?, ?, ?, ?, ?>) o;
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
        return "QuadJoin() with " + childStreamList.size() + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}

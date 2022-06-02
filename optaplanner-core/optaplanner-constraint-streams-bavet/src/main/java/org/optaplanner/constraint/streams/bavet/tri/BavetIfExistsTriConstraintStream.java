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

import java.util.Set;
import java.util.function.Consumer;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.common.AbstractIfExistsNode.Counter;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.NodeBuildHelper;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;
import org.optaplanner.constraint.streams.bavet.common.index.IndexerFactory;
import org.optaplanner.constraint.streams.bavet.common.index.JoinerUtils;
import org.optaplanner.constraint.streams.bavet.uni.BavetIfExistsBridgeUniConstraintStream;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.constraint.streams.common.quad.DefaultQuadJoiner;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;

final class BavetIfExistsTriConstraintStream<Solution_, A, B, C, D>
        extends BavetAbstractTriConstraintStream<Solution_, A, B, C> {

    private final BavetAbstractTriConstraintStream<Solution_, A, B, C> parentABC;
    private final BavetIfExistsBridgeUniConstraintStream<Solution_, D> parentBridgeD;

    private final boolean shouldExist;
    private final DefaultQuadJoiner<A, B, C, D> joiner;
    private final QuadPredicate<A, B, C, D> filtering;

    public BavetIfExistsTriConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractTriConstraintStream<Solution_, A, B, C> parentABC,
            BavetIfExistsBridgeUniConstraintStream<Solution_, D> parentBridgeD,
            boolean shouldExist,
            DefaultQuadJoiner<A, B, C, D> joiner, QuadPredicate<A, B, C, D> filtering) {
        super(constraintFactory, parentABC.getRetrievalSemantics());
        this.parentABC = parentABC;
        this.parentBridgeD = parentBridgeD;
        this.shouldExist = shouldExist;
        this.joiner = joiner;
        this.filtering = filtering;
    }

    @Override
    public boolean guaranteesDistinct() {
        return parentABC.guaranteesDistinct();
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    public void collectActiveConstraintStreams(Set<BavetAbstractConstraintStream<Solution_>> constraintStreamSet) {
        parentABC.collectActiveConstraintStreams(constraintStreamSet);
        parentBridgeD.collectActiveConstraintStreams(constraintStreamSet);
        constraintStreamSet.add(this);
    }

    @Override
    public ConstraintStream getTupleSource() {
        return parentABC.getTupleSource();
    }

    @Override
    public <Score_ extends Score<Score_>> void buildNode(NodeBuildHelper<Score_> buildHelper) {
        int inputStoreIndexA = buildHelper.reserveTupleStoreIndex(parentABC.getTupleSource());
        int inputStoreIndexB = buildHelper.reserveTupleStoreIndex(parentBridgeD.getTupleSource());
        Consumer<TriTuple<A, B, C>> insert = buildHelper.getAggregatedInsert(childStreamList);
        Consumer<TriTuple<A, B, C>> retract = buildHelper.getAggregatedRetract(childStreamList);
        IndexerFactory indexerFactory = new IndexerFactory(joiner);
        Indexer<TriTuple<A, B, C>, Counter<TriTuple<A, B, C>>> indexerAB =
                indexerFactory.buildIndexer(true);
        Indexer<UniTuple<D>, Set<Counter<TriTuple<A, B, C>>>> indexerC =
                indexerFactory.buildIndexer(false);
        IfExistsTriWithUniNode<A, B, C, D> node = new IfExistsTriWithUniNode<>(shouldExist,
                JoinerUtils.combineLeftMappings(joiner), JoinerUtils.combineRightMappings(joiner),
                inputStoreIndexA, inputStoreIndexB,
                insert, retract,
                indexerAB, indexerC, filtering);
        buildHelper.addNode(node);
        buildHelper.putInsertUpdateRetract(this, node::insertLeft, node::updateLeft, node::retractLeft);
        buildHelper.putInsertUpdateRetract(parentBridgeD, node::insertRight, node::updateRight, node::retractRight);
    }

    // ************************************************************************
    // Equality for node sharing
    // ************************************************************************

    // TODO

    @Override
    public String toString() {
        return "IfExists() with " + childStreamList.size() + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}

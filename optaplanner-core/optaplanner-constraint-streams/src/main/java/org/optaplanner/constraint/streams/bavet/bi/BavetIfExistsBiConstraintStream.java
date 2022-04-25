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

package org.optaplanner.constraint.streams.bavet.bi;

import java.util.Set;
import java.util.function.Consumer;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.NodeBuildHelper;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;
import org.optaplanner.constraint.streams.bavet.common.index.IndexerFactory;
import org.optaplanner.constraint.streams.bavet.common.index.JoinerUtils;
import org.optaplanner.constraint.streams.bavet.uni.BavetIfExistsBridgeUniConstraintStream;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.constraint.streams.tri.DefaultTriJoiner;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;

public final class BavetIfExistsBiConstraintStream<Solution_, A, B, C>
        extends BavetAbstractBiConstraintStream<Solution_, A, B> {

    private final BavetAbstractBiConstraintStream<Solution_, A, B> parentAB;
    private final BavetIfExistsBridgeUniConstraintStream<Solution_, C> parentBridgeC;

    private final boolean shouldExist;
    private final DefaultTriJoiner<A, B, C> joiner;
    private final TriPredicate<A, B, C> filtering;

    public BavetIfExistsBiConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractBiConstraintStream<Solution_, A, B> parentAB,
            BavetIfExistsBridgeUniConstraintStream<Solution_, C> parentBridgeC,
            boolean shouldExist,
            DefaultTriJoiner<A, B, C> joiner, TriPredicate<A, B, C> filtering) {
        super(constraintFactory, parentAB.getRetrievalSemantics());
        this.parentAB = parentAB;
        this.parentBridgeC = parentBridgeC;
        this.shouldExist = shouldExist;
        this.joiner = joiner;
        this.filtering = filtering;
    }

    @Override
    public boolean guaranteesDistinct() {
        return parentAB.guaranteesDistinct();
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    public void collectActiveConstraintStreams(Set<BavetAbstractConstraintStream<Solution_>> constraintStreamSet) {
        parentAB.collectActiveConstraintStreams(constraintStreamSet);
        parentBridgeC.collectActiveConstraintStreams(constraintStreamSet);
        constraintStreamSet.add(this);
    }

    @Override
    public ConstraintStream getTupleSource() {
        return parentAB.getTupleSource();
    }

    @Override
    public <Score_ extends Score<Score_>> void buildNode(NodeBuildHelper<Score_> buildHelper) {
        int inputStoreIndexA = buildHelper.reserveTupleStoreIndex(parentAB.getTupleSource());
        int inputStoreIndexB = buildHelper.reserveTupleStoreIndex(parentBridgeC.getTupleSource());
        Consumer<BiTuple<A, B>> insert = buildHelper.getAggregatedInsert(childStreamList);
        Consumer<BiTuple<A, B>> retract = buildHelper.getAggregatedRetract(childStreamList);
        IndexerFactory indexerFactory = new IndexerFactory(joiner);
        Indexer<BiTuple<A, B>, IfExistsBiWithUniNode.Counter<A, B>> indexerAB = indexerFactory.buildIndexer(true);
        Indexer<UniTuple<C>, Set<IfExistsBiWithUniNode.Counter<A, B>>> indexerC = indexerFactory.buildIndexer(false);
        IfExistsBiWithUniNode<A, B, C> node = new IfExistsBiWithUniNode<>(shouldExist,
                JoinerUtils.combineLeftMappings(joiner), JoinerUtils.combineRightMappings(joiner),
                inputStoreIndexA, inputStoreIndexB,
                insert, retract,
                indexerAB, indexerC, filtering);
        buildHelper.addNode(node);
        buildHelper.putInsertRetract(this, node::insertAB, node::retractAB);
        buildHelper.putInsertRetract(parentBridgeC, node::insertC, node::retractC);
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

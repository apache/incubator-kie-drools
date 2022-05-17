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
import org.optaplanner.constraint.streams.penta.DefaultPentaJoiner;
import org.optaplanner.core.api.function.PentaPredicate;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;

final class BavetIfExistsQuadConstraintStream<Solution_, A, B, C, D, E>
        extends BavetAbstractQuadConstraintStream<Solution_, A, B, C, D> {

    private final BavetAbstractQuadConstraintStream<Solution_, A, B, C, D> parentABCD;
    private final BavetIfExistsBridgeUniConstraintStream<Solution_, E> parentBridgeE;

    private final boolean shouldExist;
    private final DefaultPentaJoiner<A, B, C, D, E> joiner;
    private final PentaPredicate<A, B, C, D, E> filtering;

    public BavetIfExistsQuadConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractQuadConstraintStream<Solution_, A, B, C, D> parentABCD,
            BavetIfExistsBridgeUniConstraintStream<Solution_, E> parentBridgeE,
            boolean shouldExist,
            DefaultPentaJoiner<A, B, C, D, E> joiner, PentaPredicate<A, B, C, D, E> filtering) {
        super(constraintFactory, parentABCD.getRetrievalSemantics());
        this.parentABCD = parentABCD;
        this.parentBridgeE = parentBridgeE;
        this.shouldExist = shouldExist;
        this.joiner = joiner;
        this.filtering = filtering;
    }

    @Override
    public boolean guaranteesDistinct() {
        return parentABCD.guaranteesDistinct();
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    public void collectActiveConstraintStreams(Set<BavetAbstractConstraintStream<Solution_>> constraintStreamSet) {
        parentABCD.collectActiveConstraintStreams(constraintStreamSet);
        parentBridgeE.collectActiveConstraintStreams(constraintStreamSet);
        constraintStreamSet.add(this);
    }

    @Override
    public ConstraintStream getTupleSource() {
        return parentABCD.getTupleSource();
    }

    @Override
    public <Score_ extends Score<Score_>> void buildNode(NodeBuildHelper<Score_> buildHelper) {
        int inputStoreIndexA = buildHelper.reserveTupleStoreIndex(parentABCD.getTupleSource());
        int inputStoreIndexB = buildHelper.reserveTupleStoreIndex(parentBridgeE.getTupleSource());
        Consumer<QuadTuple<A, B, C, D>> insert = buildHelper.getAggregatedInsert(childStreamList);
        Consumer<QuadTuple<A, B, C, D>> retract = buildHelper.getAggregatedRetract(childStreamList);
        IndexerFactory indexerFactory = new IndexerFactory(joiner);
        Indexer<QuadTuple<A, B, C, D>, Counter<QuadTuple<A, B, C, D>>> indexerABCD =
                indexerFactory.buildIndexer(true);
        Indexer<UniTuple<E>, Set<Counter<QuadTuple<A, B, C, D>>>> indexerE =
                indexerFactory.buildIndexer(false);
        IfExistsQuadWithUniNode<A, B, C, D, E> node = new IfExistsQuadWithUniNode<>(shouldExist,
                JoinerUtils.combineLeftMappings(joiner), JoinerUtils.combineRightMappings(joiner),
                inputStoreIndexA, inputStoreIndexB,
                insert, retract,
                indexerABCD, indexerE, filtering);
        buildHelper.addNode(node);
        buildHelper.putInsertRetract(this, node::insertLeft, node::retractLeft);
        buildHelper.putInsertRetract(parentBridgeE, node::insertRight, node::retractRight);
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

/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.bi.BavetAbstractBiConstraintStream;
import org.optaplanner.constraint.streams.bavet.bi.BiTuple;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.BavetJoinConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.NodeBuildHelper;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;
import org.optaplanner.constraint.streams.bavet.common.index.IndexerFactory;
import org.optaplanner.constraint.streams.bavet.uni.BavetAbstractUniConstraintStream;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;

public final class BavetJoinTriConstraintStream<Solution_, A, B, C>
        extends BavetAbstractTriConstraintStream<Solution_, A, B, C>
        implements BavetJoinConstraintStream<Solution_> {

    private final BavetAbstractBiConstraintStream<Solution_, A, B> leftParent;
    private final BavetAbstractUniConstraintStream<Solution_, C> rightParent;

    private final BiFunction<A, B, Object[]> leftMapping;
    private final Function<C, Object[]> rightMapping;
    private final IndexerFactory indexerFactory;

    public BavetJoinTriConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractBiConstraintStream<Solution_, A, B> leftParent,
            BavetAbstractUniConstraintStream<Solution_, C> rightParent,
            BiFunction<A, B, Object[]> leftMapping, Function<C, Object[]> rightMapping,
            IndexerFactory indexerFactory) {
        super(constraintFactory, leftParent.getRetrievalSemantics());
        this.leftParent = leftParent;
        this.rightParent = rightParent;
        this.leftMapping = leftMapping;
        this.rightMapping = rightMapping;
        this.indexerFactory = indexerFactory;
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
        Indexer<BiTuple<A, B>, Set<TriTuple<A, B, C>>> indexerAB = indexerFactory.buildIndexer(true);
        Indexer<UniTuple<C>, Set<TriTuple<A, B, C>>> indexerC = indexerFactory.buildIndexer(false);
        JoinTriNode<A, B, C> node = new JoinTriNode<>(
                leftMapping, rightMapping, inputStoreIndexAB, inputStoreIndexC,
                insert, retract, outputStoreSize,
                indexerAB, indexerC);
        buildHelper.addNode(node);
        buildHelper.putInsertRetract(leftParent, node::insertAB, node::retractAB);
        buildHelper.putInsertRetract(rightParent, node::insertC, node::retractC);
    }

    // ************************************************************************
    // Equality for node sharing
    // ************************************************************************

    // TODO

    @Override
    public String toString() {
        return "TriJoin() with " + childStreamList.size() + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}

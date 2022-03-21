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

package org.optaplanner.constraint.streams.bavet.bi;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.BavetJoinConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.NodeBuildHelper;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;
import org.optaplanner.constraint.streams.bavet.common.index.IndexerFactory;
import org.optaplanner.constraint.streams.bavet.uni.BavetAbstractUniConstraintStream;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.core.api.score.Score;

public final class BavetJoinBiConstraintStream<Solution_, A, B> extends BavetAbstractBiConstraintStream<Solution_, A, B>
        implements BavetJoinConstraintStream<Solution_> {

    private final BavetAbstractUniConstraintStream<Solution_, A> leftParent;
    private final BavetAbstractUniConstraintStream<Solution_, B> rightParent;

    private final Function<A, Object[]> leftMapping;
    private final Function<B, Object[]> rightMapping;
    private final IndexerFactory indexerFactory;

    public BavetJoinBiConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractUniConstraintStream<Solution_, A> leftParent,
            BavetAbstractUniConstraintStream<Solution_, B> rightParent,
            Function<A, Object[]> leftMapping, Function<B, Object[]> rightMapping,
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
    public <Score_ extends Score<Score_>> void buildNode(NodeBuildHelper<Score_> buildHelper) {
        Consumer<BiTuple<A, B>> insert = buildHelper.getAggregatedInsert(childStreamList);
        Consumer<BiTuple<A, B>> retract = buildHelper.getAggregatedRetract(childStreamList);
        Indexer<UniTuple<A>, Set<BiTuple<A, B>>> indexerA = indexerFactory.buildIndexer(true);
        Indexer<UniTuple<B>, Set<BiTuple<A, B>>> indexerB = indexerFactory.buildIndexer(false);
        JoinBiNode<A, B> node = new JoinBiNode<>(leftMapping, rightMapping,
                insert, retract,
                indexerA, indexerB);
        buildHelper.addNode(node);
        buildHelper.putInsertRetract(leftParent, node::insertA, node::retractA);
        buildHelper.putInsertRetract(rightParent, node::insertB, node::retractB);
    }

    // ************************************************************************
    // Equality for node sharing
    // ************************************************************************

    // TODO

    @Override
    public String toString() {
        return "BiJoin() with " + childStreamList.size() + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}

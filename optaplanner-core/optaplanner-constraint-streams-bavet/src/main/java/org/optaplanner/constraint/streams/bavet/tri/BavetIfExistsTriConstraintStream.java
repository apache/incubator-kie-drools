/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.constraint.streams.bavet.tri;

import java.util.Set;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.common.AbstractIfExistsNode;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.NodeBuildHelper;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.common.index.IndexerFactory;
import org.optaplanner.constraint.streams.bavet.common.index.JoinerUtils;
import org.optaplanner.constraint.streams.bavet.uni.BavetIfExistsBridgeUniConstraintStream;
import org.optaplanner.constraint.streams.common.quad.DefaultQuadJoiner;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.score.Score;

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
    public BavetAbstractConstraintStream<Solution_> getTupleSource() {
        return parentABC.getTupleSource();
    }

    @Override
    public <Score_ extends Score<Score_>> void buildNode(NodeBuildHelper<Score_> buildHelper) {
        TupleLifecycle<TriTuple<A, B, C>> downstream = buildHelper.getAggregatedTupleLifecycle(childStreamList);
        IndexerFactory indexerFactory = new IndexerFactory(joiner);
        AbstractIfExistsNode<TriTuple<A, B, C>, D> node = indexerFactory.hasJoiners()
                ? (filtering == null ? new IndexedIfExistsTriNode<>(shouldExist,
                        JoinerUtils.combineLeftMappings(joiner), JoinerUtils.combineRightMappings(joiner),
                        buildHelper.reserveTupleStoreIndex(parentABC.getTupleSource()),
                        buildHelper.reserveTupleStoreIndex(parentABC.getTupleSource()),
                        buildHelper.reserveTupleStoreIndex(parentBridgeD.getTupleSource()),
                        buildHelper.reserveTupleStoreIndex(parentBridgeD.getTupleSource()),
                        downstream, indexerFactory.buildIndexer(true), indexerFactory.buildIndexer(false))
                        : new IndexedIfExistsTriNode<>(shouldExist,
                                JoinerUtils.combineLeftMappings(joiner), JoinerUtils.combineRightMappings(joiner),
                                buildHelper.reserveTupleStoreIndex(parentABC.getTupleSource()),
                                buildHelper.reserveTupleStoreIndex(parentABC.getTupleSource()),
                                buildHelper.reserveTupleStoreIndex(parentABC.getTupleSource()),
                                buildHelper.reserveTupleStoreIndex(parentBridgeD.getTupleSource()),
                                buildHelper.reserveTupleStoreIndex(parentBridgeD.getTupleSource()),
                                buildHelper.reserveTupleStoreIndex(parentBridgeD.getTupleSource()),
                                downstream, indexerFactory.buildIndexer(true), indexerFactory.buildIndexer(false),
                                filtering))
                : (filtering == null ? new UnindexedIfExistsTriNode<>(shouldExist,
                        buildHelper.reserveTupleStoreIndex(parentABC.getTupleSource()),
                        buildHelper.reserveTupleStoreIndex(parentBridgeD.getTupleSource()), downstream)
                        : new UnindexedIfExistsTriNode<>(shouldExist,
                                buildHelper.reserveTupleStoreIndex(parentABC.getTupleSource()),
                                buildHelper.reserveTupleStoreIndex(parentABC.getTupleSource()),
                                buildHelper.reserveTupleStoreIndex(parentBridgeD.getTupleSource()),
                                buildHelper.reserveTupleStoreIndex(parentBridgeD.getTupleSource()),
                                downstream, filtering));
        buildHelper.addNode(node, this, parentBridgeD);
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

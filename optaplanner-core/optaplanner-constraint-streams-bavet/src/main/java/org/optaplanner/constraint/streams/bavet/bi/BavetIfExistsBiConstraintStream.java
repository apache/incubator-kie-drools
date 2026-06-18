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

package org.optaplanner.constraint.streams.bavet.bi;

import java.util.Set;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.common.AbstractIfExistsNode;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.NodeBuildHelper;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.common.index.IndexerFactory;
import org.optaplanner.constraint.streams.bavet.common.index.JoinerUtils;
import org.optaplanner.constraint.streams.bavet.uni.BavetIfExistsBridgeUniConstraintStream;
import org.optaplanner.constraint.streams.common.tri.DefaultTriJoiner;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.Score;

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
    public BavetAbstractConstraintStream<Solution_> getTupleSource() {
        return parentAB.getTupleSource();
    }

    @Override
    public <Score_ extends Score<Score_>> void buildNode(NodeBuildHelper<Score_> buildHelper) {
        TupleLifecycle<BiTuple<A, B>> downstream = buildHelper.getAggregatedTupleLifecycle(childStreamList);
        IndexerFactory indexerFactory = new IndexerFactory(joiner);
        AbstractIfExistsNode<BiTuple<A, B>, C> node = indexerFactory.hasJoiners()
                ? (filtering == null ? new IndexedIfExistsBiNode<>(shouldExist,
                        JoinerUtils.combineLeftMappings(joiner), JoinerUtils.combineRightMappings(joiner),
                        buildHelper.reserveTupleStoreIndex(parentAB.getTupleSource()),
                        buildHelper.reserveTupleStoreIndex(parentAB.getTupleSource()),
                        buildHelper.reserveTupleStoreIndex(parentBridgeC.getTupleSource()),
                        buildHelper.reserveTupleStoreIndex(parentBridgeC.getTupleSource()),
                        downstream, indexerFactory.buildIndexer(true), indexerFactory.buildIndexer(false))
                        : new IndexedIfExistsBiNode<>(shouldExist,
                                JoinerUtils.combineLeftMappings(joiner), JoinerUtils.combineRightMappings(joiner),
                                buildHelper.reserveTupleStoreIndex(parentAB.getTupleSource()),
                                buildHelper.reserveTupleStoreIndex(parentAB.getTupleSource()),
                                buildHelper.reserveTupleStoreIndex(parentAB.getTupleSource()),
                                buildHelper.reserveTupleStoreIndex(parentBridgeC.getTupleSource()),
                                buildHelper.reserveTupleStoreIndex(parentBridgeC.getTupleSource()),
                                buildHelper.reserveTupleStoreIndex(parentBridgeC.getTupleSource()),
                                downstream, indexerFactory.buildIndexer(true), indexerFactory.buildIndexer(false),
                                filtering))
                : (filtering == null ? new UnindexedIfExistsBiNode<>(shouldExist,
                        buildHelper.reserveTupleStoreIndex(parentAB.getTupleSource()),
                        buildHelper.reserveTupleStoreIndex(parentBridgeC.getTupleSource()), downstream)
                        : new UnindexedIfExistsBiNode<>(shouldExist,
                                buildHelper.reserveTupleStoreIndex(parentAB.getTupleSource()),
                                buildHelper.reserveTupleStoreIndex(parentAB.getTupleSource()),
                                buildHelper.reserveTupleStoreIndex(parentBridgeC.getTupleSource()),
                                buildHelper.reserveTupleStoreIndex(parentBridgeC.getTupleSource()),
                                downstream, filtering));
        buildHelper.addNode(node, this, parentBridgeC);
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

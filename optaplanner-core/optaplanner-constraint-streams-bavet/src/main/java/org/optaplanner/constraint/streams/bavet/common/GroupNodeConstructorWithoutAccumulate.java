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

package org.optaplanner.constraint.streams.bavet.common;

import java.util.List;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;
import org.optaplanner.core.config.solver.EnvironmentMode;

final class GroupNodeConstructorWithoutAccumulate<Tuple_ extends Tuple> implements GroupNodeConstructor<Tuple_> {

    private final NodeConstructorWithoutAccumulate<Tuple_> nodeConstructorFunction;

    public GroupNodeConstructorWithoutAccumulate(NodeConstructorWithoutAccumulate<Tuple_> nodeConstructorFunction) {
        this.nodeConstructorFunction = nodeConstructorFunction;
    }

    @Override
    public <Solution_, Score_ extends Score<Score_>> void build(NodeBuildHelper<Score_> buildHelper,
            BavetAbstractConstraintStream<Solution_> parentTupleSource,
            BavetAbstractConstraintStream<Solution_> groupStream, List<? extends ConstraintStream> groupStreamChildList,
            BavetAbstractConstraintStream<Solution_> bridgeStream, List<? extends ConstraintStream> bridgeStreamChildList,
            EnvironmentMode environmentMode) {
        if (!bridgeStreamChildList.isEmpty()) {
            throw new IllegalStateException("Impossible state: the stream (" + bridgeStream
                    + ") has an non-empty childStreamList (" + bridgeStreamChildList + ") but it's a groupBy bridge.");
        }
        int groupStoreIndex = buildHelper.reserveTupleStoreIndex(parentTupleSource);
        TupleLifecycle<Tuple_> tupleLifecycle = buildHelper.getAggregatedTupleLifecycle(groupStreamChildList);
        int outputStoreSize = buildHelper.extractTupleStoreSize(groupStream);
        var node = nodeConstructorFunction.apply(groupStoreIndex, tupleLifecycle, outputStoreSize, environmentMode);
        buildHelper.addNode(node, bridgeStream);
    }
}

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

public interface GroupNodeConstructor<Tuple_ extends Tuple> {

    static <Tuple_ extends Tuple> GroupNodeConstructor<Tuple_>
            of(NodeConstructorWithAccumulate<Tuple_> nodeConstructorWithAccumulate) {
        return new GroupNodeConstructorWithAccumulate<>(nodeConstructorWithAccumulate);
    }

    static <Tuple_ extends Tuple> GroupNodeConstructor<Tuple_>
            of(NodeConstructorWithoutAccumulate<Tuple_> nodeConstructorWithoutAccumulate) {
        return new GroupNodeConstructorWithoutAccumulate<>(nodeConstructorWithoutAccumulate);
    }

    @FunctionalInterface
    interface NodeConstructorWithAccumulate<Tuple_ extends Tuple> {

        AbstractNode apply(int groupStoreIndex, int undoStoreIndex, TupleLifecycle<Tuple_> nextNodesTupleLifecycle,
                int outputStoreSize, EnvironmentMode environmentMode);

    }

    @FunctionalInterface
    interface NodeConstructorWithoutAccumulate<Tuple_ extends Tuple> {

        AbstractNode apply(int groupStoreIndex, TupleLifecycle<Tuple_> nextNodesTupleLifecycle, int outputStoreSize,
                EnvironmentMode environmentMode);

    }

    <Solution_, Score_ extends Score<Score_>> void build(NodeBuildHelper<Score_> buildHelper,
            BavetAbstractConstraintStream<Solution_> parentTupleSource,
            BavetAbstractConstraintStream<Solution_> groupStream, List<? extends ConstraintStream> groupStreamChildList,
            BavetAbstractConstraintStream<Solution_> thisStream,
            List<? extends ConstraintStream> thisStreamChildList, EnvironmentMode environmentMode);

}

/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.drools.quad;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsAbstractBiConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.tri.DroolsAbstractTriConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsAbstractUniConstraintStream;

public final class DroolsGroupingQuadConstraintStream<Solution_, NewA, NewB, NewC, NewD>
        extends DroolsAbstractQuadConstraintStream<Solution_, NewA, NewB, NewC, NewD> {

    private final DroolsQuadCondition<NewA, NewB, NewC, NewD, ?> condition;

    public <A, ResultContainerC, ResultContainerD> DroolsGroupingQuadConstraintStream(
            DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractUniConstraintStream<Solution_, A> parent, Function<A, NewA> groupKeyAMapping,
            Function<A, NewB> groupKeyBMapping, UniConstraintCollector<A, ResultContainerC, NewC> collectorC,
            UniConstraintCollector<A, ResultContainerD, NewD> collectorD) {
        super(constraintFactory, parent);
        this.condition = parent.getCondition().andGroupBiWithCollectBi(groupKeyAMapping, groupKeyBMapping,
                collectorC, collectorD);
    }

    public <A, B, ResultContainerC, ResultContainerD> DroolsGroupingQuadConstraintStream(
            DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractBiConstraintStream<Solution_, A, B> parent, BiFunction<A, B, NewA> groupKeyAMapping,
            BiFunction<A, B, NewB> groupKeyBMapping, BiConstraintCollector<A, B, ResultContainerC, NewC> collectorC,
            BiConstraintCollector<A, B, ResultContainerD, NewD> collectorD) {
        super(constraintFactory, parent);
        this.condition = parent.getCondition().andGroupBiWithCollectBi(groupKeyAMapping, groupKeyBMapping,
                collectorC, collectorD);
    }

    public <A, B, C, ResultContainerC, ResultContainerD> DroolsGroupingQuadConstraintStream(
            DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent, TriFunction<A, B, C, NewA> groupKeyAMapping,
            TriFunction<A, B, C, NewB> groupKeyBMapping,
            TriConstraintCollector<A, B, C, ResultContainerC, NewC> collectorC,
            TriConstraintCollector<A, B, C, ResultContainerD, NewD> collectorD) {
        super(constraintFactory, parent);
        this.condition = parent.getCondition().andGroupBiWithCollectBi(groupKeyAMapping, groupKeyBMapping,
                collectorC, collectorD);
    }

    public <A, B, C, D, ResultContainerC, ResultContainerD> DroolsGroupingQuadConstraintStream(
            DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> parent,
            QuadFunction<A, B, C, D, NewA> groupKeyAMapping,
            QuadFunction<A, B, C, D, NewB> groupKeyBMapping,
            QuadConstraintCollector<A, B, C, D, ResultContainerC, NewC> collectorC,
            QuadConstraintCollector<A, B, C, D, ResultContainerD, NewD> collectorD) {
        super(constraintFactory, parent);
        this.condition = parent.getCondition().andGroupBiWithCollectBi(groupKeyAMapping, groupKeyBMapping,
                collectorC, collectorD);
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public DroolsQuadCondition<NewA, NewB, NewC, NewD, ?> getCondition() {
        return condition;
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public String toString() {
        return "QuadGroupBy() with " + getChildStreams().size() + " children";
    }

}

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

package org.optaplanner.core.impl.score.stream.drools.tri;

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
import org.optaplanner.core.impl.score.stream.drools.quad.DroolsAbstractQuadConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsAbstractUniConstraintStream;

public class DroolsGroupingTriConstraintStream<Solution_, NewA, NewB, NewC>
        extends DroolsAbstractTriConstraintStream<Solution_, NewA, NewB, NewC> {

    private final DroolsTriCondition<NewA, NewB, NewC, ?> condition;

    public <A, ResultContainer_> DroolsGroupingTriConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractUniConstraintStream<Solution_, A> parent, Function<A, NewA> groupKeyAMapping,
            Function<A, NewB> groupKeyBMapping, UniConstraintCollector<A, ResultContainer_, NewC> collector) {
        super(constraintFactory, parent);
        this.condition = parent.getCondition().andGroupBiWithCollect(groupKeyAMapping, groupKeyBMapping, collector);
    }

    public <A, B, ResultContainer_> DroolsGroupingTriConstraintStream(
            DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractBiConstraintStream<Solution_, A, B> parent, BiFunction<A, B, NewA> groupKeyAMapping,
            BiFunction<A, B, NewB> groupKeyBMapping, BiConstraintCollector<A, B, ResultContainer_, NewC> collector) {
        super(constraintFactory, parent);
        this.condition = parent.getCondition().andGroupBiWithCollect(groupKeyAMapping, groupKeyBMapping, collector);
    }

    public <A, B, C, ResultContainer_> DroolsGroupingTriConstraintStream(
            DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent, TriFunction<A, B, C, NewA> groupKeyAMapping,
            TriFunction<A, B, C, NewB> groupKeyBMapping,
            TriConstraintCollector<A, B, C, ResultContainer_, NewC> collector) {
        super(constraintFactory, parent);
        this.condition = parent.getCondition().andGroupBiWithCollect(groupKeyAMapping, groupKeyBMapping, collector);
    }

    public <A, B, C, D, ResultContainer_> DroolsGroupingTriConstraintStream(
            DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> parent,
            QuadFunction<A, B, C, D, NewA> groupKeyAMapping, QuadFunction<A, B, C, D, NewB> groupKeyBMapping,
            QuadConstraintCollector<A, B, C, D, ResultContainer_, NewC> collector) {
        super(constraintFactory, parent);
        this.condition = parent.getCondition().andGroupBiWithCollect(groupKeyAMapping, groupKeyBMapping, collector);
    }

    @Override
    public DroolsTriCondition<NewA, NewB, NewC, ?> getCondition() {
        return condition;
    }

    @Override
    public String toString() {
        return "TriGroup() with " + getChildStreams().size() + " children";
    }
}

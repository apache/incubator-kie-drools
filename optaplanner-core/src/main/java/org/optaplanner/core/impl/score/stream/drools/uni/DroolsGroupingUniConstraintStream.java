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

package org.optaplanner.core.impl.score.stream.drools.uni;

import java.util.List;
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
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.quad.DroolsAbstractQuadConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.tri.DroolsAbstractTriConstraintStream;

public final class DroolsGroupingUniConstraintStream<Solution_, NewA>
        extends DroolsAbstractUniConstraintStream<Solution_, NewA> {

    private final DroolsAbstractConstraintStream<Solution_> parent;
    private final DroolsUniCondition<NewA, ?> condition;

    public <A> DroolsGroupingUniConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractUniConstraintStream<Solution_, A> parent, Function<A, NewA> groupKeyMapping) {
        super(constraintFactory);
        this.parent = parent;
        this.condition = parent.getCondition().andGroup(groupKeyMapping);
    }

    public <A, ResultContainer_> DroolsGroupingUniConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractUniConstraintStream<Solution_, A> parent,
            UniConstraintCollector<A, ResultContainer_, NewA> collector) {
        super(constraintFactory);
        this.parent = parent;
        this.condition = parent.getCondition().andCollect(collector);
    }

    public <A, B> DroolsGroupingUniConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractBiConstraintStream<Solution_, A, B> parent, BiFunction<A, B, NewA> groupKeyMapping) {
        super(constraintFactory);
        this.parent = parent;
        this.condition = parent.getCondition().andGroup(groupKeyMapping);
    }

    public <A, B, ResultContainer_> DroolsGroupingUniConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractBiConstraintStream<Solution_, A, B> parent,
            BiConstraintCollector<A, B, ResultContainer_, NewA> collector) {
        super(constraintFactory);
        this.parent = parent;
        this.condition = parent.getCondition().andCollect(collector);
    }

    public <A, B, C, ResultContainer_> DroolsGroupingUniConstraintStream(
            DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent,
            TriConstraintCollector<A, B, C, ResultContainer_, NewA> collector) {
        super(constraintFactory);
        this.parent = parent;
        this.condition = parent.getCondition().andCollect(collector);
    }

    public <A, B, C> DroolsGroupingUniConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent, TriFunction<A, B, C, NewA> groupKeyMapping) {
        super(constraintFactory);
        this.parent = parent;
        this.condition = parent.getCondition().andGroup(groupKeyMapping);
    }

    public <A, B, C, D, ResultContainer_> DroolsGroupingUniConstraintStream(
            DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> parent,
            QuadConstraintCollector<A, B, C, D, ResultContainer_, NewA> collector) {
        super(constraintFactory);
        this.parent = parent;
        this.condition = parent.getCondition().andCollect(collector);
    }

    public <A, B, C, D> DroolsGroupingUniConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> parent,
            QuadFunction<A, B, C, D, NewA> groupKeyMapping) {
        super(constraintFactory);
        this.parent = parent;
        this.condition = parent.getCondition().andGroup(groupKeyMapping);
    }

    @Override
    public List<DroolsFromUniConstraintStream<Solution_, Object>> getFromStreamList() {
        return parent.getFromStreamList();
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public DroolsUniCondition<NewA, ?> getCondition() {
        return condition;
    }

    @Override
    public String toString() {
        return "GroupBy()";
    }
}

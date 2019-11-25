/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import java.util.function.Function;

import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;

public final class DroolsGroupingUniConstraintStream<Solution_, A, NewA>
        extends DroolsAbstractUniConstraintStream<Solution_, NewA> {

    private final DroolsAbstractUniConstraintStream<Solution_, A> parent;
    private final DroolsUniCondition<NewA> condition;

    public DroolsGroupingUniConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractUniConstraintStream<Solution_, A> parent, Function<A, NewA> groupKeyMapping) {
        super(constraintFactory);
        this.parent = parent;
        this.condition = parent.getCondition().andGroup(groupKeyMapping);
    }

    public <ResultContainer_> DroolsGroupingUniConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractUniConstraintStream<Solution_, A> parent,
            UniConstraintCollector<A, ResultContainer_, NewA> collector) {
        super(constraintFactory);
        this.parent = parent;
        this.condition = parent.getCondition().andCollect(collector);
    }

    @Override
    public List<DroolsFromUniConstraintStream<Solution_, Object>> getFromStreamList() {
        return parent.getFromStreamList();
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public DroolsUniCondition<NewA> getCondition() {
        return condition;
    }

    @Override
    public String toString() {
        return "GroupBy()";
    }
}

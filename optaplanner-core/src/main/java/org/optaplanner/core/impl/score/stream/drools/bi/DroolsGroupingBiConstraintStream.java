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

package org.optaplanner.core.impl.score.stream.drools.bi;

import java.util.function.Function;

import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsAbstractUniConstraintStream;

public class DroolsGroupingBiConstraintStream<Solution_, A, NewA, ResultContainer_, NewB>
        extends DroolsAbstractBiConstraintStream<Solution_, NewA, NewB> {

    private final DroolsAbstractUniConstraintStream<Solution_, A> parent;
    private final DroolsBiCondition<NewA, NewB> condition;

    public DroolsGroupingBiConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractUniConstraintStream<Solution_, A> parent, Function<A, NewA> groupKeyMapping,
            UniConstraintCollector<A, ResultContainer_, NewB> collector) {
        super(constraintFactory);
        this.parent = parent;
        this.condition = parent.getCondition().andGroupWithCollect(groupKeyMapping, collector);
    }

    @Override
    public DroolsBiCondition<NewA, NewB> getCondition() {
        return condition;
    }

    @Override
    protected DroolsAbstractConstraintStream<Solution_> getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "BiGroup() with " + getChildStreams().size() + " children";
    }
}

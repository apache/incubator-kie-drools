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

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import org.drools.model.Variable;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractGroupBy;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractGroupByAccumulator;

public class DroolsUniToTriGroupByAccumulator<A, NewA, NewB, NewC>
        extends DroolsAbstractGroupByAccumulator<A> {

    private final Function<A, NewA> groupKeyAMapping;
    private final Function<A, NewB> groupKeyBMapping;
    private final UniConstraintCollector<A, ?, NewC> collector;
    private final Variable<A> aVariable;

    public DroolsUniToTriGroupByAccumulator(Function<A, NewA> groupKeyAMapping, Function<A, NewB> groupKeyBMapping,
            UniConstraintCollector<A, ?, NewC> collector, Variable<A> aVariable) {
        this.groupKeyAMapping = requireNonNull(groupKeyAMapping);
        this.groupKeyBMapping = requireNonNull(groupKeyBMapping);
        this.collector = requireNonNull(collector);
        this.aVariable = requireNonNull(aVariable);
    }

    @Override
    protected DroolsAbstractGroupBy<A, ?> newContext() {
        return new DroolsUniToTriGroupBy<>(groupKeyAMapping, groupKeyBMapping, collector);
    }

    @Override
    protected <X> A createInput(Function<Variable<X>, X> valueFinder) {
        return materialize(aVariable, valueFinder);
    }

}

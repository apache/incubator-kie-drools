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

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import org.drools.model.Variable;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractGroupBy;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractGroupByAccumulator;
import org.optaplanner.core.impl.score.stream.drools.common.TriTuple;
import org.optaplanner.core.impl.score.stream.tri.DefaultTriConstraintCollector;

public class DroolsTriGroupByAccumulator<A, B, C, NewA, NewB, NewC>
        extends DroolsAbstractGroupByAccumulator<TriTuple<A, B, C>> {

    private final TriFunction<A, B, C, NewA> groupKeyAMapping;
    private final TriFunction<A, B, C, NewB> groupKeyBMapping;
    private final TriConstraintCollector<A, B, C, ?, NewC> collector;
    private final Variable<A> aVariable;
    private final Variable<B> bVariable;
    private final Variable<C> cVariable;

    public DroolsTriGroupByAccumulator(TriFunction<A, B, C, NewA> groupKeyAMapping,
            TriFunction<A, B, C, NewB> groupKeyBMapping, TriConstraintCollector<A, B, C, ?, NewC> collector,
            Variable<A> aVariable, Variable<B> bVariable, Variable<C> cVariable) {
        this.groupKeyAMapping = requireNonNull(groupKeyAMapping);
        this.groupKeyBMapping = requireNonNull(groupKeyBMapping);
        // Null collector means we're only re-grouping without using a collector.
        this.collector = collector != null ? collector : DefaultTriConstraintCollector.noop();
        this.aVariable = requireNonNull(aVariable);
        this.bVariable = requireNonNull(bVariable);
        this.cVariable = requireNonNull(cVariable);
    }

    @Override
    protected DroolsAbstractGroupBy<TriTuple<A, B, C>, ?> newContext() {
        return new DroolsTriGroupBy<>(groupKeyAMapping, groupKeyBMapping, collector);
    }

    @Override
    protected <X> TriTuple<A, B, C> createInput(Function<Variable<X>, X> valueFinder) {
        final A a = materialize(aVariable, valueFinder);
        final B b = materialize(bVariable, valueFinder);
        final C c = materialize(cVariable, valueFinder);
        return new TriTuple<>(a, b, c);
    }

}

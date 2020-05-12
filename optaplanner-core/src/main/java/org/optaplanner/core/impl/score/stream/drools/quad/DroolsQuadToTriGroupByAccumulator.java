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

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import org.drools.model.Variable;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractGroupBy;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractGroupByAccumulator;
import org.optaplanner.core.impl.score.stream.drools.common.QuadTuple;
import org.optaplanner.core.impl.score.stream.quad.DefaultQuadConstraintCollector;

public class DroolsQuadToTriGroupByAccumulator<A, B, C, D, NewA, NewB, NewC>
        extends DroolsAbstractGroupByAccumulator<QuadTuple<A, B, C, D>> {

    private final QuadConstraintCollector<A, B, C, D, ?, NewC> collector;
    private final QuadFunction<A, B, C, D, NewA> groupKeyAMapping;
    private final QuadFunction<A, B, C, D, NewB> groupKeyBMapping;
    private final Variable<A> aVariable;
    private final Variable<B> bVariable;
    private final Variable<C> cVariable;
    private final Variable<D> dVariable;

    public DroolsQuadToTriGroupByAccumulator(QuadFunction<A, B, C, D, NewA> groupKeyAMapping,
            QuadFunction<A, B, C, D, NewB> groupKeyBMapping, QuadConstraintCollector<A, B, C, D, ?, NewC> collector,
            Variable<A> aVariable, Variable<B> bVariable, Variable<C> cVariable, Variable<D> dVariable) {
        this.groupKeyAMapping = requireNonNull(groupKeyAMapping);
        this.groupKeyBMapping = requireNonNull(groupKeyBMapping);
        // Null collector means we're only re-grouping without using a collector.
        this.collector = collector != null ? collector : DefaultQuadConstraintCollector.noop();
        this.aVariable = requireNonNull(aVariable);
        this.bVariable = requireNonNull(bVariable);
        this.cVariable = requireNonNull(cVariable);
        this.dVariable = requireNonNull(dVariable);
    }

    @Override
    protected DroolsAbstractGroupBy<QuadTuple<A, B, C, D>, ?> newContext() {
        return new DroolsQuadToTriGroupBy<>(groupKeyAMapping, groupKeyBMapping, collector);
    }

    @Override
    protected <X> QuadTuple<A, B, C, D> createInput(Function<Variable<X>, X> valueFinder) {
        final A a = materialize(aVariable, valueFinder);
        final B b = materialize(bVariable, valueFinder);
        final C c = materialize(cVariable, valueFinder);
        final D d = materialize(dVariable, valueFinder);
        return new QuadTuple<>(a, b, c, d);
    }

}

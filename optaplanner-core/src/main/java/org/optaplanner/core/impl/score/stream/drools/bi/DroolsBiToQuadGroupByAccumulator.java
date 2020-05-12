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

package org.optaplanner.core.impl.score.stream.drools.bi;

import static java.util.Objects.requireNonNull;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.drools.model.Variable;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.common.BiTuple;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractGroupBy;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractGroupByAccumulator;

public class DroolsBiToQuadGroupByAccumulator<A, B, NewA, NewB, NewC, NewD>
        extends DroolsAbstractGroupByAccumulator<BiTuple<A, B>> {

    private final BiFunction<A, B, NewA> groupKeyAMapping;
    private final BiFunction<A, B, NewB> groupKeyBMapping;
    private final BiConstraintCollector<A, B, ?, NewC> collectorC;
    private final BiConstraintCollector<A, B, ?, NewD> collectorD;
    private final Variable<A> aVariable;
    private final Variable<B> bVariable;

    public DroolsBiToQuadGroupByAccumulator(BiFunction<A, B, NewA> groupKeyAMapping,
            BiFunction<A, B, NewB> groupKeyBMapping, BiConstraintCollector<A, B, ?, NewC> collectorC,
            BiConstraintCollector<A, B, ?, NewD> collectorD, Variable<A> aVariable, Variable<B> bVariable) {
        this.groupKeyAMapping = requireNonNull(groupKeyAMapping);
        this.groupKeyBMapping = requireNonNull(groupKeyBMapping);
        this.collectorC = requireNonNull(collectorC);
        this.collectorD = requireNonNull(collectorD);
        this.aVariable = requireNonNull(aVariable);
        this.bVariable = requireNonNull(bVariable);
    }

    @Override
    protected DroolsAbstractGroupBy<BiTuple<A, B>, ?> newContext() {
        return new DroolsBiToQuadGroupBy<>(groupKeyAMapping, groupKeyBMapping, collectorC, collectorD);
    }

    @Override
    protected <X> BiTuple<A, B> createInput(Function<Variable<X>, X> valueFinder) {
        A a = materialize(aVariable, valueFinder);
        B b = materialize(bVariable, valueFinder);
        return new BiTuple<>(a, b);
    }

}

/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.drools.common;

import java.util.Objects;
import java.util.function.Function;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.Tuple;
import org.drools.core.rule.Declaration;
import org.drools.model.Variable;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;

final class BiAccumulator<A, B, ResultContainer_, Result_> extends AbstractAccumulator<ResultContainer_, Result_> {

    private final String varA;
    private final String varB;
    private final TriFunction<ResultContainer_, A, B, Runnable> accumulator;

    private Function<Tuple, A> valueExtractorA;
    private Function<Tuple, B> valueExtractorB;

    public BiAccumulator(Variable<A> varA, Variable<B> varB,
            BiConstraintCollector<A, B, ResultContainer_, Result_> collector) {
        super(collector.supplier(), collector.finisher());
        this.accumulator = Objects.requireNonNull(collector.accumulator());
        this.varA = varA.getName();
        this.varB = varB.getName();
    }

    @Override
    protected Runnable accumulate(ResultContainer_ context, Tuple leftTuple, InternalFactHandle handle,
            Declaration[] innerDeclarations) {
        A a = valueExtractorA.apply(leftTuple);
        B b = valueExtractorB.apply(leftTuple);
        return accumulator.apply(context, a, b);
    }

    @Override
    protected void initialize(Tuple leftTuple, Declaration[] innerDeclarations) {
        for (Declaration declaration : innerDeclarations) {
            if (declaration.getBindingName().equals(varA)) {
                valueExtractorA = getValueExtractor(declaration, leftTuple);
            } else if (declaration.getBindingName().equals(varB)) {
                valueExtractorB = getValueExtractor(declaration, leftTuple);
            }
        }
    }

}

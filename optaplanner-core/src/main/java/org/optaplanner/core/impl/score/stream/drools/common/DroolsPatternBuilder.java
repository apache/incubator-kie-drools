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

package org.optaplanner.core.impl.score.stream.drools.common;

import static org.drools.model.PatternDSL.pattern;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import org.drools.model.DeclarationSource;
import org.drools.model.PatternDSL;
import org.drools.model.Variable;

/**
 * Instances of {@link PatternDSL.PatternDef} are mutable and as such can not be reused by constraint streams that could
 * result in mutually different rules.
 * However, multiple rules can be based on the same constraint streams through stream sharing.
 * This class makes it nearly impossible for {@link PatternDSL.PatternDef}s to be unknowingly shared.
 *
 * <p>
 * Instances of this class are immutable.
 * Mutating methods (such as {@link #expand(UnaryOperator)}) create a new instance, effectively preventing
 * streams from sharing patterns.
 * Patterns are only built when {@link #build()} or {@link #build(DeclarationSource)} is called. Callers must ensure
 * that {@link PatternDSL.PatternDef}s obtained by these methods are never mutated, as that defeats the purpose.
 *
 * <p>
 * Example:
 * Stream 1 joins A and B.
 * Stream 2 filters Stream 1.
 * Stream 3 also filters Stream 1, but with a different filter.
 * If the pattern inside Stream 1 was shared by Stream 2 and Stream 3, filters from Stream 2 would also apply to
 * Stream 3, and vice versa.
 * Therefore, Stream 2 and Stream 3 each get their own immutable copy of Stream 1's {@link DroolsPatternBuilder}, one
 * which only applies their individual filters.
 *
 * @param <T> generic type of the pattern to be eventually created by this builder
 */
public final class DroolsPatternBuilder<T> {

    private final Variable<T> baseVariable;
    private final Function<PatternDSL.PatternDef<T>, PatternDSL.PatternDef<T>> builder;

    /**
     * @param baseVariable will be used during the {@link PatternDSL#pattern(Variable)} call within {@link #build()} or
     *        {@link #build(DeclarationSource)}
     */
    public DroolsPatternBuilder(Variable<T> baseVariable) {
        this.baseVariable = baseVariable;
        this.builder = null;
    }

    private DroolsPatternBuilder(Variable<T> baseVariable,
            Function<PatternDSL.PatternDef<T>, PatternDSL.PatternDef<T>> builder) {
        this.baseVariable = baseVariable;
        this.builder = builder;
    }

    /**
     * Mutate the existing {@link PatternDSL.PatternDef}, adding a new operation, such as a new filter or a new variable
     * binding.
     *
     * @return copy of the {@link DroolsPatternBuilder} with the new expanding operation included.
     */
    public DroolsPatternBuilder<T> expand(UnaryOperator<PatternDSL.PatternDef<T>> expander) {
        Function<PatternDSL.PatternDef<T>, PatternDSL.PatternDef<T>> newBuilder = builder == null ? expander
                : builder.andThen(expander);
        return new DroolsPatternBuilder<>(baseVariable, newBuilder);
    }

    private PatternDSL.PatternDef<T> build(PatternDSL.PatternDef<T> basePattern) {
        if (builder == null) {
            return basePattern;
        } else {
            return builder.apply(basePattern);
        }
    }

    /**
     * Builds a new instance of the pattern, with all the {@link UnaryOperator}s from
     * {@link #expand(UnaryOperator)} applied.
     *
     * @return should no longer be mutated to guarantee that rules can not influence one another
     */
    public PatternDSL.PatternDef<T> build() {
        return build(pattern(baseVariable));
    }

    /**
     * Builds a new instance of the pattern, with all the {@link UnaryOperator}s from
     * {@link #expand(UnaryOperator)} applied.
     *
     * @param declarationSource will be applied to the newly created pattern
     * @return should no longer be mutated to guarantee that rules can not influence one another
     */
    public PatternDSL.PatternDef<T> build(DeclarationSource declarationSource) {
        return build(pattern(baseVariable, declarationSource));
    }

    public Variable<T> getBaseVariable() {
        return baseVariable;
    }

}

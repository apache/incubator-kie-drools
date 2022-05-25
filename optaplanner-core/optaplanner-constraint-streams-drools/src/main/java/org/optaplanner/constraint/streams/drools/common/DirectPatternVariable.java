/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import static org.drools.model.PatternDSL.pattern;

import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;

import org.drools.model.PatternDSL;
import org.drools.model.Variable;
import org.drools.model.view.ViewItem;

/**
 * Represents a single variable with all of its patterns in the left hand side of a Drools rule.
 *
 * <p>
 * Consider the following simple bivariate rule, in the equivalent DRL:
 *
 * <pre>
 * {@code
 *  rule "Simple bivariate rule"
 *  when
 *      $a: Something()
 *      $b: SomethingElse()
 *  then
 *      // Do something with the $a and $b variables.
 *  end
 * }
 * </pre>
 *
 * In this rule, each variable with its pattern would be represented by one instance of this class.
 * The variable to which a pattern applies is called "primary".
 *
 * <p>
 * In some cases, a variable requires certain expressions to be able to reach its value.
 * This often happens with the groupBy(...) construct.
 * Such expressions are called "prerequisite".
 * In the following sample, the accumulate expression would be a prerequisite for the primary variable, as the primary
 * variable would have nowhere to read its value from, if the prerequisite expression were not present:
 *
 * <pre>
 * {@code
 *  rule "Complex univariate rule"
 *  when
 *      $accumulateResult: Collection() from accumulate(
 *          ...
 *      )
 *      $a: Object() from $accumulateResult
 *  then
 *      // Do something with the $a variable.
 *  end
 * }
 * </pre>
 *
 * <p>
 * In other cases, a variable has certain trailing expressions that limit its use.
 * This often happen with conditional propagation constructs, such as ifExists(...)
 * Such expressions are called "dependent".
 * In the following sample, the "exists" expression would be dependent on the primary variable, as the primary variable
 * would behave differently if the dependent expression were not present:
 *
 * <pre>
 * {@code
 *  rule "Complex univariate rule"
 *  when
 *      $a: Something()
 *      exists SomethingElse()
 *  then
 *      // Do something with the $a variable.
 *  end
 * }
 * </pre>
 *
 * <p>
 * Patterns in the Drools executable model are mutable, and therefore we must take extra care to ensure that two
 * constraint streams never apply expressions and bindings to the same pattern.
 * If that were to happen, those two constraint streams would effectively be modifying the same rule, which is very
 * unlikely to result in the expected outcome.
 * In order to ensure that these situations are prevented, patterns are actually only ever created at the time when
 * the final rule assembly is requested.
 * Until then, the pattern supplier accumulates all the operations to be executed at the appropriate time.
 *
 * @param <A> generic type of the primary variable
 */
final class DirectPatternVariable<A> extends AbstractPatternVariable<A, A, DirectPatternVariable<A>> {

    DirectPatternVariable(Variable<A> aVariable) {
        this(aVariable, Collections.emptyList());
    }

    DirectPatternVariable(Variable<A> aVariable, List<ViewItem<?>> prerequisiteExpressions) {
        super(aVariable, () -> pattern(aVariable), prerequisiteExpressions, Collections.emptyList());
    }

    DirectPatternVariable(Variable<A> aVariable, ViewItem<?> prerequisiteExpression) {
        this(aVariable, Collections.singletonList(prerequisiteExpression));
    }

    private DirectPatternVariable(DirectPatternVariable<A> patternCreator,
            UnaryOperator<PatternDSL.PatternDef<A>> patternMutator) {
        super(patternCreator, patternMutator);
    }

    private DirectPatternVariable(DirectPatternVariable<A> patternCreator, ViewItem<?> dependentExpression) {
        super(patternCreator, dependentExpression);
    }

    @Override
    protected A extract(A a) {
        return a; // Values of direct variables come straight from the pattern.
    }

    @Override
    protected DirectPatternVariable<A> create(UnaryOperator<PatternDSL.PatternDef<A>> patternMutator) {
        return new DirectPatternVariable<>(this, patternMutator);
    }

    @Override
    protected DirectPatternVariable<A> create(ViewItem<?> dependentExpression) {
        return new DirectPatternVariable<>(this, dependentExpression);
    }
}

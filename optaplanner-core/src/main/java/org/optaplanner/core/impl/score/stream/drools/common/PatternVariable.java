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

package org.optaplanner.core.impl.score.stream.drools.common;

import static org.drools.model.PatternDSL.betaIndexedBy;
import static org.drools.model.PatternDSL.pattern;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.model.BetaIndex;
import org.drools.model.PatternDSL;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Predicate2;
import org.drools.model.view.ViewItem;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.impl.score.stream.bi.AbstractBiJoiner;
import org.optaplanner.core.impl.score.stream.common.JoinerType;

/**
 * Represents a single variable with all of its patterns in the left hand side of a Drools rule.
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
class PatternVariable<A> {

    private final Variable<A> primaryVariable;
    private final Supplier<PatternDSL.PatternDef<A>> patternSupplier;
    private final List<ViewItem<?>> prerequisiteExpressions;
    private final List<ViewItem<?>> dependentExpressions;

    PatternVariable(Variable<A> aVariable) {
        this(aVariable, Collections.emptyList());
    }

    PatternVariable(Variable<A> aVariable, List<ViewItem<?>> prerequisiteExpressions) {
        this.primaryVariable = aVariable;
        this.patternSupplier = () -> pattern(aVariable);
        this.prerequisiteExpressions = prerequisiteExpressions;
        this.dependentExpressions = Collections.emptyList();
    }

    PatternVariable(PatternVariable<A> patternCreator, UnaryOperator<PatternDSL.PatternDef<A>> patternMutator) {
        this.primaryVariable = patternCreator.primaryVariable;
        this.patternSupplier = () -> patternMutator.apply(patternCreator.patternSupplier.get());
        this.prerequisiteExpressions = patternCreator.prerequisiteExpressions;
        this.dependentExpressions = patternCreator.dependentExpressions;
    }

    PatternVariable(PatternVariable<A> patternCreator, ViewItem<?> dependentExpression) {
        this.primaryVariable = patternCreator.primaryVariable;
        this.patternSupplier = patternCreator.patternSupplier;
        this.prerequisiteExpressions = patternCreator.prerequisiteExpressions;
        this.dependentExpressions = Stream.concat(patternCreator.dependentExpressions.stream(), Stream.of(dependentExpression))
                .collect(Collectors.toList());

    }

    public Variable<A> getPrimaryVariable() {
        return primaryVariable;
    }

    public PatternVariable<A> filter(Predicate<A> predicate) {
        return new PatternVariable<>(this, p -> p.expr("Filter using " + predicate, predicate::test));
    }

    public <LeftJoinVar_> PatternVariable<A> filter(BiPredicate<LeftJoinVar_, A> predicate,
            Variable<LeftJoinVar_> leftJoinVariable) {
        return new PatternVariable<>(this,
                p -> p.expr("Filter using " + predicate, leftJoinVariable, (a, leftJoinVar) -> predicate.test(leftJoinVar, a)));
    }

    public <LeftJoinVarA_, LeftJoinVarB_> PatternVariable<A> filter(
            TriPredicate<LeftJoinVarA_, LeftJoinVarB_, A> predicate, Variable<LeftJoinVarA_> leftJoinVariableA,
            Variable<LeftJoinVarB_> leftJoinVariableB) {
        return new PatternVariable<>(this, p -> p.expr("Filter using " + predicate, leftJoinVariableA, leftJoinVariableB,
                (a, leftJoinVarA, leftJoinVarB) -> predicate.test(leftJoinVarA, leftJoinVarB, a)));
    }

    public <LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_> PatternVariable<A> filter(
            QuadPredicate<LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_, A> predicate,
            Variable<LeftJoinVarA_> leftJoinVariableA, Variable<LeftJoinVarB_> leftJoinVariableB,
            Variable<LeftJoinVarC_> leftJoinVariableC) {
        return new PatternVariable<>(this,
                p -> p.expr("Filter using " + predicate, leftJoinVariableA, leftJoinVariableB, leftJoinVariableC,
                        (a, leftJoinVarA, leftJoinVarB, leftJoinVarC) -> predicate.test(leftJoinVarA, leftJoinVarB,
                                leftJoinVarC, a)));
    }

    public <LeftJoinVar_> PatternVariable<A> filterOnJoinVar(Variable<LeftJoinVar_> leftJoinVar,
            AbstractBiJoiner<LeftJoinVar_, A> joiner, JoinerType joinerType, int mappingIndex) {
        // For each mapping, bind a join variable from A to B and index the binding.
        Function<LeftJoinVar_, Object> leftMapping = joiner.getLeftMapping(mappingIndex);
        Function<A, Object> rightMapping = joiner.getRightMapping(mappingIndex);
        Function1<A, Object> rightExtractor = rightMapping::apply;
        // Only extract B; A is coming from a pre-bound join var.
        Predicate2<A, LeftJoinVar_> predicate = (b, a) -> joinerType.matches(a, rightExtractor.apply(b));
        return new PatternVariable<>(this, p -> {
            BetaIndex<A, LeftJoinVar_, Object> index = betaIndexedBy(Object.class,
                    AbstractLeftHandSide.getConstraintType(joinerType), mappingIndex, rightExtractor,
                    leftMapping::apply);
            return p.expr("Join using joiner #" + mappingIndex + " in " + joiner, leftJoinVar, predicate, index);
        });
    }

    public <BoundVar_> PatternVariable<A> bind(Variable<BoundVar_> boundVariable, Function<A, BoundVar_> bindingFunction) {
        return new PatternVariable<>(this, p -> p.bind(boundVariable, bindingFunction::apply));
    }

    public <BoundVar_, LeftJoinVar_> PatternVariable<A> bind(Variable<BoundVar_> boundVariable,
            Variable<LeftJoinVar_> leftJoinVariable, BiFunction<A, LeftJoinVar_, BoundVar_> bindingFunction) {
        return new PatternVariable<>(this, p -> p.bind(boundVariable, leftJoinVariable, bindingFunction::apply));
    }

    public <BoundVar_, LeftJoinVarA_, LeftJoinVarB_> PatternVariable<A> bind(Variable<BoundVar_> boundVariable,
            Variable<LeftJoinVarA_> leftJoinVariableA, Variable<LeftJoinVarB_> leftJoinVariableB,
            TriFunction<A, LeftJoinVarA_, LeftJoinVarB_, BoundVar_> bindingFunction) {
        return new PatternVariable<>(this,
                p -> p.bind(boundVariable, leftJoinVariableA, leftJoinVariableB, bindingFunction::apply));
    }

    public <BoundVar_, LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_> PatternVariable<A> bind(
            Variable<BoundVar_> boundVariable, Variable<LeftJoinVarA_> leftJoinVariableA,
            Variable<LeftJoinVarB_> leftJoinVariableB, Variable<LeftJoinVarC_> leftJoinVariableC,
            QuadFunction<A, LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_, BoundVar_> bindingFunction) {
        return new PatternVariable<>(this,
                p -> p.bind(boundVariable, leftJoinVariableA, leftJoinVariableB, leftJoinVariableC,
                        bindingFunction::apply));
    }

    public PatternVariable<A> addDependentExpression(ViewItem<?> expression) {
        return new PatternVariable<>(this, expression);
    }

    public List<ViewItem<?>> build() {
        Stream<ViewItem<?>> prerequisites = prerequisiteExpressions.stream();
        Stream<ViewItem<?>> dependents = dependentExpressions.stream();
        return Stream.concat(Stream.concat(prerequisites, Stream.of(patternSupplier.get())), dependents)
                .collect(Collectors.toList());
    }

}

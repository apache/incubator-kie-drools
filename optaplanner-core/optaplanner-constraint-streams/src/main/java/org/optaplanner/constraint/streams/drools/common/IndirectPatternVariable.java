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

import static org.drools.model.PatternDSL.betaIndexedBy;

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
import org.drools.model.BetaIndex2;
import org.drools.model.BetaIndex3;
import org.drools.model.PatternDSL;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Predicate2;
import org.drools.model.functions.Predicate3;
import org.drools.model.functions.Predicate4;
import org.drools.model.view.ViewItem;
import org.optaplanner.constraint.streams.bi.DefaultBiJoiner;
import org.optaplanner.constraint.streams.quad.DefaultQuadJoiner;
import org.optaplanner.constraint.streams.tri.DefaultTriJoiner;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.impl.score.stream.JoinerType;

/**
 * Represents a single variable with all of its patterns in the left hand side of a Drools rule,
 * which doesn't use the pattern's variable but instead binds another on that pattern.
 *
 * <p>
 * Consider the following simple bivariate rule, in the equivalent DRL:
 *
 * <pre>
 * {@code
 *  rule "Simple bivariate rule"
 *  when
 *      $a: Something()
 *      SomethingElse($b: someField)
 *  then
 *      // Do something with the $a and $b variables.
 *  end
 * }
 * </pre>
 * <p>
 * In this rule, variable "a" would be represented by {@link DirectPatternVariable}.
 * Variable "b" would be represented by this class and would be extracted from SomethingElse using a mapping function.
 *
 * <p>
 * Therefore although all the operations of {@link PatternVariable} have the same semantics here and in
 * {@link DirectPatternVariable}, indirect variables need to apply a level of indirection to get their values from the
 * pattern.
 * This will require repeated invocations of the mapping function (see {@link #extract(Object)},
 * which must therefore be efficiently implemented, ideally a pure stateless getter.
 *
 * <p>
 * These repeated invocations are a Drools performance trade-off. If we instead bound the variable on the pattern,
 * we would subsequently have to increase the arity of all binding/expression executable model functions by that
 * one bound variable.
 * Unfortunately, this would have been inefficient, as that would prevent these higher-arity functions from being
 * properly JITted and the performance would arguably suffer more than when we have to call an inexpensive mapping
 * function which would likely be optimized by the JIT anyway.
 * 
 * @param <A> generic type of the primary variable as obtained by the mapping function from the pattern variable
 * @param <PatternVar_>> generic type of the pattern variable
 */
final class IndirectPatternVariable<A, PatternVar_>
        implements PatternVariable<A, PatternVar_, IndirectPatternVariable<A, PatternVar_>> {

    private final Variable<A> primaryVariable;
    private final Supplier<PatternDSL.PatternDef<PatternVar_>> patternSupplier;
    private final Function<PatternVar_, A> mappingFunction;
    private final List<ViewItem<?>> prerequisiteExpressions;
    private final List<ViewItem<?>> dependentExpressions;

    <OldA> IndirectPatternVariable(IndirectPatternVariable<OldA, PatternVar_> patternCreator, Variable<A> boundVariable,
            Function<OldA, A> mappingFunction) {
        this.primaryVariable = boundVariable;
        this.patternSupplier = patternCreator.patternSupplier;
        this.mappingFunction = patternCreator.mappingFunction.andThen(mappingFunction);
        this.prerequisiteExpressions = patternCreator.prerequisiteExpressions;
        this.dependentExpressions = patternCreator.dependentExpressions;
    }

    IndirectPatternVariable(DirectPatternVariable<PatternVar_> patternCreator, Variable<A> boundVariable,
            Function<PatternVar_, A> mappingFunction) {
        this.primaryVariable = boundVariable;
        this.patternSupplier = patternCreator.getPatternSupplier();
        this.mappingFunction = mappingFunction;
        this.prerequisiteExpressions = patternCreator.getPrerequisiteExpressions();
        this.dependentExpressions = patternCreator.getDependentExpressions();
    }

    private IndirectPatternVariable(IndirectPatternVariable<A, PatternVar_> patternCreator,
            UnaryOperator<PatternDSL.PatternDef<PatternVar_>> patternMutator) {
        this.primaryVariable = patternCreator.primaryVariable;
        this.patternSupplier = () -> patternMutator.apply(patternCreator.patternSupplier.get());
        this.mappingFunction = patternCreator.mappingFunction;
        this.prerequisiteExpressions = patternCreator.prerequisiteExpressions;
        this.dependentExpressions = patternCreator.dependentExpressions;
    }

    private IndirectPatternVariable(IndirectPatternVariable<A, PatternVar_> patternCreator,
            ViewItem<?> dependentExpression) {
        this.primaryVariable = patternCreator.primaryVariable;
        this.patternSupplier = patternCreator.patternSupplier;
        this.mappingFunction = patternCreator.mappingFunction;
        this.prerequisiteExpressions = patternCreator.prerequisiteExpressions;
        this.dependentExpressions = Stream.concat(patternCreator.dependentExpressions.stream(), Stream.of(dependentExpression))
                .collect(Collectors.toList());
    }

    @Override
    public Variable<A> getPrimaryVariable() {
        return primaryVariable;
    }

    @Override
    public List<ViewItem<?>> getPrerequisiteExpressions() {
        return prerequisiteExpressions;
    }

    @Override
    public List<ViewItem<?>> getDependentExpressions() {
        return dependentExpressions;
    }

    private A extract(PatternVar_ patternVar) {
        return mappingFunction.apply(patternVar);
    }

    @Override
    public IndirectPatternVariable<A, PatternVar_> filter(Predicate<A> predicate) {
        return new IndirectPatternVariable<>(this, p -> p.expr("Filter using " + predicate,
                a -> predicate.test(extract(a))));
    }

    @Override
    public <LeftJoinVar_> IndirectPatternVariable<A, PatternVar_> filter(BiPredicate<LeftJoinVar_, A> predicate,
            Variable<LeftJoinVar_> leftJoinVariable) {
        return new IndirectPatternVariable<>(this,
                p -> p.expr("Filter using " + predicate, leftJoinVariable,
                        (a, leftJoinVar) -> predicate.test(leftJoinVar, extract(a))));
    }

    @Override
    public <LeftJoinVarA_, LeftJoinVarB_> IndirectPatternVariable<A, PatternVar_> filter(
            TriPredicate<LeftJoinVarA_, LeftJoinVarB_, A> predicate, Variable<LeftJoinVarA_> leftJoinVariableA,
            Variable<LeftJoinVarB_> leftJoinVariableB) {
        return new IndirectPatternVariable<>(this,
                p -> p.expr("Filter using " + predicate, leftJoinVariableA, leftJoinVariableB,
                        (a, leftJoinVarA, leftJoinVarB) -> predicate.test(leftJoinVarA, leftJoinVarB, extract(a))));
    }

    @Override
    public <LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_> IndirectPatternVariable<A, PatternVar_> filter(
            QuadPredicate<LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_, A> predicate,
            Variable<LeftJoinVarA_> leftJoinVariableA, Variable<LeftJoinVarB_> leftJoinVariableB,
            Variable<LeftJoinVarC_> leftJoinVariableC) {
        return new IndirectPatternVariable<>(this,
                p -> p.expr("Filter using " + predicate, leftJoinVariableA, leftJoinVariableB, leftJoinVariableC,
                        (a, leftJoinVarA, leftJoinVarB, leftJoinVarC) -> predicate.test(leftJoinVarA, leftJoinVarB,
                                leftJoinVarC, extract(a))));
    }

    @Override
    public <LeftJoinVar_> PatternVariable<A, PatternVar_, IndirectPatternVariable<A, PatternVar_>> filterForJoin(
            Variable<LeftJoinVar_> leftJoinVar, DefaultBiJoiner<LeftJoinVar_, A> joiner, JoinerType joinerType,
            int mappingIndex) {
        Function<LeftJoinVar_, Object> leftMapping = joiner.getLeftMapping(mappingIndex);
        Function<A, Object> rightMapping = joiner.getRightMapping(mappingIndex);
        Function1<PatternVar_, Object> rightExtractor = b -> rightMapping.apply(extract(b));
        Predicate2<PatternVar_, LeftJoinVar_> predicate =
                (b, a) -> joinerType.matches(leftMapping.apply(a), rightExtractor.apply(b));
        return new IndirectPatternVariable<>(this, p -> {
            BetaIndex<PatternVar_, LeftJoinVar_, Object> index = betaIndexedBy(Object.class,
                    AbstractLeftHandSide.getConstraintType(joinerType), mappingIndex, rightExtractor,
                    leftMapping::apply);
            return p.expr("Join using joiner #" + mappingIndex + " in " + joiner, leftJoinVar, predicate, index);
        });
    }

    @Override
    public <LeftJoinVarA_, LeftJoinVarB_> PatternVariable<A, PatternVar_, IndirectPatternVariable<A, PatternVar_>>
            filterForJoin(Variable<LeftJoinVarA_> leftJoinVarA, Variable<LeftJoinVarB_> leftJoinVarB,
                    DefaultTriJoiner<LeftJoinVarA_, LeftJoinVarB_, A> joiner, JoinerType joinerType, int mappingIndex) {
        BiFunction<LeftJoinVarA_, LeftJoinVarB_, Object> leftMapping = joiner.getLeftMapping(mappingIndex);
        Function<A, Object> rightMapping = joiner.getRightMapping(mappingIndex);
        Function1<PatternVar_, Object> rightExtractor = b -> rightMapping.apply(extract(b));
        Predicate3<PatternVar_, LeftJoinVarA_, LeftJoinVarB_> predicate =
                (c, a, b) -> joinerType.matches(leftMapping.apply(a, b), rightExtractor.apply(c));
        return new IndirectPatternVariable<>(this, p -> {
            BetaIndex2<PatternVar_, LeftJoinVarA_, LeftJoinVarB_, Object> index =
                    betaIndexedBy(Object.class, AbstractLeftHandSide.getConstraintType(joinerType), mappingIndex,
                            rightExtractor, leftMapping::apply, Object.class);
            return p.expr("Join using joiner #" + mappingIndex + " in " + joiner, leftJoinVarA, leftJoinVarB, predicate, index);
        });
    }

    @Override
    public <LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_>
            PatternVariable<A, PatternVar_, IndirectPatternVariable<A, PatternVar_>>
            filterForJoin(Variable<LeftJoinVarA_> leftJoinVarA, Variable<LeftJoinVarB_> leftJoinVarB,
                    Variable<LeftJoinVarC_> leftJoinVarC,
                    DefaultQuadJoiner<LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_, A> joiner, JoinerType joinerType,
                    int mappingIndex) {
        TriFunction<LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_, Object> leftMapping =
                joiner.getLeftMapping(mappingIndex);
        Function<A, Object> rightMapping = joiner.getRightMapping(mappingIndex);
        Function1<PatternVar_, Object> rightExtractor = b -> rightMapping.apply(extract(b));
        Predicate4<PatternVar_, LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_> predicate =
                (d, a, b, c) -> joinerType.matches(leftMapping.apply(a, b, c), rightExtractor.apply(d));
        return new IndirectPatternVariable<>(this, p -> {
            BetaIndex3<PatternVar_, LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_, Object> index =
                    betaIndexedBy(Object.class, AbstractLeftHandSide.getConstraintType(joinerType), mappingIndex,
                            rightExtractor, leftMapping::apply, Object.class);
            return p.expr("Join using joiner #" + mappingIndex + " in " + joiner, leftJoinVarA, leftJoinVarB,
                    leftJoinVarC, predicate, index);
        });
    }

    @Override
    public <BoundVar_> IndirectPatternVariable<A, PatternVar_> bind(Variable<BoundVar_> boundVariable,
            Function<A, BoundVar_> bindingFunction) {
        return new IndirectPatternVariable<>(this,
                p -> p.bind(boundVariable, a -> bindingFunction.apply(extract(a))));
    }

    @Override
    public <BoundVar_, LeftJoinVar_> IndirectPatternVariable<A, PatternVar_> bind(Variable<BoundVar_> boundVariable,
            Variable<LeftJoinVar_> leftJoinVariable, BiFunction<A, LeftJoinVar_, BoundVar_> bindingFunction) {
        return new IndirectPatternVariable<>(this,
                p -> p.bind(boundVariable, leftJoinVariable,
                        (a, leftJoinVar) -> bindingFunction.apply(extract(a), leftJoinVar)));
    }

    @Override
    public <BoundVar_, LeftJoinVarA_, LeftJoinVarB_> IndirectPatternVariable<A, PatternVar_> bind(
            Variable<BoundVar_> boundVariable, Variable<LeftJoinVarA_> leftJoinVariableA,
            Variable<LeftJoinVarB_> leftJoinVariableB,
            TriFunction<A, LeftJoinVarA_, LeftJoinVarB_, BoundVar_> bindingFunction) {
        return new IndirectPatternVariable<>(this,
                p -> p.bind(boundVariable, leftJoinVariableA, leftJoinVariableB,
                        (a, leftJoinVarA, leftJoinVarB) -> bindingFunction.apply(extract(a), leftJoinVarA, leftJoinVarB)));
    }

    @Override
    public <BoundVar_, LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_> IndirectPatternVariable<A, PatternVar_> bind(
            Variable<BoundVar_> boundVariable, Variable<LeftJoinVarA_> leftJoinVariableA,
            Variable<LeftJoinVarB_> leftJoinVariableB, Variable<LeftJoinVarC_> leftJoinVariableC,
            QuadFunction<A, LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_, BoundVar_> bindingFunction) {
        return new IndirectPatternVariable<>(this,
                p -> p.bind(boundVariable, leftJoinVariableA, leftJoinVariableB, leftJoinVariableC,
                        (a, leftJoinVarA, leftJoinVarB, leftJoinVarC) -> bindingFunction.apply(extract(a), leftJoinVarA,
                                leftJoinVarB, leftJoinVarC)));
    }

    @Override
    public IndirectPatternVariable<A, PatternVar_> addDependentExpression(ViewItem<?> expression) {
        return new IndirectPatternVariable<>(this, expression);
    }

    @Override
    public List<ViewItem<?>> build() {
        Stream<ViewItem<?>> prerequisites = prerequisiteExpressions.stream();
        Stream<ViewItem<?>> dependents = dependentExpressions.stream();
        return Stream.concat(Stream.concat(prerequisites, Stream.of(patternSupplier.get())), dependents)
                .collect(Collectors.toList());
    }
}

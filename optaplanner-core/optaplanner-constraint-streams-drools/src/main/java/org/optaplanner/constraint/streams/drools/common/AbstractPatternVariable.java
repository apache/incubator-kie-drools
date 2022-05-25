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
import org.optaplanner.constraint.streams.common.bi.DefaultBiJoiner;
import org.optaplanner.constraint.streams.common.quad.DefaultQuadJoiner;
import org.optaplanner.constraint.streams.common.tri.DefaultTriJoiner;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.impl.score.stream.JoinerType;

abstract class AbstractPatternVariable<A, PatternVar_, Child_ extends AbstractPatternVariable<A, PatternVar_, Child_>>
        implements PatternVariable<A, PatternVar_, Child_> {

    private final Variable<A> primaryVariable;
    private final Supplier<PatternDSL.PatternDef<PatternVar_>> patternSupplier;
    private final List<ViewItem<?>> prerequisiteExpressions;
    private final List<ViewItem<?>> dependentExpressions;

    protected AbstractPatternVariable(Variable<A> aVariable, Supplier<PatternDSL.PatternDef<PatternVar_>> patternSupplier,
            List<ViewItem<?>> prerequisiteExpressions, List<ViewItem<?>> dependentExpressions) {
        this.primaryVariable = aVariable;
        this.patternSupplier = patternSupplier;
        this.prerequisiteExpressions = prerequisiteExpressions;
        this.dependentExpressions = dependentExpressions;
    }

    protected AbstractPatternVariable(AbstractPatternVariable<?, PatternVar_, ?> patternCreator,
            Variable<A> boundVariable) {
        this.primaryVariable = boundVariable;
        this.patternSupplier = patternCreator.getPatternSupplier();
        this.prerequisiteExpressions = patternCreator.getPrerequisiteExpressions();
        this.dependentExpressions = patternCreator.getDependentExpressions();
    }

    protected AbstractPatternVariable(AbstractPatternVariable<A, PatternVar_, ?> patternCreator,
            UnaryOperator<PatternDSL.PatternDef<PatternVar_>> patternMutator) {
        this.primaryVariable = patternCreator.primaryVariable;
        this.patternSupplier = () -> patternMutator.apply(patternCreator.patternSupplier.get());
        this.prerequisiteExpressions = patternCreator.prerequisiteExpressions;
        this.dependentExpressions = patternCreator.dependentExpressions;
    }

    protected AbstractPatternVariable(AbstractPatternVariable<A, PatternVar_, ?> patternCreator,
            ViewItem<?> dependentExpression) {
        this.primaryVariable = patternCreator.primaryVariable;
        this.patternSupplier = patternCreator.patternSupplier;
        this.prerequisiteExpressions = patternCreator.prerequisiteExpressions;
        this.dependentExpressions = Stream.concat(patternCreator.dependentExpressions.stream(), Stream.of(dependentExpression))
                .collect(Collectors.toList());
    }

    @Override
    public Variable<A> getPrimaryVariable() {
        return primaryVariable;
    }

    public Supplier<PatternDSL.PatternDef<PatternVar_>> getPatternSupplier() {
        return patternSupplier;
    }

    @Override
    public List<ViewItem<?>> getPrerequisiteExpressions() {
        return prerequisiteExpressions;
    }

    @Override
    public List<ViewItem<?>> getDependentExpressions() {
        return dependentExpressions;
    }

    /**
     * Variable values can be either read directly from the pattern variable (see {@link DirectPatternVariable}
     * or indirectly by applying a mapping function to it (see {@link IndirectPatternVariable}.
     * This method abstracts this behavior, so that the surrounding code may be shared between both implementations.
     *
     * @param patternVar never null, pattern variable to extract the value from
     * @return value of the variable
     */
    protected abstract A extract(PatternVar_ patternVar);

    protected abstract Child_ create(UnaryOperator<PatternDSL.PatternDef<PatternVar_>> patternMutator);

    protected abstract Child_ create(ViewItem<?> dependentExpression);

    @Override
    public final Child_ filter(Predicate<A> predicate) {
        return create(p -> p.expr("Filter using " + predicate, a -> predicate.test(extract(a))));
    }

    @Override
    public final <LeftJoinVar_> Child_ filter(BiPredicate<LeftJoinVar_, A> predicate,
            Variable<LeftJoinVar_> leftJoinVariable) {
        return create(p -> p.expr("Filter using " + predicate, leftJoinVariable,
                (a, leftJoinVar) -> predicate.test(leftJoinVar, extract(a))));
    }

    @Override
    public final <LeftJoinVarA_, LeftJoinVarB_> Child_ filter(
            TriPredicate<LeftJoinVarA_, LeftJoinVarB_, A> predicate, Variable<LeftJoinVarA_> leftJoinVariableA,
            Variable<LeftJoinVarB_> leftJoinVariableB) {
        return create(p -> p.expr("Filter using " + predicate, leftJoinVariableA, leftJoinVariableB,
                (a, leftJoinVarA, leftJoinVarB) -> predicate.test(leftJoinVarA, leftJoinVarB, extract(a))));
    }

    @Override
    public final <LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_> Child_ filter(
            QuadPredicate<LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_, A> predicate,
            Variable<LeftJoinVarA_> leftJoinVariableA, Variable<LeftJoinVarB_> leftJoinVariableB,
            Variable<LeftJoinVarC_> leftJoinVariableC) {
        return create(p -> p.expr("Filter using " + predicate, leftJoinVariableA, leftJoinVariableB, leftJoinVariableC,
                (a, leftJoinVarA, leftJoinVarB, leftJoinVarC) -> predicate.test(leftJoinVarA, leftJoinVarB,
                        leftJoinVarC, extract(a))));
    }

    @Override
    public final <LeftJoinVar_> Child_ filterForJoin(Variable<LeftJoinVar_> leftJoinVar,
            DefaultBiJoiner<LeftJoinVar_, A> joiner, JoinerType joinerType, int mappingIndex) {
        Function<LeftJoinVar_, Object> leftMapping = joiner.getLeftMapping(mappingIndex);
        Function<A, Object> rightMapping = joiner.getRightMapping(mappingIndex);
        Function1<PatternVar_, Object> rightExtractor = b -> rightMapping.apply(extract(b));
        Predicate2<PatternVar_, LeftJoinVar_> predicate =
                (b, a) -> joinerType.matches(leftMapping.apply(a), rightExtractor.apply(b));
        return create(p -> {
            BetaIndex<PatternVar_, LeftJoinVar_, Object> index = betaIndexedBy(Object.class,
                    AbstractLeftHandSide.getConstraintType(joinerType), mappingIndex, rightExtractor,
                    leftMapping::apply);
            return p.expr("Join using joiner #" + mappingIndex + " in " + joiner, leftJoinVar, predicate, index);
        });
    }

    @Override
    public final <LeftJoinVarA_, LeftJoinVarB_> Child_ filterForJoin(Variable<LeftJoinVarA_> leftJoinVarA,
            Variable<LeftJoinVarB_> leftJoinVarB, DefaultTriJoiner<LeftJoinVarA_, LeftJoinVarB_, A> joiner,
            JoinerType joinerType, int mappingIndex) {
        BiFunction<LeftJoinVarA_, LeftJoinVarB_, Object> leftMapping = joiner.getLeftMapping(mappingIndex);
        Function<A, Object> rightMapping = joiner.getRightMapping(mappingIndex);
        Function1<PatternVar_, Object> rightExtractor = b -> rightMapping.apply(extract(b));
        Predicate3<PatternVar_, LeftJoinVarA_, LeftJoinVarB_> predicate =
                (c, a, b) -> joinerType.matches(leftMapping.apply(a, b), rightExtractor.apply(c));
        return create(p -> {
            BetaIndex2<PatternVar_, LeftJoinVarA_, LeftJoinVarB_, Object> index =
                    betaIndexedBy(Object.class, AbstractLeftHandSide.getConstraintType(joinerType), mappingIndex,
                            rightExtractor, leftMapping::apply, Object.class);
            return p.expr("Join using joiner #" + mappingIndex + " in " + joiner, leftJoinVarA, leftJoinVarB, predicate, index);
        });
    }

    @Override
    public final <LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_> Child_ filterForJoin(Variable<LeftJoinVarA_> leftJoinVarA,
            Variable<LeftJoinVarB_> leftJoinVarB, Variable<LeftJoinVarC_> leftJoinVarC,
            DefaultQuadJoiner<LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_, A> joiner, JoinerType joinerType,
            int mappingIndex) {
        TriFunction<LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_, Object> leftMapping =
                joiner.getLeftMapping(mappingIndex);
        Function<A, Object> rightMapping = joiner.getRightMapping(mappingIndex);
        Function1<PatternVar_, Object> rightExtractor = b -> rightMapping.apply(extract(b));
        Predicate4<PatternVar_, LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_> predicate =
                (d, a, b, c) -> joinerType.matches(leftMapping.apply(a, b, c), rightExtractor.apply(d));
        return create(p -> {
            BetaIndex3<PatternVar_, LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_, Object> index =
                    betaIndexedBy(Object.class, AbstractLeftHandSide.getConstraintType(joinerType), mappingIndex,
                            rightExtractor, leftMapping::apply, Object.class);
            return p.expr("Join using joiner #" + mappingIndex + " in " + joiner, leftJoinVarA, leftJoinVarB,
                    leftJoinVarC, predicate, index);
        });
    }

    @Override
    public final <BoundVar_> Child_ bind(Variable<BoundVar_> boundVariable, Function<A, BoundVar_> bindingFunction) {
        return create(p -> p.bind(boundVariable, a -> bindingFunction.apply(extract(a))));
    }

    @Override
    public final <BoundVar_, LeftJoinVar_> Child_ bind(Variable<BoundVar_> boundVariable,
            Variable<LeftJoinVar_> leftJoinVariable, BiFunction<A, LeftJoinVar_, BoundVar_> bindingFunction) {
        return create(p -> p.bind(boundVariable, leftJoinVariable,
                (a, leftJoinVar) -> bindingFunction.apply(extract(a), leftJoinVar)));
    }

    @Override
    public final <BoundVar_, LeftJoinVarA_, LeftJoinVarB_> Child_ bind(Variable<BoundVar_> boundVariable,
            Variable<LeftJoinVarA_> leftJoinVariableA, Variable<LeftJoinVarB_> leftJoinVariableB,
            TriFunction<A, LeftJoinVarA_, LeftJoinVarB_, BoundVar_> bindingFunction) {
        return create(p -> p.bind(boundVariable, leftJoinVariableA, leftJoinVariableB,
                (a, leftJoinVarA, leftJoinVarB) -> bindingFunction.apply(extract(a), leftJoinVarA, leftJoinVarB)));
    }

    @Override
    public final <BoundVar_, LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_> Child_ bind(Variable<BoundVar_> boundVariable,
            Variable<LeftJoinVarA_> leftJoinVariableA, Variable<LeftJoinVarB_> leftJoinVariableB,
            Variable<LeftJoinVarC_> leftJoinVariableC,
            QuadFunction<A, LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_, BoundVar_> bindingFunction) {
        return create(p -> p.bind(boundVariable, leftJoinVariableA, leftJoinVariableB, leftJoinVariableC,
                (a, leftJoinVarA, leftJoinVarB, leftJoinVarC) -> bindingFunction.apply(extract(a), leftJoinVarA,
                        leftJoinVarB, leftJoinVarC)));
    }

    @Override
    public final Child_ addDependentExpression(ViewItem<?> expression) {
        return create(expression);
    }

    @Override
    public final List<ViewItem<?>> build() {
        Stream<ViewItem<?>> prerequisites = prerequisiteExpressions.stream();
        Stream<ViewItem<?>> dependents = dependentExpressions.stream();
        return Stream.concat(Stream.concat(prerequisites, Stream.of(patternSupplier.get())), dependents)
                .collect(Collectors.toList());
    }
}

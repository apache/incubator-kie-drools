/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.constraint.streams.drools.common;

import static org.drools.model.PatternDSL.betaIndexedBy;
import static org.optaplanner.constraint.streams.drools.common.AbstractLeftHandSide.getConstraintType;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
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
    private final PatternDSL.PatternDef<PatternVar_> pattern;
    private final List<ViewItem<?>> prerequisiteExpressions;
    private final List<ViewItem<?>> dependentExpressions;

    protected AbstractPatternVariable(Variable<A> aVariable, PatternDSL.PatternDef<PatternVar_> pattern,
            List<ViewItem<?>> prerequisiteExpressions, List<ViewItem<?>> dependentExpressions) {
        this.primaryVariable = aVariable;
        this.pattern = pattern;
        this.prerequisiteExpressions = prerequisiteExpressions;
        this.dependentExpressions = dependentExpressions;
    }

    protected AbstractPatternVariable(AbstractPatternVariable<?, PatternVar_, ?> patternCreator, Variable<A> boundVariable) {
        this.primaryVariable = boundVariable;
        this.pattern = patternCreator.getPattern();
        this.prerequisiteExpressions = patternCreator.getPrerequisiteExpressions();
        this.dependentExpressions = patternCreator.getDependentExpressions();
    }

    protected AbstractPatternVariable(AbstractPatternVariable<A, PatternVar_, ?> patternCreator,
            ViewItem<?> dependentExpression) {
        this.primaryVariable = patternCreator.primaryVariable;
        this.pattern = patternCreator.pattern;
        this.prerequisiteExpressions = patternCreator.prerequisiteExpressions;
        this.dependentExpressions = Stream.concat(patternCreator.dependentExpressions.stream(), Stream.of(dependentExpression))
                .collect(Collectors.toList());
    }

    @Override
    public Variable<A> getPrimaryVariable() {
        return primaryVariable;
    }

    public PatternDSL.PatternDef<PatternVar_> getPattern() {
        return pattern;
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

    @Override
    public final Child_ filter(Predicate<A> predicate) {
        pattern.expr("Filter using " + predicate, a -> predicate.test(extract(a)));
        return (Child_) this;
    }

    @Override
    public final <LeftJoinVar_> Child_ filter(BiPredicate<LeftJoinVar_, A> predicate,
            Variable<LeftJoinVar_> leftJoinVariable) {
        pattern.expr("Filter using " + predicate, leftJoinVariable,
                (a, leftJoinVar) -> predicate.test(leftJoinVar, extract(a)));
        return (Child_) this;
    }

    @Override
    public final <LeftJoinVarA_, LeftJoinVarB_> Child_ filter(
            TriPredicate<LeftJoinVarA_, LeftJoinVarB_, A> predicate, Variable<LeftJoinVarA_> leftJoinVariableA,
            Variable<LeftJoinVarB_> leftJoinVariableB) {
        pattern.expr("Filter using " + predicate, leftJoinVariableA, leftJoinVariableB,
                (a, leftJoinVarA, leftJoinVarB) -> predicate.test(leftJoinVarA, leftJoinVarB, extract(a)));
        return (Child_) this;
    }

    @Override
    public final <LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_> Child_ filter(
            QuadPredicate<LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_, A> predicate,
            Variable<LeftJoinVarA_> leftJoinVariableA, Variable<LeftJoinVarB_> leftJoinVariableB,
            Variable<LeftJoinVarC_> leftJoinVariableC) {
        pattern.expr("Filter using " + predicate, leftJoinVariableA, leftJoinVariableB, leftJoinVariableC,
                (a, leftJoinVarA, leftJoinVarB, leftJoinVarC) -> predicate.test(leftJoinVarA, leftJoinVarB, leftJoinVarC,
                        extract(a)));
        return (Child_) this;
    }

    @Override
    public final <LeftJoinVar_> Child_ filterForJoin(Variable<LeftJoinVar_> leftJoinVar,
            DefaultBiJoiner<LeftJoinVar_, A> joiner, JoinerType joinerType, int mappingIndex) {
        Function<LeftJoinVar_, Object> leftMapping = joiner.getLeftMapping(mappingIndex);
        Function<A, Object> rightMapping = joiner.getRightMapping(mappingIndex);
        Function1<PatternVar_, Object> rightExtractor = b -> rightMapping.apply(extract(b));
        Predicate2<PatternVar_, LeftJoinVar_> predicate =
                (b, a) -> joinerType.matches(leftMapping.apply(a), rightExtractor.apply(b));
        BetaIndex<PatternVar_, LeftJoinVar_, ?> index =
                createBetaIndex(joinerType, mappingIndex, leftMapping, rightExtractor);
        pattern.expr("Join using joiner #" + mappingIndex + " in " + joiner, leftJoinVar, predicate, index);
        return (Child_) this;
    }

    private <LeftJoinVar_> BetaIndex<PatternVar_, LeftJoinVar_, ?> createBetaIndex(JoinerType joinerType, int mappingIndex,
            Function<LeftJoinVar_, Object> leftMapping, Function1<PatternVar_, Object> rightExtractor) {
        if (joinerType == JoinerType.EQUAL) {
            return betaIndexedBy(Object.class, getConstraintType(joinerType), mappingIndex, rightExtractor, leftMapping::apply,
                    Object.class);
        } else { // Drools beta index on LT/LTE/GT/GTE requires Comparable.
            JoinerType reversedJoinerType = joinerType.flip();
            return betaIndexedBy(Comparable.class, getConstraintType(reversedJoinerType), mappingIndex,
                    c -> (Comparable) rightExtractor.apply(c), leftMapping::apply, Comparable.class);
        }
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
        BetaIndex2<PatternVar_, LeftJoinVarA_, LeftJoinVarB_, ?> index =
                createBetaIndex(joinerType, mappingIndex, leftMapping, rightExtractor);
        pattern.expr("Join using joiner #" + mappingIndex + " in " + joiner, leftJoinVarA, leftJoinVarB, predicate, index);
        return (Child_) this;
    }

    private <LeftJoinVarA_, LeftJoinVarB_> BetaIndex2<PatternVar_, LeftJoinVarA_, LeftJoinVarB_, ?> createBetaIndex(
            JoinerType joinerType,
            int mappingIndex, BiFunction<LeftJoinVarA_, LeftJoinVarB_, Object> leftMapping,
            Function1<PatternVar_, Object> rightExtractor) {
        if (joinerType == JoinerType.EQUAL) {
            return betaIndexedBy(Object.class, getConstraintType(joinerType), mappingIndex, rightExtractor, leftMapping::apply,
                    Object.class);
        } else { // Drools beta index on LT/LTE/GT/GTE requires Comparable.
            JoinerType reversedJoinerType = joinerType.flip();
            return betaIndexedBy(Comparable.class, getConstraintType(reversedJoinerType), mappingIndex,
                    c -> (Comparable) rightExtractor.apply(c), leftMapping::apply, Comparable.class);
        }
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
        BetaIndex3<PatternVar_, LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_, ?> index =
                createBetaIndex(joinerType, mappingIndex, leftMapping, rightExtractor);
        pattern.expr("Join using joiner #" + mappingIndex + " in " + joiner, leftJoinVarA, leftJoinVarB,
                leftJoinVarC, predicate, index);
        return (Child_) this;
    }

    private <LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_>
            BetaIndex3<PatternVar_, LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_, ?> createBetaIndex(JoinerType joinerType,
                    int mappingIndex, TriFunction<LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_, Object> leftMapping,
                    Function1<PatternVar_, Object> rightExtractor) {
        if (joinerType == JoinerType.EQUAL) {
            return betaIndexedBy(Object.class, getConstraintType(joinerType), mappingIndex, rightExtractor, leftMapping::apply,
                    Object.class);
        } else { // Drools beta index on LT/LTE/GT/GTE requires Comparable.
            JoinerType reversedJoinerType = joinerType.flip();
            return betaIndexedBy(Comparable.class, getConstraintType(reversedJoinerType), mappingIndex,
                    c -> (Comparable) rightExtractor.apply(c), leftMapping::apply, Comparable.class);
        }
    }

    @Override
    public final <BoundVar_> Child_ bind(Variable<BoundVar_> boundVariable, Function<A, BoundVar_> bindingFunction) {
        pattern.bind(boundVariable, a -> bindingFunction.apply(extract(a)));
        return (Child_) this;
    }

    @Override
    public final <BoundVar_, LeftJoinVar_> Child_ bind(Variable<BoundVar_> boundVariable,
            Variable<LeftJoinVar_> leftJoinVariable, BiFunction<A, LeftJoinVar_, BoundVar_> bindingFunction) {
        pattern.bind(boundVariable, leftJoinVariable,
                (a, leftJoinVar) -> bindingFunction.apply(extract(a), leftJoinVar));
        return (Child_) this;
    }

    @Override
    public final <BoundVar_, LeftJoinVarA_, LeftJoinVarB_> Child_ bind(Variable<BoundVar_> boundVariable,
            Variable<LeftJoinVarA_> leftJoinVariableA, Variable<LeftJoinVarB_> leftJoinVariableB,
            TriFunction<A, LeftJoinVarA_, LeftJoinVarB_, BoundVar_> bindingFunction) {
        pattern.bind(boundVariable, leftJoinVariableA, leftJoinVariableB,
                (a, leftJoinVarA, leftJoinVarB) -> bindingFunction.apply(extract(a), leftJoinVarA, leftJoinVarB));
        return (Child_) this;
    }

    @Override
    public final <BoundVar_, LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_> Child_ bind(Variable<BoundVar_> boundVariable,
            Variable<LeftJoinVarA_> leftJoinVariableA, Variable<LeftJoinVarB_> leftJoinVariableB,
            Variable<LeftJoinVarC_> leftJoinVariableC,
            QuadFunction<A, LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_, BoundVar_> bindingFunction) {
        pattern.bind(boundVariable, leftJoinVariableA, leftJoinVariableB, leftJoinVariableC,
                (a, leftJoinVarA, leftJoinVarB, leftJoinVarC) -> bindingFunction.apply(extract(a), leftJoinVarA,
                        leftJoinVarB, leftJoinVarC));
        return (Child_) this;
    }

    @Override
    public final List<ViewItem<?>> build() {
        Stream<ViewItem<?>> prerequisites = prerequisiteExpressions.stream();
        Stream<ViewItem<?>> dependents = dependentExpressions.stream();
        return Stream.concat(Stream.concat(prerequisites, Stream.of(pattern)), dependents)
                .collect(Collectors.toList());
    }
}

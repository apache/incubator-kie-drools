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

import static org.drools.model.DSL.accFunction;
import static org.drools.model.PatternDSL.alphaIndexedBy;
import static org.drools.model.PatternDSL.pattern;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.drools.model.DSL;
import org.drools.model.Drools;
import org.drools.model.Index;
import org.drools.model.PatternDSL;
import org.drools.model.PatternDSL.PatternDef;
import org.drools.model.Variable;
import org.drools.model.view.ViewItem;
import org.drools.model.view.ViewItemBuilder;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.holder.AbstractScoreHolder;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraint;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsBiCondition;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsBiRuleStructure;
import org.optaplanner.core.impl.score.stream.drools.quad.DroolsQuadCondition;
import org.optaplanner.core.impl.score.stream.drools.quad.DroolsQuadRuleStructure;
import org.optaplanner.core.impl.score.stream.drools.tri.DroolsTriCondition;
import org.optaplanner.core.impl.score.stream.drools.tri.DroolsTriRuleStructure;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsUniCondition;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsUniRuleStructure;

/**
 * Encapsulates the low-level rule creation and manipulation operations via the Drools executable model DSL
 * (see {@link PatternDSL}.
 *
 * @param <T> type of Drools rule that we operate on
 */
public abstract class DroolsCondition<PatternVar, T extends DroolsRuleStructure<PatternVar>> {

    protected final T ruleStructure;

    protected DroolsCondition(T ruleStructure) {
        this.ruleStructure = ruleStructure;
    }

    protected abstract <InTuple> PatternDef<PatternVar> bindTupleVariableOnFirstGrouping(PatternDef<PatternVar> pattern,
            Variable<InTuple> tupleVariable);

    protected <NewA, InTuple, OutTuple, __> DroolsUniCondition<NewA, NewA> collect(
            DroolsAbstractAccumulateFunction<__, InTuple, OutTuple> accumulateFunctionBridge) {
        PatternDef<PatternVar> mainAccumulatePattern = ruleStructure.getPrimaryPatternBuilder().build();
        Variable<PatternVar> baseVariable = ruleStructure.getPrimaryPatternBuilder().getBaseVariable();
        boolean isRegrouping = FactTuple.class.isAssignableFrom(baseVariable.getType());
        Variable<InTuple> tupleVariable;
        if (isRegrouping) {
            tupleVariable = (Variable<InTuple>) mainAccumulatePattern.getFirstVariable();
        } else {
            tupleVariable = ruleStructure.createVariable("tuple");
            mainAccumulatePattern = bindTupleVariableOnFirstGrouping(mainAccumulatePattern, tupleVariable);
        }
        ViewItem<?> innerAccumulatePattern = getInnerAccumulatePattern(mainAccumulatePattern);
        Variable<NewA> outputVariable = ruleStructure.createVariable("collected");
        ViewItem<?> outerAccumulatePattern = DSL.accumulate(innerAccumulatePattern,
                accFunction(() -> accumulateFunctionBridge, tupleVariable).as(outputVariable));
        DroolsUniRuleStructure<NewA, NewA> newRuleStructure = ruleStructure.recollect(outputVariable,
                outerAccumulatePattern);
        return new DroolsUniCondition<>(newRuleStructure);
    }

    protected <NewA, NewB, InTuple, OutPatternVar> DroolsBiCondition<NewA, NewB, OutPatternVar> groupWithCollect(
            Supplier<? extends DroolsAbstractGroupByAccumulator<InTuple>> invokerSupplier) {
        return universalGroupWithCollect(invokerSupplier, (var, pattern, accumulate) -> {
            DroolsBiRuleStructure<NewA, NewB, OutPatternVar> newRuleStructure = ruleStructure.regroupBi((Variable) var,
                    (PatternDef) pattern, accumulate);
            return new DroolsBiCondition<>(newRuleStructure);
        });
    }

    protected <NewA, NewB, NewC, InTuple, OutPatternVar> DroolsTriCondition<NewA, NewB, NewC, OutPatternVar> groupBiWithCollect(
            Supplier<? extends DroolsAbstractGroupByAccumulator<InTuple>> invokerSupplier) {
        return universalGroupWithCollect(invokerSupplier, (var, pattern, accumulate) -> {
            DroolsTriRuleStructure<NewA, NewB, NewC, OutPatternVar> newRuleStructure = ruleStructure
                    .regroupBiToTri((Variable) var, (PatternDef) pattern, accumulate);
            return new DroolsTriCondition<>(newRuleStructure);
        });
    }

    protected <NewA, NewB, NewC, NewD, InTuple, OutPatternVar> DroolsQuadCondition<NewA, NewB, NewC, NewD, OutPatternVar>
            groupBiWithCollectBi(Supplier<? extends DroolsAbstractGroupByAccumulator<InTuple>> invokerSupplier) {
        return universalGroupWithCollect(invokerSupplier, (var, pattern, accumulate) -> {
            DroolsQuadRuleStructure<NewA, NewB, NewC, NewD, OutPatternVar> newRuleStructure = ruleStructure
                    .regroupBiToQuad((Variable) var, (PatternDef) pattern, accumulate);
            return new DroolsQuadCondition<>(newRuleStructure);
        });
    }

    private <InTuple, OutPatternVar, R extends DroolsRuleStructure<OutPatternVar>, C extends DroolsCondition<OutPatternVar, R>>
            C universalGroupWithCollect(Supplier<? extends DroolsAbstractGroupByAccumulator<InTuple>> invokerSupplier,
                    Mutator<InTuple, OutPatternVar, R, C> mutator) {
        Variable<Collection<InTuple>> tupleCollection = (Variable<Collection<InTuple>>) ruleStructure
                .createVariable(Collection.class, "tupleCollection");
        PatternDSL.PatternDef<Collection<InTuple>> pattern = pattern(tupleCollection)
                .expr("Non-empty", collection -> !collection.isEmpty(),
                        alphaIndexedBy(Integer.class, Index.ConstraintType.GREATER_THAN, -1, Collection::size, 0));
        PatternDSL.PatternDef<PatternVar> innerCollectingPattern = ruleStructure.getPrimaryPatternBuilder().build();
        ViewItem<?> innerAccumulatePattern = getInnerAccumulatePattern(innerCollectingPattern);
        ViewItem<?> accumulate = DSL.accumulate(innerAccumulatePattern, accFunction(invokerSupplier).as(tupleCollection));
        return mutator.apply(tupleCollection, pattern, accumulate);
    }

    protected <S extends Score<S>, H extends AbstractScoreHolder<S>> void impactScore(Drools drools, H scoreHolder) {
        RuleContext kcontext = (RuleContext) drools;
        scoreHolder.impactScore(kcontext);
    }

    protected <S extends Score<S>, H extends AbstractScoreHolder<S>> void impactScore(DroolsConstraint<?> constraint,
            Drools drools, H scoreHolder, int impact) {
        RuleContext kcontext = (RuleContext) drools;
        constraint.assertCorrectImpact(impact);
        scoreHolder.impactScore(kcontext, impact);
    }

    protected <S extends Score<S>, H extends AbstractScoreHolder<S>> void impactScore(DroolsConstraint<?> constraint,
            Drools drools, H scoreHolder, long impact) {
        RuleContext kcontext = (RuleContext) drools;
        constraint.assertCorrectImpact(impact);
        scoreHolder.impactScore(kcontext, impact);
    }

    protected <S extends Score<S>, H extends AbstractScoreHolder<S>> void impactScore(DroolsConstraint<?> constraint,
            Drools drools, H scoreHolder, BigDecimal impact) {
        RuleContext kcontext = (RuleContext) drools;
        constraint.assertCorrectImpact(impact);
        scoreHolder.impactScore(kcontext, impact);
    }

    protected ViewItem<?> getInnerAccumulatePattern(PatternDef<PatternVar> mainAccumulatePattern) {
        Stream<ViewItemBuilder<?>> primaryAndPrerequisites = Stream.concat(ruleStructure.getPrerequisites().stream(),
                Stream.of(mainAccumulatePattern));
        Stream<ViewItemBuilder<?>> all = Stream.concat(primaryAndPrerequisites, ruleStructure.getDependents().stream());
        ViewItem[] items = all.toArray(ViewItem[]::new);
        return PatternDSL.and(items[0], Arrays.copyOfRange(items, 1, items.length));
    }

    public T getRuleStructure() {
        return ruleStructure;
    }

    public Class[] getExpectedJustificationTypes() {
        return ruleStructure.getExpectedJustificationTypes();
    }

    @FunctionalInterface
    protected interface Mutator<InTuple, OutPatternVar, R extends DroolsRuleStructure<OutPatternVar>, C extends DroolsCondition<OutPatternVar, R>>
            extends
            TriFunction<Variable<Collection<InTuple>>, PatternDef<Collection<InTuple>>, ViewItem<?>, C> {

    }

    /**
     * When two filters follow one another immediately, we merge them into a new {@link Predicate}. This is done for
     * performance reasons, as filters are not indexed and therefore we only want to pay the penalty once.
     *
     * This class is a data carrier facilitating that feature.
     *
     * @param <PredicateType> type of the predicate (uni, bi, ...) matching the stream
     */
    public final class ImmediatelyPreviousFilter<PredicateType> {

        public final T ruleStructure;
        public final PredicateType predicate;

        public ImmediatelyPreviousFilter(T ruleStructure, PredicateType predicate) {
            this.ruleStructure = ruleStructure;
            this.predicate = predicate;
        }

    }

}

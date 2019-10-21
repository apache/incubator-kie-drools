/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.drools.uni;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.drools.core.base.accumulators.CollectSetAccumulateFunction;
import org.drools.model.DSL;
import org.drools.model.Declaration;
import org.drools.model.Drools;
import org.drools.model.Global;
import org.drools.model.PatternDSL;
import org.drools.model.RuleItemBuilder;
import org.drools.model.Variable;
import org.drools.model.consequences.ConsequenceBuilder;
import org.drools.model.functions.Block3;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Predicate2;
import org.drools.model.view.ExprViewItem;
import org.kie.api.runtime.rule.AccumulateFunction;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.score.stream.bi.AbstractBiJoiner;
import org.optaplanner.core.impl.score.stream.common.JoinerType;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsBiCondition;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAccumulateContext;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsGenuineMetadata;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsInferredMetadata;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsLogicalTuple;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsMetadata;

import static org.drools.model.DSL.accFunction;
import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.on;
import static org.drools.model.PatternDSL.from;
import static org.drools.model.PatternDSL.pattern;

public final class DroolsUniCondition<A> {

    private final DroolsMetadata<Object, A> aMetadata;

    public DroolsUniCondition(Class<A> aVariableType) {
        Declaration<A> aVariableDeclaration = declarationOf(aVariableType);
        this.aMetadata = (DroolsGenuineMetadata) DroolsMetadata.ofGenuine(aVariableDeclaration);
    }

    public DroolsUniCondition(Declaration<DroolsLogicalTuple> aVariableDeclaration,
            Function<Declaration<DroolsLogicalTuple>, PatternDSL.PatternDef<DroolsLogicalTuple>> patternProvider) {
        this.aMetadata = (DroolsInferredMetadata) DroolsMetadata.ofInferred(aVariableDeclaration,
                () -> patternProvider.apply(aVariableDeclaration));
    }

    private DroolsUniCondition(DroolsMetadata<Object, A> aMetadata) {
        this.aMetadata = aMetadata;
    }

    public DroolsMetadata<Object, A> getAMetadata() {
        return aMetadata;
    }

    public DroolsUniCondition<A> andFilter(Predicate<A> predicate) {
        Supplier<PatternDSL.PatternDef<Object>> patternSupplier = () -> aMetadata.buildPattern()
                .expr(a -> predicate.test(aMetadata.extract(a)));
        return new DroolsUniCondition<>(aMetadata.substitute(patternSupplier));
    }

    public <B> DroolsBiCondition<A, B> andJoin(DroolsUniCondition<B> bCondition, AbstractBiJoiner<A, B> biJoiner) {
        DroolsMetadata<Object, B> bMetadata = bCondition.aMetadata;
        Supplier<PatternDSL.PatternDef<Object>> patternSupplier = () -> bMetadata.buildPattern()
                .expr(aMetadata.getVariableDeclaration(),
                        (b, a) -> matches(biJoiner, aMetadata.extract(a), bMetadata.extract(b)));
        return new DroolsBiCondition<>(aMetadata, bMetadata.substitute(patternSupplier));
    }

    /**
     * The goal of this method is to create the left-hand side of a rule to look like this:
     *
     * <pre>
     * when
     *     accumulate(Person(), $set: collectSet(Person::getCity))
     *     $newA : City() from $set // grouping
     *     accumulate(Person(getCity() == $newA), $newB: collect(ConstraintCollectors.count()))
     * then
     *     insertLogical($newA, $newB);
     * end
     * </pre>
     *
     * Note: This is pseudo-code and the actual Drools code will look slightly different in terms of syntax.
     * @param ruleId never null, id of the context in which the new facts will be inserted
     * @param groupKeyMapping never null, grouping to apply
     * @param collector never null, collector to apply
     * @param <ResultContainer> implementation detail, unimportant
     * @param <NewA> type of the first logical fact
     * @param <NewB> type of the second logical fact
     * @return
     */
    public <ResultContainer, NewA, NewB> List<RuleItemBuilder<?>> completeWithLogicalInsert(
            Object ruleId, Function<A, NewA> groupKeyMapping,
            UniConstraintCollector<A, ResultContainer, NewB> collector) {
        Function1<Object, A> extractor = aMetadata::extract;
        Function1<Object, NewA> grouper = a -> groupKeyMapping.apply(extractor.apply(a));
        // Accumulate all NewA into a set.
        Declaration<NewA> innerNewADeclaration = (Declaration<NewA>) declarationOf(Object.class);
        PatternDSL.PatternDef<Object> innerNewACollectingPattern = aMetadata.buildPattern()
                .bind(innerNewADeclaration, grouper);
        Declaration<Object> setOfNewADeclaration = declarationOf(Object.class);
        ExprViewItem<Object> collectingPattern = DSL.accumulate(innerNewACollectingPattern,
                accFunction(CollectSetAccumulateFunction::new, innerNewADeclaration).as(setOfNewADeclaration));
        // Operate individually with every NewA from that set, creating the final grouping.
        Declaration<NewA> actualNewADeclaration =
                (Declaration<NewA>) declarationOf(Object.class, from(setOfNewADeclaration));
        // And run an accumulate on all A which match NewA, applying the collector.
        Predicate2<Object, NewA> matcher = (a, newA) -> grouper.apply(a).equals(newA);
        Declaration<A> aVariable = (Declaration<A>) aMetadata.getVariableDeclaration();
        PatternDSL.PatternDef<Object> innerAccumulatePattern = aMetadata.buildPattern()
                .expr(actualNewADeclaration, matcher)
                .bind(aVariable, extractor);
        AccumulateFunction<DroolsAccumulateContext<ResultContainer>> accumulateFunction =
                new DroolsUniAccumulateFunctionBridge<>(collector);
        Declaration<Object> accumulateResultVariable = declarationOf(Object.class);
        ExprViewItem<Object> outerAccumulatePattern = DSL.accumulate(innerAccumulatePattern,
                accFunction(() -> accumulateFunction, aVariable).as(accumulateResultVariable));
        ConsequenceBuilder._2<?, ?> consequence = on(actualNewADeclaration, accumulateResultVariable)
                .execute((drools, newA, newB) -> {
                    RuleContext kcontext = (RuleContext) drools;
                    kcontext.insertLogical(new DroolsLogicalTuple(ruleId, newA, newB));
                });
        return Arrays.asList(collectingPattern, pattern(setOfNewADeclaration), pattern(actualNewADeclaration),
                outerAccumulatePattern, consequence);
    }

    public <ResultContainer, NewA> List<RuleItemBuilder<?>> completeWithLogicalInsert(
            Object ruleId, UniConstraintCollector<A, ResultContainer, NewA> collector) {
        DroolsMetadata<Object, A> inputMetadata = getAMetadata();
        Variable<Object> inputVariable = inputMetadata.getVariableDeclaration();
        PatternDSL.PatternDef<Object> innerAccumulatePattern =
                inputMetadata.buildPattern().bind(inputVariable, inputMetadata::extract);
        AccumulateFunction<DroolsAccumulateContext<ResultContainer>> accumulateFunction =
                new DroolsUniAccumulateFunctionBridge<>(collector);
        Variable<Object> outputVariable = declarationOf(Object.class);
        ExprViewItem<Object> outerAccumulatePattern = DSL.accumulate(innerAccumulatePattern,
                accFunction(() -> accumulateFunction, inputVariable).as(outputVariable));
        ConsequenceBuilder._1<?> consequence = on(outputVariable)
                .execute((drools, newA) -> {
                    RuleContext kcontext = (RuleContext) drools;
                    kcontext.insertLogical(new DroolsLogicalTuple(ruleId, newA));
                });
        return Arrays.asList(outerAccumulatePattern, consequence);
    }

    public <GroupKey_> List<RuleItemBuilder<?>> completeWithLogicalInsert(Object ruleId,
            Function<A, GroupKey_> groupKeyMapping) {
        ConsequenceBuilder._1<?> consequence = on(aMetadata.getVariableDeclaration())
                .execute((drools, a) -> {
                    GroupKey_ aMapped = groupKeyMapping.apply(aMetadata.extract(a));
                    RuleContext kcontext = (RuleContext) drools;
                    kcontext.insertLogical(new DroolsLogicalTuple(ruleId, aMapped));
                });
        return Arrays.asList(aMetadata.buildPattern(), consequence);
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal) {
        return completeWithScoring(scoreHolderGlobal, (drools, scoreHolder, __) -> {
            RuleContext kcontext = (RuleContext) drools;
            scoreHolder.impactScore(kcontext);
        });
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal,
            ToIntFunction<A> matchWeighter) {
        return completeWithScoring(scoreHolderGlobal, (drools, scoreHolder, a) -> {
            int weightMultiplier = matchWeighter.applyAsInt(aMetadata.extract(a));
            RuleContext kcontext = (RuleContext) drools;
            scoreHolder.impactScore(kcontext, weightMultiplier);
        });
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal,
            ToLongFunction<A> matchWeighter) {
        return completeWithScoring(scoreHolderGlobal, (drools, scoreHolder, a) -> {
            long weightMultiplier = matchWeighter.applyAsLong(aMetadata.extract(a));
            RuleContext kcontext = (RuleContext) drools;
            scoreHolder.impactScore(kcontext, weightMultiplier);
        });
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal,
            Function<A, BigDecimal> matchWeighter) {
        return completeWithScoring(scoreHolderGlobal, (drools, scoreHolder, a) -> {
            BigDecimal weightMultiplier = matchWeighter.apply(aMetadata.extract(a));
            RuleContext kcontext = (RuleContext) drools;
            scoreHolder.impactScore(kcontext, weightMultiplier);
        });
    }

    private <ScoreHolder extends AbstractScoreHolder<?>> List<RuleItemBuilder<?>> completeWithScoring(
            Global<ScoreHolder> scoreHolderGlobal, Block3<Drools, ScoreHolder, Object> consequenceImpl) {
        ConsequenceBuilder._2<ScoreHolder, Object> consequence =
                on(scoreHolderGlobal, aMetadata.getVariableDeclaration())
                        .execute(consequenceImpl);
        return Arrays.asList(aMetadata.buildPattern(), consequence);
    }

    private static <A, B> boolean matches(AbstractBiJoiner<A, B> biJoiner, A left, B right) {
        Object[] leftMappings = biJoiner.getLeftCombinedMapping().apply(left);
        Object[] rightMappings = biJoiner.getRightCombinedMapping().apply(right);
        JoinerType[] joinerTypes = biJoiner.getJoinerTypes();
        for (int i = 0; i < joinerTypes.length; i++) {
            JoinerType joinerType = joinerTypes[i];
            if (!joinerType.matches(leftMappings[i], rightMappings[i])) {
                return false;
            }
        }
        return true;
    }

}

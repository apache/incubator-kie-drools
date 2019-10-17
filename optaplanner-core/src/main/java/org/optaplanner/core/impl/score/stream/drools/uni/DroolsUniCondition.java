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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.drools.model.DSL;
import org.drools.model.Declaration;
import org.drools.model.Drools;
import org.drools.model.Global;
import org.drools.model.PatternDSL;
import org.drools.model.RuleItemBuilder;
import org.drools.model.Variable;
import org.drools.model.consequences.ConsequenceBuilder;
import org.drools.model.functions.Block3;
import org.drools.model.view.ExprViewItem;
import org.kie.api.runtime.rule.AccumulateFunction;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.score.stream.bi.AbstractBiJoiner;
import org.optaplanner.core.impl.score.stream.common.JoinerType;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsBiCondition;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsGenuineMetadata;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsInferredMetadata;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsLogicalTuple;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsMetadata;

import static org.drools.model.DSL.accFunction;
import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.on;

public final class DroolsUniCondition<A> {

    private final DroolsMetadata<Object, A> aMetadata;

    public DroolsUniCondition(Class<A> aVariableType) {
        Declaration<A> aVariableDeclaration = declarationOf(aVariableType);
        this.aMetadata = (DroolsGenuineMetadata) DroolsMetadata.ofGenuine(aVariableDeclaration,
                PatternDSL.pattern(aVariableDeclaration));
    }

    public DroolsUniCondition(Declaration<DroolsLogicalTuple> aVariableDeclaration,
            Function<Declaration<DroolsLogicalTuple>, PatternDSL.PatternDef<DroolsLogicalTuple>> patternProvider) {
        this.aMetadata = (DroolsInferredMetadata) DroolsMetadata.ofInferred(aVariableDeclaration,
                patternProvider.apply(aVariableDeclaration));
    }

    private DroolsUniCondition(DroolsMetadata<Object, A> aMetadata) {
        this.aMetadata = aMetadata;
    }

    public DroolsMetadata<Object, A> getAMetadata() {
        return aMetadata;
    }

    public DroolsUniCondition<A> andFilter(Predicate<A> predicate) {
        PatternDSL.PatternDef<Object> newPattern = aMetadata.getPattern()
                .expr(a -> predicate.test(aMetadata.extract(a)));
        if (aMetadata instanceof DroolsInferredMetadata) {
            return new DroolsUniCondition<>(((DroolsInferredMetadata) aMetadata).substitute(newPattern));
        } else {
            return new DroolsUniCondition<>(((DroolsGenuineMetadata) aMetadata).substitute(newPattern));
        }
    }

    public <B> DroolsBiCondition<A, B> andJoin(DroolsUniCondition<B> bCondition, AbstractBiJoiner<A, B> biJoiner) {
        DroolsMetadata<Object, B> bMetadata = bCondition.aMetadata;
        PatternDSL.PatternDef<Object> newPattern = bMetadata.getPattern()
                .expr(aMetadata.getVariableDeclaration(),
                        (b, a) -> matches(biJoiner, aMetadata.extract(a), bMetadata.extract(b)));
        if (bMetadata instanceof DroolsInferredMetadata) {
            return new DroolsBiCondition<>(aMetadata, ((DroolsInferredMetadata) bMetadata).substitute(newPattern));
        } else {
            return new DroolsBiCondition<>(aMetadata, ((DroolsGenuineMetadata) bMetadata).substitute(newPattern));
        }
    }

    public <ResultContainer extends Serializable, NewA> List<RuleItemBuilder<?>> completeWithLogicalInsert(
            Object ruleId, UniConstraintCollector<A, ResultContainer, NewA> collector) {
        DroolsMetadata<Object, A> inputMetadata = getAMetadata();
        Variable<Object> inputVariable = inputMetadata.getVariableDeclaration();
        PatternDSL.PatternDef<Object> innerAccumulatePattern =
                inputMetadata.getPattern().bind(inputVariable, inputMetadata::extract);
        AccumulateFunction<ResultContainer> accumulateFunction = new DroolsUniAccumulateFunctionBridge<>(collector);
        Variable<Object> outputVariable = declarationOf(Object.class);
        ExprViewItem<Object> outerAccumulatePattern = DSL.accumulate(innerAccumulatePattern,
                accFunction(() -> accumulateFunction, inputVariable).as(outputVariable));
        ConsequenceBuilder._1<?> consequence = on(outputVariable)
                .execute((drools, accumulateResult) -> {
                    RuleContext kcontext = (RuleContext) drools;
                    kcontext.insertLogical(new DroolsLogicalTuple(ruleId, accumulateResult));
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
        return Arrays.asList(aMetadata.getPattern(), consequence);
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
        return Arrays.asList(aMetadata.getPattern(), consequence);
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

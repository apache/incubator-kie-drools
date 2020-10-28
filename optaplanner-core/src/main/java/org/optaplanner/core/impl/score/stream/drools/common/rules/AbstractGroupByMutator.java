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

package org.optaplanner.core.impl.score.stream.drools.common.rules;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.drools.model.DSL.accFunction;
import static org.drools.model.DSL.and;
import static org.drools.model.DSL.from;
import static org.drools.model.PatternDSL.alphaIndexedBy;
import static org.drools.model.PatternDSL.pattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.drools.model.DSL;
import org.drools.model.Index;
import org.drools.model.PatternDSL;
import org.drools.model.PatternDSL.PatternDef;
import org.drools.model.Variable;
import org.drools.model.view.ViewItem;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.impl.score.stream.drools.common.BiTuple;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractAccumulateFunction;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractGroupByAccumulator;
import org.optaplanner.core.impl.score.stream.drools.common.FactTuple;
import org.optaplanner.core.impl.score.stream.drools.common.QuadTuple;
import org.optaplanner.core.impl.score.stream.drools.common.TriTuple;

abstract class AbstractGroupByMutator implements Mutator {

    protected abstract <InTuple> PatternDef bindTupleVariableOnFirstGrouping(AbstractRuleAssembler ruleAssembler,
            PatternDef pattern, Variable<InTuple> tupleVariable);

    protected ViewItem getInnerAccumulatePattern(AbstractRuleAssembler<?> ruleAssembler) {
        ruleAssembler.applyFilterToLastPrimaryPattern();
        List<ViewItem> patternList = new ArrayList<>();
        for (int i = 0; i < ruleAssembler.getPrimaryPatterns().size(); i++) {
            patternList.add(ruleAssembler.getPrimaryPatterns().get(i));
            patternList.addAll(ruleAssembler.getDependentExpressionMap().getOrDefault(i, Collections.emptyList()));
        }
        ViewItem firstPattern = patternList.get(0);
        if (patternList.size() == 1) {
            return firstPattern;
        }
        ViewItem[] remainingPatternArray = patternList.subList(1, patternList.size())
                .toArray(new ViewItem[0]);
        return and(firstPattern, remainingPatternArray);
    }

    protected <NewA, InTuple, OutTuple> AbstractRuleAssembler collect(AbstractRuleAssembler ruleAssembler,
            DroolsAbstractAccumulateFunction<?, InTuple, OutTuple> accumulateFunctionBridge) {
        PatternDef mainAccumulatePattern = ruleAssembler.getLastPrimaryPattern();
        boolean isRegrouping = FactTuple.class.isAssignableFrom(mainAccumulatePattern.getFirstVariable().getType());
        Variable<InTuple> tupleVariable = isRegrouping ? mainAccumulatePattern.getFirstVariable()
                : ruleAssembler.createVariable("tuple");
        if (!isRegrouping) {
            bindTupleVariableOnFirstGrouping(ruleAssembler, mainAccumulatePattern, tupleVariable);
        }
        ViewItem<?> innerAccumulatePattern = getInnerAccumulatePattern(ruleAssembler);
        Variable<NewA> outputVariable = ruleAssembler.createVariable("collected");
        ViewItem<?> outerAccumulatePattern = DSL.accumulate(innerAccumulatePattern,
                accFunction(() -> accumulateFunctionBridge, tupleVariable).as(outputVariable));
        return recollect(ruleAssembler, outputVariable, outerAccumulatePattern);
    }

    protected <InTuple> AbstractRuleAssembler groupWithCollect(AbstractRuleAssembler ruleAssembler,
            Supplier<? extends DroolsAbstractGroupByAccumulator<InTuple>> invokerSupplier) {
        return universalGroupWithCollect(ruleAssembler, invokerSupplier,
                (var, pattern, accumulate) -> regroupBi(ruleAssembler, (Variable) var, pattern, accumulate));
    }

    private <InTuple> AbstractRuleAssembler universalGroupWithCollect(AbstractRuleAssembler ruleAssembler,
            Supplier<? extends DroolsAbstractGroupByAccumulator<InTuple>> invokerSupplier, Transformer<InTuple> mutator) {
        ViewItem<?> innerAccumulatePattern = getInnerAccumulatePattern(ruleAssembler);
        Variable<Collection<InTuple>> tupleCollection =
                (Variable<Collection<InTuple>>) ruleAssembler.createVariable(Collection.class, "tupleCollection");
        PatternDef<Collection<InTuple>> pattern = pattern(tupleCollection)
                .expr("Non-empty", collection -> !collection.isEmpty(),
                        alphaIndexedBy(Integer.class, Index.ConstraintType.GREATER_THAN, -1, Collection::size, 0));
        ViewItem<?> accumulate = DSL.accumulate(innerAccumulatePattern, accFunction(invokerSupplier).as(tupleCollection));
        return mutator.apply(tupleCollection, pattern, accumulate);
    }

    protected <InTuple> AbstractRuleAssembler groupBiWithCollect(AbstractRuleAssembler ruleAssembler,
            Supplier<? extends DroolsAbstractGroupByAccumulator<InTuple>> invokerSupplier) {
        return universalGroupWithCollect(ruleAssembler, invokerSupplier,
                (var, pattern, accumulate) -> regroupBiToTri(ruleAssembler, (Variable) var, pattern, accumulate));
    }

    protected <InTuple> AbstractRuleAssembler groupBiWithCollectBi(AbstractRuleAssembler ruleAssembler,
            Supplier<? extends DroolsAbstractGroupByAccumulator<InTuple>> invokerSupplier) {
        return universalGroupWithCollect(ruleAssembler, invokerSupplier,
                (var, pattern, accumulate) -> regroupBiToQuad(ruleAssembler, (Variable) var, pattern, accumulate));
    }

    protected <NewA> AbstractRuleAssembler recollect(AbstractRuleAssembler ruleAssembler, Variable<NewA> newA,
            ViewItem accumulatePattern) {
        List<ViewItem> newFinishedExpressions = new ArrayList<>(ruleAssembler.getFinishedExpressions());
        newFinishedExpressions.add(accumulatePattern); // The last pattern is added here.
        PatternDef<NewA> newPrimaryPattern = pattern(newA);
        return new UniRuleAssembler(ruleAssembler, ruleAssembler.getExpectedGroupByCount(), newFinishedExpressions,
                newA, singletonList(newPrimaryPattern), emptyMap());
    }

    public <NewA> AbstractRuleAssembler regroup(AbstractRuleAssembler ruleAssembler, Variable<Collection<NewA>> newASource,
            ViewItem collectPattern, ViewItem accumulatePattern) {
        ruleAssembler.applyFilterToLastPrimaryPattern();
        List<ViewItem> newFinishedExpressions = new ArrayList<>(ruleAssembler.getFinishedExpressions());
        newFinishedExpressions.add(accumulatePattern);
        newFinishedExpressions.add(collectPattern);
        Variable<NewA> newA =
                (Variable<NewA>) ruleAssembler.createVariable("uniGrouped", DSL.from(newASource));
        PatternDef<NewA> newPrimaryPattern = pattern(newA);
        return new UniRuleAssembler(ruleAssembler, ruleAssembler.getExpectedGroupByCount(), newFinishedExpressions,
                newA, singletonList(newPrimaryPattern), emptyMap());
    }

    public <NewA, NewB> AbstractRuleAssembler regroupBi(AbstractRuleAssembler ruleAssembler,
            Variable<Collection<BiTuple<NewA, NewB>>> newSource, ViewItem collectPattern, ViewItem accumulatePattern) {
        ruleAssembler.applyFilterToLastPrimaryPattern();
        Variable<BiTuple<NewA, NewB>> newTuple =
                (Variable<BiTuple<NewA, NewB>>) ruleAssembler.createVariable(BiTuple.class, "biGrouped", from(newSource));
        List<ViewItem> newFinishedExpressions = new ArrayList<>(ruleAssembler.getFinishedExpressions());
        newFinishedExpressions.add(accumulatePattern);
        newFinishedExpressions.add(collectPattern);
        Variable<NewA> newA = ruleAssembler.createVariable("newA");
        Variable<NewB> newB = ruleAssembler.createVariable("newB");
        PatternDef<BiTuple<NewA, NewB>> newPrimaryPattern = pattern(newTuple)
                .bind(newTuple, tuple -> tuple)
                .bind(newA, tuple -> tuple.a)
                .bind(newB, tuple -> tuple.b);
        return new BiRuleAssembler(ruleAssembler, ruleAssembler.getExpectedGroupByCount(), newFinishedExpressions,
                newA, newB, singletonList(newPrimaryPattern), emptyMap());
    }

    public <NewA, NewB, NewC> AbstractRuleAssembler regroupBiToTri(AbstractRuleAssembler ruleAssembler,
            Variable<Set<TriTuple<NewA, NewB, NewC>>> newSource, ViewItem collectPattern,
            ViewItem accumulatePattern) {
        ruleAssembler.applyFilterToLastPrimaryPattern();
        List<ViewItem> newFinishedExpressions = new ArrayList<>(ruleAssembler.getFinishedExpressions());
        newFinishedExpressions.add(accumulatePattern);
        newFinishedExpressions.add(collectPattern);
        Variable<NewA> newA = ruleAssembler.createVariable("newA");
        Variable<NewB> newB = ruleAssembler.createVariable("newB");
        Variable<NewC> newC = ruleAssembler.createVariable("newC");
        Variable<TriTuple<NewA, NewB, NewC>> newTuple =
                (Variable<TriTuple<NewA, NewB, NewC>>) ruleAssembler.createVariable(TriTuple.class, "triGrouped",
                        from(newSource));
        PatternDef<TriTuple<NewA, NewB, NewC>> newPrimaryPattern = pattern(newTuple)
                .bind(newTuple, tuple -> tuple)
                .bind(newA, tuple -> tuple.a)
                .bind(newB, tuple -> tuple.b)
                .bind(newC, tuple -> tuple.c);
        return new TriRuleAssembler(ruleAssembler, ruleAssembler.getExpectedGroupByCount(), newFinishedExpressions,
                newA, newB, newC, singletonList(newPrimaryPattern), emptyMap());
    }

    public <NewA, NewB, NewC, NewD> AbstractRuleAssembler regroupBiToQuad(AbstractRuleAssembler ruleAssembler,
            Variable<Set<QuadTuple<NewA, NewB, NewC, NewD>>> newSource, ViewItem collectPattern,
            ViewItem accumulatePattern) {
        ruleAssembler.applyFilterToLastPrimaryPattern();
        List<ViewItem> newFinishedExpressions = new ArrayList<>(ruleAssembler.getFinishedExpressions());
        newFinishedExpressions.add(accumulatePattern);
        newFinishedExpressions.add(collectPattern);
        Variable<NewA> newA = ruleAssembler.createVariable("newA");
        Variable<NewB> newB = ruleAssembler.createVariable("newB");
        Variable<NewC> newC = ruleAssembler.createVariable("newC");
        Variable<NewD> newD = ruleAssembler.createVariable("newD");
        Variable<QuadTuple<NewA, NewB, NewC, NewD>> newTuple =
                (Variable<QuadTuple<NewA, NewB, NewC, NewD>>) ruleAssembler.createVariable(QuadTuple.class, "quadGrouped",
                        from(newSource));
        PatternDef<QuadTuple<NewA, NewB, NewC, NewD>> newPrimaryPattern = pattern(newTuple)
                .bind(newTuple, tuple -> tuple)
                .bind(newA, tuple -> tuple.a)
                .bind(newB, tuple -> tuple.b)
                .bind(newC, tuple -> tuple.c)
                .bind(newD, tuple -> tuple.d);
        return new QuadRuleAssembler(ruleAssembler, ruleAssembler.getExpectedGroupByCount(), newFinishedExpressions,
                newA, newB, newC, newD, singletonList(newPrimaryPattern), emptyMap());
    }

    protected UniRuleAssembler downgrade(BiRuleAssembler ruleAssembler) {
        // Downgrade the bi-stream to a uni-stream by ignoring the dummy no-op collector variable.
        return new UniRuleAssembler(ruleAssembler, ruleAssembler.getExpectedGroupByCount(),
                ruleAssembler.getFinishedExpressions(), ruleAssembler.getVariable(0),
                ruleAssembler.getPrimaryPatterns(), emptyMap());
    }

    protected BiRuleAssembler downgrade(TriRuleAssembler ruleAssembler) {
        // Downgrade the tri-stream to a bi-stream by ignoring the dummy no-op collector variable.
        return new BiRuleAssembler(ruleAssembler, ruleAssembler.getExpectedGroupByCount(),
                ruleAssembler.getFinishedExpressions(), ruleAssembler.getVariable(0), ruleAssembler.getVariable(1),
                ruleAssembler.getPrimaryPatterns(), emptyMap());
    }

    protected <NewA> UniRuleAssembler toUni(AbstractRuleAssembler ruleAssembler, ViewItem groupBy,
            Variable<NewA> aVariable) {
        List<ViewItem> newFinishedExpressions = new ArrayList<>(ruleAssembler.getFinishedExpressions());
        newFinishedExpressions.add(groupBy); // The last pattern is added here.
        PatternDSL.PatternDef<NewA> newPrimaryPattern = pattern(aVariable);
        return new UniRuleAssembler(ruleAssembler, ruleAssembler.getExpectedGroupByCount(), newFinishedExpressions,
                aVariable, singletonList(newPrimaryPattern), emptyMap());
    }

    protected <NewA, NewB> BiRuleAssembler toBi(AbstractRuleAssembler ruleAssembler, ViewItem groupBy,
            Variable<NewA> aVariable, Variable<NewB> bVariable) {
        List<ViewItem> newFinishedExpressions = new ArrayList<>(ruleAssembler.getFinishedExpressions());
        newFinishedExpressions.add(groupBy); // The last pattern is added here.
        PatternDSL.PatternDef<NewA> newAPattern = pattern(aVariable);
        newFinishedExpressions.add(newAPattern);
        PatternDSL.PatternDef<NewB> newPrimaryPattern = pattern(bVariable);
        return new BiRuleAssembler(ruleAssembler, ruleAssembler.getExpectedGroupByCount(), newFinishedExpressions,
                aVariable, bVariable, singletonList(newPrimaryPattern), emptyMap());
    }

    protected <NewA, NewB, NewC> TriRuleAssembler toTri(AbstractRuleAssembler ruleAssembler, ViewItem groupBy,
            Variable<NewA> aVariable, Variable<NewB> bVariable, Variable<NewC> cVariable) {
        List<ViewItem> newFinishedExpressions = new ArrayList<>(ruleAssembler.getFinishedExpressions());
        newFinishedExpressions.add(groupBy); // The last pattern is added here.
        PatternDSL.PatternDef<NewA> newAPattern = pattern(aVariable);
        newFinishedExpressions.add(newAPattern);
        PatternDSL.PatternDef<NewB> newBPattern = pattern(bVariable);
        newFinishedExpressions.add(newBPattern);
        PatternDSL.PatternDef<NewC> newPrimaryPattern = pattern(cVariable);
        return new TriRuleAssembler(ruleAssembler, ruleAssembler.getExpectedGroupByCount(), newFinishedExpressions,
                aVariable, bVariable, cVariable, singletonList(newPrimaryPattern), emptyMap());
    }

    protected <NewA, NewB, NewC, NewD> QuadRuleAssembler toQuad(AbstractRuleAssembler ruleAssembler, ViewItem groupBy,
            Variable<NewA> aVariable, Variable<NewB> bVariable, Variable<NewC> cVariable, Variable<NewD> dVariable) {
        List<ViewItem> newFinishedExpressions = new ArrayList<>(ruleAssembler.getFinishedExpressions());
        newFinishedExpressions.add(groupBy); // The last pattern is added here.
        PatternDSL.PatternDef<NewA> newAPattern = pattern(aVariable);
        newFinishedExpressions.add(newAPattern);
        PatternDSL.PatternDef<NewB> newBPattern = pattern(bVariable);
        newFinishedExpressions.add(newBPattern);
        PatternDSL.PatternDef<NewC> newCPattern = pattern(cVariable);
        newFinishedExpressions.add(newCPattern);
        PatternDSL.PatternDef<NewD> newPrimaryPattern = pattern(dVariable);
        return new QuadRuleAssembler(ruleAssembler, ruleAssembler.getExpectedGroupByCount(), newFinishedExpressions,
                aVariable, bVariable, cVariable, dVariable, singletonList(newPrimaryPattern), emptyMap());
    }

    @FunctionalInterface
    protected interface Transformer<InTuple> extends
            TriFunction<Variable<Collection<InTuple>>, PatternDef<Collection<InTuple>>, ViewItem<?>, AbstractRuleAssembler> {

    }
}

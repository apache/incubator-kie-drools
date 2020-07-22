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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.drools.model.DSL;
import org.drools.model.Index;
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
        ruleAssembler.applyFilterToLastPrimaryPattern();
        PatternDef mainAccumulatePattern = ruleAssembler.getLastPrimaryPattern();
        boolean isRegrouping = FactTuple.class.isAssignableFrom(mainAccumulatePattern.getFirstVariable().getType());
        Variable<InTuple> tupleVariable = isRegrouping ? mainAccumulatePattern.getFirstVariable()
                : Util.createVariable(ruleAssembler.generateNextId("tuple"));
        if (!isRegrouping) {
            bindTupleVariableOnFirstGrouping(ruleAssembler, mainAccumulatePattern, tupleVariable);
        }
        ViewItem<?> innerAccumulatePattern = getInnerAccumulatePattern(ruleAssembler);
        Variable<NewA> outputVariable = Util.createVariable(ruleAssembler.generateNextId("collected"));
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
        ruleAssembler.applyFilterToLastPrimaryPattern();
        ViewItem<?> innerAccumulatePattern = getInnerAccumulatePattern(ruleAssembler);
        Variable<Collection<InTuple>> tupleCollection =
                (Variable<Collection<InTuple>>) Util.createVariable(Collection.class,
                        ruleAssembler.generateNextId("tupleCollection"));
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
        return new UniRuleAssembler(ruleAssembler::generateNextId, ruleAssembler.getExpectedGroupByCount(),
                newFinishedExpressions, singletonList(newA), singletonList(newPrimaryPattern), emptyMap());
    }

    public <NewA> AbstractRuleAssembler regroup(AbstractRuleAssembler ruleAssembler, Variable<Collection<NewA>> newASource,
            ViewItem collectPattern, ViewItem accumulatePattern) {
        ruleAssembler.applyFilterToLastPrimaryPattern();
        List<ViewItem> newFinishedExpressions = new ArrayList<>(ruleAssembler.getFinishedExpressions());
        newFinishedExpressions.add(accumulatePattern);
        newFinishedExpressions.add(collectPattern);
        Variable<NewA> newA =
                (Variable<NewA>) Util.createVariable(ruleAssembler.generateNextId("uniGrouped"), DSL.from(newASource));
        PatternDef<NewA> newPrimaryPattern = pattern(newA);
        return new UniRuleAssembler(ruleAssembler::generateNextId, ruleAssembler.getExpectedGroupByCount(),
                newFinishedExpressions, singletonList(newA), singletonList(newPrimaryPattern), emptyMap());
    }

    public <NewA, NewB> AbstractRuleAssembler regroupBi(AbstractRuleAssembler ruleAssembler,
            Variable<Collection<BiTuple<NewA, NewB>>> newSource, ViewItem collectPattern, ViewItem accumulatePattern) {
        ruleAssembler.applyFilterToLastPrimaryPattern();
        Variable<BiTuple<NewA, NewB>> newTuple =
                (Variable<BiTuple<NewA, NewB>>) Util.createVariable(BiTuple.class,
                        ruleAssembler.generateNextId("biGrouped"), from(newSource));
        List<ViewItem> newFinishedExpressions = new ArrayList<>(ruleAssembler.getFinishedExpressions());
        newFinishedExpressions.add(accumulatePattern);
        newFinishedExpressions.add(collectPattern);
        Variable<NewA> newA = Util.createVariable(ruleAssembler.generateNextId("newA"));
        Variable<NewB> newB = Util.createVariable(ruleAssembler.generateNextId("newB"));
        List<Variable> newVariables = Arrays.asList(newA, newB);
        PatternDef<BiTuple<NewA, NewB>> newPrimaryPattern = pattern(newTuple)
                .bind(newTuple, tuple -> tuple)
                .bind(newA, tuple -> tuple.a)
                .bind(newB, tuple -> tuple.b);
        return new BiRuleAssembler(ruleAssembler::generateNextId, ruleAssembler.getExpectedGroupByCount(),
                newFinishedExpressions, newVariables, singletonList(newPrimaryPattern), emptyMap());
    }

    public <NewA, NewB, NewC> AbstractRuleAssembler regroupBiToTri(AbstractRuleAssembler ruleAssembler,
            Variable<Set<TriTuple<NewA, NewB, NewC>>> newSource, ViewItem collectPattern,
            ViewItem accumulatePattern) {
        ruleAssembler.applyFilterToLastPrimaryPattern();
        List<ViewItem> newFinishedExpressions = new ArrayList<>(ruleAssembler.getFinishedExpressions());
        newFinishedExpressions.add(accumulatePattern);
        newFinishedExpressions.add(collectPattern);
        Variable<NewA> newA = Util.createVariable(ruleAssembler.generateNextId("newA"));
        Variable<NewB> newB = Util.createVariable(ruleAssembler.generateNextId("newB"));
        Variable<NewC> newC = Util.createVariable(ruleAssembler.generateNextId("newC"));
        List<Variable> newVariables = Arrays.asList(newA, newB, newC);
        Variable<TriTuple<NewA, NewB, NewC>> newTuple =
                (Variable<TriTuple<NewA, NewB, NewC>>) Util.createVariable(TriTuple.class,
                        ruleAssembler.generateNextId("triGrouped"), from(newSource));
        PatternDef<TriTuple<NewA, NewB, NewC>> newPrimaryPattern = pattern(newTuple)
                .bind(newTuple, tuple -> tuple)
                .bind(newA, tuple -> tuple.a)
                .bind(newB, tuple -> tuple.b)
                .bind(newC, tuple -> tuple.c);
        return new TriRuleAssembler(ruleAssembler::generateNextId, ruleAssembler.getExpectedGroupByCount(),
                newFinishedExpressions, newVariables, singletonList(newPrimaryPattern), emptyMap());
    }

    public <NewA, NewB, NewC, NewD> AbstractRuleAssembler regroupBiToQuad(AbstractRuleAssembler ruleAssembler,
            Variable<Set<QuadTuple<NewA, NewB, NewC, NewD>>> newSource, ViewItem collectPattern,
            ViewItem accumulatePattern) {
        ruleAssembler.applyFilterToLastPrimaryPattern();
        List<ViewItem> newFinishedExpressions = new ArrayList<>(ruleAssembler.getFinishedExpressions());
        newFinishedExpressions.add(accumulatePattern);
        newFinishedExpressions.add(collectPattern);
        Variable<NewA> newA = Util.createVariable(ruleAssembler.generateNextId("newA"));
        Variable<NewB> newB = Util.createVariable(ruleAssembler.generateNextId("newB"));
        Variable<NewC> newC = Util.createVariable(ruleAssembler.generateNextId("newC"));
        Variable<NewD> newD = Util.createVariable(ruleAssembler.generateNextId("newD"));
        List<Variable> newVariables = Arrays.asList(newA, newB, newC, newD);
        Variable<QuadTuple<NewA, NewB, NewC, NewD>> newTuple =
                (Variable<QuadTuple<NewA, NewB, NewC, NewD>>) Util.createVariable(QuadTuple.class,
                        ruleAssembler.generateNextId("quadGrouped"), from(newSource));
        PatternDef<QuadTuple<NewA, NewB, NewC, NewD>> newPrimaryPattern = pattern(newTuple)
                .bind(newTuple, tuple -> tuple)
                .bind(newA, tuple -> tuple.a)
                .bind(newB, tuple -> tuple.b)
                .bind(newC, tuple -> tuple.c)
                .bind(newD, tuple -> tuple.d);
        return new QuadRuleAssembler(ruleAssembler::generateNextId, ruleAssembler.getExpectedGroupByCount(),
                newFinishedExpressions, newVariables, singletonList(newPrimaryPattern), emptyMap());
    }

    protected UniRuleAssembler downgrade(BiRuleAssembler ruleAssembler) {
        // Downgrade the bi-stream to a uni-stream by ignoring the dummy no-op collector variable.
        List<Variable> allVariablesButLast = ruleAssembler.getVariables()
                .subList(0, ruleAssembler.getVariables().size() - 1);
        return new UniRuleAssembler(ruleAssembler::generateNextId, ruleAssembler.getExpectedGroupByCount(),
                ruleAssembler.getFinishedExpressions(), allVariablesButLast, ruleAssembler.getPrimaryPatterns(),
                emptyMap());
    }

    protected BiRuleAssembler downgrade(TriRuleAssembler ruleAssembler) {
        // Downgrade the tri-stream to a bi-stream by ignoring the dummy no-op collector variable.
        List<Variable> allVariablesButLast = ruleAssembler.getVariables()
                .subList(0, ruleAssembler.getVariables().size() - 1);
        return new BiRuleAssembler(ruleAssembler::generateNextId, ruleAssembler.getExpectedGroupByCount(),
                ruleAssembler.getFinishedExpressions(), allVariablesButLast, ruleAssembler.getPrimaryPatterns(),
                emptyMap());
    }

    @FunctionalInterface
    protected interface Transformer<InTuple> extends
            TriFunction<Variable<Collection<InTuple>>, PatternDef<Collection<InTuple>>, ViewItem<?>, AbstractRuleAssembler> {

    }
}

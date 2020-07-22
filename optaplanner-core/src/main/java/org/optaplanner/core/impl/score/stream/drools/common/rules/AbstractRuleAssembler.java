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

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static org.drools.model.DSL.declarationOf;
import static org.drools.model.PatternDSL.pattern;
import static org.drools.model.PatternDSL.rule;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.drools.model.Argument;
import org.drools.model.Drools;
import org.drools.model.Global;
import org.drools.model.PatternDSL.PatternDef;
import org.drools.model.Rule;
import org.drools.model.RuleItemBuilder;
import org.drools.model.Variable;
import org.drools.model.consequences.ConsequenceBuilder;
import org.drools.model.view.ViewItem;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.score.holder.AbstractScoreHolder;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraint;
import org.optaplanner.core.impl.score.stream.drools.common.FactTuple;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.AbstractConstraintModelGroupingNode;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.AbstractConstraintModelJoiningNode;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.ConstraintGraphNode;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.ConstraintGraphNodeType;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.FromNode;

abstract class AbstractRuleAssembler<Predicate_> implements RuleAssembler {

    private final UnaryOperator<String> idSupplier;
    private final int expectedGroupByCount;
    private final List<Variable> variables;
    private final List<ViewItem> finishedExpressions;
    private final List<PatternDef> primaryPatterns;
    private final Map<Integer, List<ViewItem>> dependentExpressionMap;

    protected AbstractRuleAssembler(ConstraintGraphNode fromNode, int expectedGroupByCount) {
        this(prefix -> prefix + ((FromNode) fromNode).getGraph().getNextId(), expectedGroupByCount, emptyList(),
                emptyList(), emptyList(), emptyMap());
        variables.add(declarationOf(((FromNode) fromNode).getFactType(), generateNextId("var")));
        primaryPatterns.add(pattern(variables.get(0)));
    }

    protected AbstractRuleAssembler(UnaryOperator<String> idSupplier, int expectedGroupByCount,
            List<ViewItem> finishedExpressions, List<Variable> variables, List<PatternDef> primaryPatterns,
            Map<Integer, List<ViewItem>> dependentExpressionMap) {
        this.idSupplier = idSupplier;
        this.expectedGroupByCount = expectedGroupByCount;
        this.finishedExpressions = new ArrayList<>(finishedExpressions);
        this.variables = new ArrayList<>(variables);
        this.primaryPatterns = new ArrayList<>(primaryPatterns);
        this.dependentExpressionMap = new HashMap<>(dependentExpressionMap);
    }

    protected static void impactScore(Drools drools, AbstractScoreHolder scoreHolder) {
        RuleContext kcontext = (RuleContext) drools;
        scoreHolder.impactScore(kcontext);
    }

    protected static void impactScore(DroolsConstraint constraint, Drools drools, AbstractScoreHolder scoreHolder,
            int impact) {
        RuleContext kcontext = (RuleContext) drools;
        constraint.assertCorrectImpact(impact);
        scoreHolder.impactScore(kcontext, impact);
    }

    protected static void impactScore(DroolsConstraint constraint, Drools drools, AbstractScoreHolder scoreHolder,
            long impact) {
        RuleContext kcontext = (RuleContext) drools;
        constraint.assertCorrectImpact(impact);
        scoreHolder.impactScore(kcontext, impact);
    }

    protected static void impactScore(DroolsConstraint constraint, Drools drools, AbstractScoreHolder scoreHolder,
            BigDecimal impact) {
        RuleContext kcontext = (RuleContext) drools;
        constraint.assertCorrectImpact(impact);
        scoreHolder.impactScore(kcontext, impact);
    }

    String generateNextId(String prefix) {
        return idSupplier.apply(prefix);
    }

    int getExpectedGroupByCount() {
        return expectedGroupByCount;
    }

    List<Variable> getVariables() {
        return unmodifiableList(variables);
    }

    Variable getVariable(int index) {
        return variables.get(index);
    }

    List<PatternDef> getPrimaryPatterns() {
        return unmodifiableList(primaryPatterns);
    }

    PatternDef getLastPrimaryPattern() {
        return primaryPatterns.get(primaryPatterns.size() - 1);
    }

    protected abstract void addFilterToLastPrimaryPattern(Predicate_ predicate);

    protected abstract void applyFilterToLastPrimaryPattern();

    Map<Integer, List<ViewItem>> getDependentExpressionMap() {
        return unmodifiableMap(dependentExpressionMap);
    }

    void addDependentExpressionToLastPattern(ViewItem expression) {
        int lastPatternId = primaryPatterns.size() - 1;
        dependentExpressionMap
                .computeIfAbsent(lastPatternId, key -> new ArrayList<>(1))
                .add(expression);
    }

    List<ViewItem> getFinishedExpressions() {
        return unmodifiableList(finishedExpressions);
    }

    @Override
    public final AbstractRuleAssembler andThen(ConstraintGraphNode node) {
        switch (node.getType()) {
            case FILTER:
                return andThenFilter(node);
            case IF_EXISTS:
            case IF_NOT_EXISTS:
                AbstractConstraintModelJoiningNode joiningNode = (AbstractConstraintModelJoiningNode) node;
                boolean shouldExist = joiningNode.getType() == ConstraintGraphNodeType.IF_EXISTS;
                return andThenExists(joiningNode, shouldExist);
            case GROUPBY_MAPPING_ONLY:
            case GROUPBY_COLLECTING_ONLY:
            case GROUPBY_MAPPING_AND_COLLECTING:
                AbstractConstraintModelGroupingNode groupingNode = (AbstractConstraintModelGroupingNode) node;
                return andThenGroupBy(groupingNode);
            default:
                throw new UnsupportedOperationException(node.getType().toString());
        }
    }

    @Override
    public final RuleAssembler join(RuleAssembler ruleAssembler, ConstraintGraphNode joinNode) {
        if (!(ruleAssembler instanceof UniRuleAssembler)) {
            throw new IllegalStateException("Impossible state: Rule assembler (" + ruleAssembler + ") not instance of "
                    + UniRuleAssembler.class + ".");
        }
        return join((UniRuleAssembler) ruleAssembler, joinNode);
    }

    protected abstract AbstractRuleAssembler join(UniRuleAssembler ruleAssembler, ConstraintGraphNode joinNode);

    protected final AbstractRuleAssembler andThenFilter(ConstraintGraphNode filterNode) {
        Predicate_ predicate = ((Supplier<Predicate_>) filterNode).get();
        addFilterToLastPrimaryPattern(predicate);
        return this;
    }

    protected abstract AbstractRuleAssembler andThenExists(AbstractConstraintModelJoiningNode joiningNode,
            boolean shouldExist);

    protected final AbstractRuleAssembler andThenGroupBy(AbstractConstraintModelGroupingNode groupingNode) {
        List<Function> mappings = groupingNode.getMappings();
        int mappingCount = mappings.size();
        List<UniConstraintCollector> collectors = groupingNode.getCollectors();
        int collectorCount = collectors.size();
        switch (groupingNode.getType()) {
            case GROUPBY_MAPPING_ONLY:
                switch (mappingCount) {
                    case 1:
                        return new1Map0CollectGroupByMutator(mappings.get(0))
                                .apply(this);
                    case 2:
                        return new2Map0CollectGroupByMutator(mappings.get(0), mappings.get(1))
                                .apply(this);
                    default:
                        throw new UnsupportedOperationException("Impossible state: Mapping count (" + mappingCount + ").");
                }
            case GROUPBY_COLLECTING_ONLY:
                if (collectorCount == 1) {
                    return new0Map1CollectGroupByMutator(collectors.get(0))
                            .apply(this);
                }
                throw new UnsupportedOperationException("Impossible state: Collector count (" + collectorCount + ").");
            case GROUPBY_MAPPING_AND_COLLECTING:
                if (mappingCount == 1 && collectorCount == 1) {
                    return new1Map1CollectGroupByMutator(mappings.get(0), collectors.get(0))
                            .apply(this);
                } else if (mappingCount == 2 && collectorCount == 1) {
                    return new2Map1CollectGroupByMutator(mappings.get(0), mappings.get(1), collectors.get(0))
                            .apply(this);
                } else if (mappingCount == 2 && collectorCount == 2) {
                    return new2Map2CollectGroupByMutator(mappings.get(0), mappings.get(1), collectors.get(0),
                            collectors.get(1)).apply(this);
                } else {
                    throw new UnsupportedOperationException("Impossible state: Mapping count (" + mappingCount + "), " +
                            "collector count (" + collectorCount + ").");
                }
            default:
                throw new UnsupportedOperationException(groupingNode.getType().toString());
        }
    }

    protected abstract AbstractGroupByMutator new0Map1CollectGroupByMutator(Object collector);

    protected abstract AbstractGroupByMutator new1Map0CollectGroupByMutator(Object mapping);

    protected abstract AbstractGroupByMutator new1Map1CollectGroupByMutator(Object mapping, Object collector);

    protected abstract AbstractGroupByMutator new2Map0CollectGroupByMutator(Object mappingA, Object mappingB);

    protected abstract AbstractGroupByMutator new2Map1CollectGroupByMutator(Object mappingA, Object mappingB,
            Object collectorC);

    protected abstract AbstractGroupByMutator new2Map2CollectGroupByMutator(Object mappingA, Object mappingB,
            Object collectorC, Object collectorD);

    protected abstract ConsequenceBuilder.ValidBuilder buildConsequence(DroolsConstraint constraint,
            Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal, Variable... variables);

    public RuleAssembly assemble(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal, DroolsConstraint constraint) {
        applyFilterToLastPrimaryPattern();
        List<RuleItemBuilder> ruleItemBuilderList = new ArrayList<>(0);
        ruleItemBuilderList.addAll(finishedExpressions);
        for (int i = 0; i < primaryPatterns.size(); i++) {
            ruleItemBuilderList.add(primaryPatterns.get(i));
            ruleItemBuilderList.addAll(dependentExpressionMap.getOrDefault(i, emptyList()));
        }
        ConsequenceBuilder.ValidBuilder consequence = buildConsequence(constraint, scoreHolderGlobal,
                variables.toArray(new Variable[0]));
        ruleItemBuilderList.add(consequence);
        Rule rule = rule(constraint.getConstraintPackage(), constraint.getConstraintName())
                .build(ruleItemBuilderList.toArray(new RuleItemBuilder[0]));
        return new RuleAssembly(rule, getExpectedJustificationTypes().toArray(Class[]::new));
    }

    private Stream<Class> getExpectedJustificationTypes() {
        PatternDef pattern = primaryPatterns.get(primaryPatterns.size() - 1);
        Class type = pattern.getFirstVariable().getType();
        if (FactTuple.class.isAssignableFrom(type)) {
            // There is one expected constraint justification, and that is of the tuple type.
            return Stream.of(type);
        }
        // There are plenty expected constraint justifications, one for each variable.
        return variables.stream()
                .map(Argument::getType);
    }

}

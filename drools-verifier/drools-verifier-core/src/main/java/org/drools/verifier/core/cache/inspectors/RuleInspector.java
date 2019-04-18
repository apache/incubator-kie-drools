/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.verifier.core.cache.inspectors;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.drools.verifier.api.reporting.model.Bound;
import org.drools.verifier.api.reporting.model.Interval;
import org.drools.verifier.core.cache.RuleInspectorCache;
import org.drools.verifier.core.cache.inspectors.action.ActionInspector;
import org.drools.verifier.core.cache.inspectors.action.ActionsInspectorMultiMap;
import org.drools.verifier.core.cache.inspectors.action.BRLActionInspector;
import org.drools.verifier.core.cache.inspectors.condition.BRLConditionInspector;
import org.drools.verifier.core.cache.inspectors.condition.ComparableConditionInspector;
import org.drools.verifier.core.cache.inspectors.condition.ConditionInspector;
import org.drools.verifier.core.cache.inspectors.condition.ConditionsInspectorMultiMap;
import org.drools.verifier.core.checks.base.Check;
import org.drools.verifier.core.checks.base.CheckStorage;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.model.Action;
import org.drools.verifier.core.index.model.BRLAction;
import org.drools.verifier.core.index.model.BRLCondition;
import org.drools.verifier.core.index.model.Condition;
import org.drools.verifier.core.index.model.meta.ConditionMaster;
import org.drools.verifier.core.index.model.meta.ConditionParentType;
import org.drools.verifier.core.maps.InspectorList;
import org.drools.verifier.core.maps.util.HasKeys;
import org.drools.verifier.core.relations.HumanReadable;
import org.drools.verifier.core.relations.IsConflicting;
import org.drools.verifier.core.relations.IsOverlapping;
import org.drools.verifier.core.relations.IsRedundant;
import org.drools.verifier.core.relations.IsSubsuming;
import org.drools.verifier.core.util.PortablePreconditions;

import static org.drools.verifier.core.util.IntervalUtil.areIntervalsOverlapping;

public class RuleInspector
        implements IsRedundant,
                   IsSubsuming,
                   IsConflicting,
                   IsOverlapping,
                   HumanReadable,
                   HasKeys {

    protected InspectedRule inspectedRule;
    protected final CheckStorage checkStorage;
    protected final RuleInspectorCache cache;
    protected final AnalyzerConfiguration configuration;

    protected final UUIDKey uuidKey;

    protected final InspectorList<ConditionMasterInspector> conditionMasterInspectorList;
    protected final InspectorList<ConditionInspector> brlConditionsInspectors;
    protected final InspectorList<ActionInspector> brlActionInspectors;
    protected InspectorList<ActionsInspectorMultiMap> actionsInspectors = null;
    protected InspectorList<ConditionsInspectorMultiMap> conditionsInspectors = null;

    public RuleInspector(final InspectedRule inspectedRule,
                         final CheckStorage checkStorage,
                         final RuleInspectorCache cache,
                         final AnalyzerConfiguration configuration) {
        this.inspectedRule = PortablePreconditions.checkNotNull("inspectedRule",
                                                                inspectedRule);
        this.checkStorage = PortablePreconditions.checkNotNull("checkStorage",
                                                               checkStorage);
        this.cache = PortablePreconditions.checkNotNull("cache",
                                                        cache);
        this.configuration = PortablePreconditions.checkNotNull("configuration",
                                                                configuration);

        uuidKey = configuration.getUUID(this);
        conditionMasterInspectorList = new InspectorList<>(configuration);
        brlConditionsInspectors = new InspectorList<>(true,
                                                      configuration);
        brlActionInspectors = new InspectorList<>(true,
                                                  configuration);

        makePatternsInspectors();
        makeBRLActionInspectors();
        makeBRLConditionInspectors();

        makeChecks();
    }

    private void makeConditionsInspectors() {
        conditionsInspectors = new InspectorList<>(true,
                                                   configuration);

        for (final ConditionMasterInspector conditionMasterInspector : conditionMasterInspectorList) {
            conditionsInspectors.add(conditionMasterInspector.getConditionsInspector());
        }
    }

    private void makeActionsInspectors() {
        actionsInspectors = new InspectorList<>(true,
                                                configuration);

        for (final ConditionMasterInspector conditionMasterInspector : conditionMasterInspectorList) {
            actionsInspectors.add(conditionMasterInspector.getActionsInspector());
        }
    }

    private void makeBRLConditionInspectors() {
        updateBRLConditionInspectors(inspectedRule.getBRLConditions());
    }

    private void makeBRLActionInspectors() {
        updateBRLActionInspectors(inspectedRule.getBRLActions());
    }

    protected void updateBRLConditionInspectors(final Collection<Condition> conditions) {
        this.brlConditionsInspectors.clear();
        for (final Condition condition : conditions) {
            this.brlConditionsInspectors.add(new BRLConditionInspector((BRLCondition) condition,
                                                                       configuration));
        }
    }

    protected void updateBRLActionInspectors(final Collection<Action> actions) {
        this.brlActionInspectors.clear();
        for (final Action action : actions) {
            this.brlActionInspectors.add(new BRLActionInspector((BRLAction) action,
                                                                configuration));
        }
    }

    private void makePatternsInspectors() {
        for (final ConditionMaster pattern : inspectedRule.getConditionMasters()) {
            final ConditionMasterInspector conditionMasterInspector = new ConditionMasterInspector(pattern,
                                                                                                   new RuleInspectorUpdater() {

                                                                                                       @Override
                                                                                                       public void resetActionsInspectors() {
                                                                                                           actionsInspectors = null;
                                                                                                       }

                                                                                                       @Override
                                                                                                       public void resetConditionsInspectors() {
                                                                                                           conditionsInspectors = null;
                                                                                                       }
                                                                                                   },
                                                                                                   configuration);

            conditionMasterInspectorList.add(conditionMasterInspector);
        }
    }

    public InspectorList<ConditionsInspectorMultiMap> getConditionsInspectors() {
        if (conditionsInspectors == null) {
            makeConditionsInspectors();
        }
        return conditionsInspectors;
    }

    public InspectorList<ActionsInspectorMultiMap> getActionsInspectors() {
        if (actionsInspectors == null) {
            makeActionsInspectors();
        }
        return actionsInspectors;
    }

    public InspectorList<ConditionMasterInspector> getPatternsInspector() {
        return conditionMasterInspectorList;
    }

    public int getRowIndex() {
        return inspectedRule.getRowNumber();
    }

    public RuleInspectorCache getCache() {
        return cache;
    }

    @Override
    public boolean isRedundant(final Object other) {
        return other instanceof RuleInspector
                && inspectedRule.getActivationTime().overlaps(((RuleInspector) other).inspectedRule.getActivationTime())
                && brlConditionsInspectors.isRedundant(((RuleInspector) other).brlConditionsInspectors)
                && brlActionInspectors.isRedundant(((RuleInspector) other).brlActionInspectors)
                && getActionsInspectors().isRedundant(((RuleInspector) other).getActionsInspectors())
                && getConditionsInspectors().isRedundant(((RuleInspector) other).getConditionsInspectors());
    }

    @Override
    public boolean subsumes(final Object other) {
        return other instanceof RuleInspector
                && inspectedRule.getActivationTime().overlaps(((RuleInspector) other).inspectedRule.getActivationTime())
                && brlActionInspectors.subsumes(((RuleInspector) other).brlActionInspectors)
                && brlConditionsInspectors.subsumes(((RuleInspector) other).brlConditionsInspectors)
                && getActionsInspectors().subsumes(((RuleInspector) other).getActionsInspectors())
                && getConditionsInspectors().subsumes(((RuleInspector) other).getConditionsInspectors());
    }

    @Override
    public boolean overlaps(Object other) {
        if (other instanceof RuleInspector && inspectedRule.getActivationTime().overlaps(((RuleInspector) other).inspectedRule.getActivationTime())) {
            return getConditionsInspectors().overlaps(((RuleInspector) other).getConditionsInspectors())
                    && getBrlConditionsInspectors().overlaps(((RuleInspector) other).getBrlConditionsInspectors());
        }
        return false;
    }

    @Override
    public boolean conflicts(final Object other) {
        if (other instanceof RuleInspector && inspectedRule.getActivationTime().overlaps(((RuleInspector) other).inspectedRule.getActivationTime())) {

            if (getActionsInspectors().conflicts(((RuleInspector) other).getActionsInspectors())) {
                boolean subsumes = getConditionsInspectors().subsumes(((RuleInspector) other).getConditionsInspectors());
                if (subsumes
                        && getBrlConditionsInspectors().subsumes(((RuleInspector) other).getBrlConditionsInspectors())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return !atLeastOneConditionHasAValue() && !atLeastOneActionHasAValue();
    }

    public boolean atLeastOneActionHasAValue() {
        final int amountOfActions = inspectedRule.getAllActions().size();
        return amountOfActions > 0;
    }

    public boolean atLeastOneConditionHasAValue() {
        final int amountOfConditions = inspectedRule.getAllConditions().size();
        return amountOfConditions > 0;
    }

    @Override
    public String toHumanReadableString() {
        return inspectedRule.toHumanReadableString();
    }

    public InspectorList<ConditionInspector> getBrlConditionsInspectors() {
        return brlConditionsInspectors;
    }

    public InspectorList<ActionInspector> getBrlActionInspectors() {
        return brlActionInspectors;
    }

    @Override
    public UUIDKey getUuidKey() {
        return uuidKey;
    }

    @Override
    public Key[] keys() {
        return new Key[]{
                uuidKey
        };
    }

    public Set<Check> getChecks() {
        return checkStorage.getChecks(this);
    }

    private void makeChecks() {
        checkStorage.makeChecks(this);
    }

    public Set<Check> clearChecks() {
        return checkStorage.remove(this);
    }

    public Map<ConditionParentType, Interval> getIntervals() {
        final Map<ConditionParentType, Interval> intervals = new HashMap<>();

        final List<Object> collect = getConditionsInspectors()
                .stream().flatMap(x -> x.allValues().stream())
                .collect(Collectors.toList());

        for (Object o : collect) {
            if (o instanceof ComparableConditionInspector) {
                final ComparableConditionInspector conditionInspector = (ComparableConditionInspector) o;

                if (intervals.containsKey(conditionInspector.getField().getConditionParentType())) {

                    final Interval first = intervals.get(conditionInspector.getField().getConditionParentType());
                    final Interval second = conditionInspector.getInterval();
                    final Bound firstLowerBound = first.getLowerBound();
                    final Bound secondUpperBound = second.getUpperBound();

                    if (areIntervalsOverlapping(first, second)) {
                        final Interval interval = Interval.newFromBounds(firstLowerBound,
                                                                         secondUpperBound);

                        interval.getOriginColumns().add(conditionInspector.getCondition().getColumn().getIndex());
                        intervals.put(conditionInspector.getField().getConditionParentType(),
                                      interval);
                    }
                } else {
                    final Interval interval = conditionInspector.getInterval();

                    interval.getOriginColumns().add(conditionInspector.getCondition().getColumn().getIndex());

                    intervals.put(conditionInspector.getField().getConditionParentType(),
                                  interval);
                }
            }
        }

        return intervals;
    }

    public InspectedRule getInspectedRule() {
        return inspectedRule;
    }
}

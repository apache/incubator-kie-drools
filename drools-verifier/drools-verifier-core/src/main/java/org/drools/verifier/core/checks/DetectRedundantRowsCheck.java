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
package org.drools.verifier.core.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.OverlappingIssue;
import org.drools.verifier.api.reporting.Severity;
import org.drools.verifier.api.reporting.model.Bound;
import org.drools.verifier.api.reporting.model.Interval;
import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.drools.verifier.core.cache.inspectors.RuleInspectorDumper;
import org.drools.verifier.core.checks.base.PairCheck;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.configuration.CheckConfiguration;
import org.drools.verifier.core.index.model.Action;
import org.drools.verifier.core.index.model.Column;
import org.drools.verifier.core.index.model.ColumnType;
import org.drools.verifier.core.index.model.Condition;
import org.drools.verifier.core.index.model.meta.ConditionParentType;

import static org.drools.verifier.core.util.IntervalUtil.areIntervalsOverlapping;

public class DetectRedundantRowsCheck
        extends PairCheck {

    private CheckType issueType = null;

    private boolean allowRedundancyReporting = true;
    private boolean allowSubsumptionReporting = true;
    private boolean allowOverlapReporting = false;

    public DetectRedundantRowsCheck(final RuleInspector ruleInspector,
                                    final RuleInspector other,
                                    final AnalyzerConfiguration configuration) {
        super(ruleInspector,
              other,
              configuration);
    }

    @Override
    protected List<Issue> makeIssues(final Severity severity,
                                     final CheckType checkType) {
        if (issueType == CheckType.OVERLAPPING_ROWS) {
            final ArrayList<Issue> result = new ArrayList<Issue>();
            result.add(makeOverlapIssue(severity,
                                        checkType));
            return result;
        } else {
            return Arrays.asList(new Issue(severity,
                                           checkType,
                                           new HashSet<>(Arrays.asList(ruleInspector.getRowIndex() + 1,
                                                                       other.getRowIndex() + 1))));
        }
    }

    @Override
    public boolean isActive(final CheckConfiguration checkConfiguration) {

        allowRedundancyReporting = checkConfiguration.getCheckConfiguration()
                .contains(CheckType.REDUNDANT_ROWS);

        allowSubsumptionReporting = checkConfiguration.getCheckConfiguration()
                .contains(CheckType.SUBSUMPTANT_ROWS);

        allowOverlapReporting = checkConfiguration.getCheckConfiguration()
                .contains(CheckType.OVERLAPPING_ROWS);

        return allowRedundancyReporting || allowSubsumptionReporting || allowOverlapReporting;
    }

    @Override
    protected CheckType getCheckType() {
        return issueType;
    }

    @Override
    protected Severity getDefaultSeverity() {
        return Severity.WARNING;
    }

    @Override
    public boolean check() {









        if (ruleInspector.getRowIndex() > other.getRowIndex()) {
            return hasIssues;
        }













        boolean b = other.atLeastOneActionHasAValue();
        if (b && ruleInspector.subsumes(other)) {
            if (allowRedundancyReporting && other.subsumes(ruleInspector)) {
                issueType = CheckType.REDUNDANT_ROWS;
                return hasIssues = true;
            } else if (allowSubsumptionReporting) {
                issueType = CheckType.SUBSUMPTANT_ROWS;
                return hasIssues = true;
            }
        }

        // TODO only check redundancy one way
        // Only check overlaps if the above are not an issue.
        if (allowOverlapReporting && !hasIssues) {
            if (ruleInspector.getRowIndex() > other.getRowIndex()) {
                return hasIssues;
            }
            issueType = CheckType.SUBSUMPTANT_ROWS;
            hasIssues = ruleInspector.overlaps(other);
            return hasIssues;
        }

        return hasIssues = false;
    }

    private OverlappingIssue makeOverlapIssue(Severity severity, CheckType checkType) {
        final List<Interval> intervals = getIntervals();

        final boolean containsAnyValueField = getContainsAnyValueCell();
        final OverlappingIssue issue = new OverlappingIssue(severity,
                                                            checkType,
                                                            intervals,
                                                            containsAnyValueField,
                                                            getRHSValues(),
                                                            new HashSet<>(Arrays.asList(ruleInspector.getRowIndex() + 1,
                                                                                        other.getRowIndex() + 1))
        );
        issue.setDebugMessage(new RuleInspectorDumper(ruleInspector).dump() + " ## " + new RuleInspectorDumper(other).dump());
        return issue;
    }

    private List<Interval> getIntervals() {

        final Map<ConditionParentType, Interval> mapByCondition = new HashMap<>();

        final Map<ConditionParentType, Interval> intervalsOther = other.getIntervals();
        final Map<ConditionParentType, Interval> intervals = ruleInspector.getIntervals();

        for (ConditionParentType key : intervalsOther.keySet()) {
            if (intervals.containsKey(key)) {
                final Interval other = intervalsOther.get(key);
                final Interval interval = intervals.get(key);
                if (areIntervalsOverlapping(other, interval)) {
                    final Bound lowerBound = getHigherBound(other.getLowerBound(),
                                                            interval.getLowerBound());
                    final Bound higherBound = getLowerBound(other.getUpperBound(),
                                                            interval.getUpperBound());
                    final Interval resultInterval = Interval.newFromBounds(
                            lowerBound,
                            higherBound
                    );
                    mapByCondition.put(key,
                                       resultInterval);
                }
            } else {
                mapByCondition.put(key,
                                   intervalsOther.get(key));
            }
        }

        for (ConditionParentType key : intervals.keySet()) {
            if (!intervalsOther.containsKey(key)) {
                mapByCondition.put(key,
                                   intervals.get(key));
            }
        }

        return new ArrayList<Interval>(mapByCondition.values());
    }

    private Bound getHigherBound(final Bound other,
                                 final Bound interval) {
        if (other.compareTo(interval) >= 0) {
            return other;
        } else {
            return interval;
        }
    }

    private Bound getLowerBound(final Bound other,
                                final Bound interval) {
        if (other.compareTo(interval) >= 0) {
            return interval;
        } else {
            return other;
        }
    }

    private boolean getContainsAnyValueCell() {
//        for (final Column column : index.getColumns().where(Column.columnType().is(ColumnType.LHS)).select().all()) {
//            if (!ruleInspector.getRule().getConditions().where(Condition.columnUUID().is(column.getUuidKey())).select().exists()) {
//                return true;
//            }
//        }

        return false;
    }

    private Map<Integer, String> getRHSValues() {
        final Map<Integer, String> result = new HashMap<>();

//        for (final Column column : index.getColumns().where(Column.columnType().is(ColumnType.RHS)).select().all()) {
//            for (final Action action : ruleInspector.getRule().getActions().where(Action.columnUUID().is(column.getUuidKey())).select().all()) {
//                result.put(column.getIndex(), action.getValues().stream().map(Object::toString).collect(Collectors.joining(",")));
//            }
//        }

        return result;
    }
}

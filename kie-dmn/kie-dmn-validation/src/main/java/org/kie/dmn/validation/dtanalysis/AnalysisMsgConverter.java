/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.dmn.validation.dtanalysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.OverlappingIssue;
import org.drools.verifier.api.reporting.Severity;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.lang.ast.DashNode;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.validation.dtanalysis.model.Bound;
import org.kie.dmn.validation.dtanalysis.model.DDTAInputEntry;
import org.kie.dmn.validation.dtanalysis.model.DDTAOutputClause;
import org.kie.dmn.validation.dtanalysis.model.DDTATable;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;
import org.kie.dmn.validation.dtanalysis.model.Interval;
import org.kie.dmn.validation.dtanalysis.model.Subsumption;

public class AnalysisMsgConverter {

    private final DecisionTable dt;
    private final DDTATable ddtaTable;
    private final DTAnalysis analysis;

    private final HashSet<DMNDTAnalysisMessage> result = new HashSet<>();

    public AnalysisMsgConverter(final DecisionTable dt,
                                final DDTATable ddtaTable,
                                final DTAnalysis analysis) {
        this.dt = dt;
        this.ddtaTable = ddtaTable;
        this.analysis = analysis;
    }

    public ConversionMessage convert(final Set<Issue> issues) {

        final ArrayList<Subsumption> subsumptions = new ArrayList<>();

        for (final Issue issue : issues) {
            if (issue.getCheckType().equals(CheckType.OVERLAPPING_ROWS)) {
                overlapAsMessage((OverlappingIssue) issue);
            } else {
                if ((issue.getCheckType() == CheckType.SUBSUMPTANT_ROWS || issue.getCheckType() == CheckType.REDUNDANT_ROWS) && issue.getRowNumbers().size() == 2) {
                    final Iterator<Integer> iterator = issue.getRowNumbers().iterator();

                    subsumptions.add(new Subsumption(iterator.next(), iterator.next()));
                }
                result.add(convert(issue));
            }
        }

        return new ConversionMessage(result,
                                     subsumptions);
    }

    private void overlapAsMessage(final OverlappingIssue issue) {

        if (issue.getRowNumbers().size() < 2) {
            throw new IllegalArgumentException("There should be at least two row numbers");
        }

        switch (dt.getHitPolicy()) {
            case UNIQUE:
                overlapAsUniqueMessage(issue);
                break;
            case ANY:
                overlapsMessageAny(issue);
                break;
            case PRIORITY:

                if (isMasked(issue)) {
                    result.add(new DMNDTAnalysisMessage(analysis,
                                                        DMNMessage.Severity.ERROR,
                                                        MsgUtil.createMessage(Msg.DTANALYSIS_HITPOLICY_PRIORITY_MASKED_RULE,
                                                                              getHighest(issue.getRowNumbers()),
                                                                              getLowest(issue.getRowNumbers())),
                                                        Msg.DTANALYSIS_HITPOLICY_PRIORITY_MASKED_RULE.getType(), Collections.singletonList((Integer) issue.getRowNumbers().toArray()[0])));
                }
                if (isMisleading(issue)) {
                    result.add(new DMNDTAnalysisMessage(analysis,
                                                        DMNMessage.Severity.WARN,
                                                        MsgUtil.createMessage(Msg.DTANALYSIS_HITPOLICY_PRIORITY_MISLEADING_RULE,
                                                                              getHighest(issue.getRowNumbers()),
                                                                              getLowest(issue.getRowNumbers())),
                                                        Msg.DTANALYSIS_HITPOLICY_PRIORITY_MISLEADING_RULE.getType(), Collections.singletonList((Integer) issue.getRowNumbers().toArray()[1])));
                }

                overlapToStandardDMNMessage(issue);
                break;
            default:
                overlapToStandardDMNMessage(issue);
                break;
        }
    }

    private List<Interval> convert(final OverlappingIssue issue) {
        final ArrayList<Interval> result = new ArrayList<>();

        for (org.drools.verifier.api.reporting.model.Interval interval : issue.getIntervals()) {
            result.add(Interval.newFromBounds(convert(interval.getLowerBound()),
                                              convert(interval.getUpperBound())));
        }

        return result;
    }

    private Bound convert(org.drools.verifier.api.reporting.model.Bound verifierBound) {
        return new Bound(verifierBound.getValue(),
                         convert(verifierBound.getBoundaryType()),
                         null);
    }

    private Range.RangeBoundary convert(org.drools.verifier.api.reporting.model.Range.RangeBoundary boundaryType) {
        switch (boundaryType) {
            case OPEN:
                return Range.RangeBoundary.OPEN;
            case CLOSED:
            default:
                return Range.RangeBoundary.CLOSED;
        }
    }

    private boolean isMasked(final OverlappingIssue issue) {

        final Integer firstRuleId = getLowest(issue.getRowNumbers());
        final Integer secondRuleID = getHighest(issue.getRowNumbers());

        if (isMasked(firstRuleId, secondRuleID)) {
            return true;
        } else {
            return isMasked(secondRuleID, firstRuleId);
        }
    }

    private boolean isMasked(final Integer ruleId,
                             final Integer otherRuleID) {
        List<Comparable<?>> curValues = ddtaTable.getRule().get(ruleId - 1).getOutputEntry();

        for (int jOutputIdx = 0; jOutputIdx < ddtaTable.outputCols(); jOutputIdx++) {
            DDTAOutputClause curOutputClause = ddtaTable.getOutputs().get(jOutputIdx);
            if (curOutputClause.isDiscreteDomain()) {
                int curOutputIdx = curOutputClause.getOutputOrder().indexOf(curValues.get(jOutputIdx));

                List<Comparable<?>> otherRuleValues = ddtaTable.getRule().get(otherRuleID - 1).getOutputEntry();
                int otherOutputIdx = curOutputClause.getOutputOrder().indexOf(otherRuleValues.get(jOutputIdx));
                if (curOutputIdx > otherOutputIdx) {
                    try {
                        boolean isOtherRuleWider = comparingRulesIsRightWider(ruleId, otherRuleID);
                        if (isOtherRuleWider) {
                            return true;
                        }
                    } catch (ComparingRulesWithMultipleInputEntries e) {
                        result.add(new DMNDTAnalysisMessage(analysis,
                                                            DMNMessage.Severity.WARN,
                                                            MsgUtil.createMessage(Msg.DTANALYSIS_HITPOLICY_PRIORITY_ANALYSIS_SKIPPED,
                                                                                  dt.getOutputLabel(),
                                                                                  ruleId, otherRuleID),
                                                            Msg.DTANALYSIS_HITPOLICY_PRIORITY_ANALYSIS_SKIPPED.getType(), Collections.singletonList(ruleId)));
                    }
                }
            }
        }
        return false;
    }

    public boolean comparingRulesIsRightWider(final int ruleId,
                                              final int isWiderRuleId) throws ComparingRulesWithMultipleInputEntries {
        boolean isOtherRuleWider = true;
        for (int jInputIdx = 0; jInputIdx < ddtaTable.inputCols(); jInputIdx++) {
            DDTAInputEntry ruleIdInputAtIdx = ddtaTable.getRule().get(ruleId - 1).getInputEntry().get(jInputIdx);
            DDTAInputEntry otherRuleInputAtIdx = ddtaTable.getRule().get(isWiderRuleId - 1).getInputEntry().get(jInputIdx);
            if (ruleIdInputAtIdx.getIntervals().size() != 1 || otherRuleInputAtIdx.getIntervals().size() != 1) {
                throw new ComparingRulesWithMultipleInputEntries("Multiple entries not supported");
            } else {
                Interval ruleIdInterval = ruleIdInputAtIdx.getIntervals().get(0);
                Interval otherRuleInterval = otherRuleInputAtIdx.getIntervals().get(0);
                isOtherRuleWider = otherRuleInterval.includes(ruleIdInterval);
            }
        }
        return isOtherRuleWider;
    }

    public class ComparingRulesWithMultipleInputEntries extends Exception {

        public ComparingRulesWithMultipleInputEntries(String message) {
            super(message);
        }
    }

    private boolean isMisleading(final OverlappingIssue issue) {
        Integer ruleId = getHighest(issue.getRowNumbers());
        List<Comparable<?>> curValues = ddtaTable.getRule().get(ruleId - 1).getOutputEntry();

        for (int jOutputIdx = 0; jOutputIdx < ddtaTable.outputCols(); jOutputIdx++) {
            DDTAOutputClause curOutputClause = ddtaTable.getOutputs().get(jOutputIdx);
            if (curOutputClause.isDiscreteDomain()) {
                int curOutputIdx = curOutputClause.getOutputOrder().indexOf(curValues.get(jOutputIdx));
                boolean isOutputLowestPriority = curOutputIdx == curOutputClause.getOutputOrder().size() - 1;
                if (!isOutputLowestPriority) {
                    List<DDTAInputEntry> inputEntry = ddtaTable.getRule().get(ruleId - 1).getInputEntry();
                    boolean isRuleContainsHypen = inputEntry.stream().flatMap(ie -> ie.getUts().stream()).anyMatch(DashNode.class::isInstance);
                    if (isRuleContainsHypen) {

                        Integer otherRuleID = getLowest(issue.getRowNumbers());
                        List<Comparable<?>> otherRuleValues = ddtaTable.getRule().get(otherRuleID - 1).getOutputEntry();
                        Comparable<?> o = otherRuleValues.get(jOutputIdx);
                        int otherOutputIdx = curOutputClause.getOutputOrder().indexOf(o);
                        if (otherOutputIdx > curOutputIdx) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private Integer getHighest(final Set<Integer> numbers) {
        final Optional<Integer> max = numbers.stream().max(Comparator.comparing(Integer::intValue));
        if (max.isPresent()) {
            return max.get();
        } else {
            throw new IllegalArgumentException("There should be at least two row numbers");
        }
    }

    private Integer getLowest(final Set<Integer> numbers) {
        final Optional<Integer> min = numbers.stream().min(Comparator.comparing(Integer::intValue));
        if (min.isPresent()) {
            return min.get();
        } else {
            throw new IllegalArgumentException("There should be at least two row numbers");
        }
    }

    private String formatOverlapMessage(final OverlappingIssue issue) {
        return "Overlap values: " + issue.getIntervals() + " for rules: " + issue.getRowNumbers();
    }

    private void overlapsMessageAny(final OverlappingIssue issue) {

        result.add(new DMNDTAnalysisMessage(analysis,
                                            DMNMessage.Severity.ERROR,
                                            MsgUtil.createMessage(Msg.DTANALYSIS_OVERLAP_HITPOLICY_ANY,
                                                                  formatOverlapMessage(issue)),
                                            Msg.DTANALYSIS_OVERLAP_HITPOLICY_ANY.getType(),
                                            issue.getRowNumbers()));
    }

    private void overlapAsUniqueMessage(final OverlappingIssue issue) {

        result.add(new DMNDTAnalysisMessage(analysis,
                                            DMNMessage.Severity.ERROR,
                                            MsgUtil.createMessage(Msg.DTANALYSIS_OVERLAP_HITPOLICY_UNIQUE,
                                                                  formatOverlapMessage(issue)),
                                            Msg.DTANALYSIS_OVERLAP_HITPOLICY_UNIQUE.getType(),
                                            issue.getRowNumbers()));
    }

    private void overlapToStandardDMNMessage(final OverlappingIssue issue) {

        result.add(new DMNDTAnalysisMessage(analysis,
                                            DMNMessage.Severity.INFO,
                                            MsgUtil.createMessage(Msg.DTANALYSIS_OVERLAP,
                                                                  formatOverlapMessage(issue)),
                                            Msg.DTANALYSIS_OVERLAP.getType(),
                                            issue.getRowNumbers()));
    }

    private DMNDTAnalysisMessage convert(final Issue issue) {
        return new DMNDTAnalysisMessage(analysis,
                                        convert(issue.getSeverity()),
                                        issue.getCheckType() + " " + issue.getDebugMessage() + " " + issue.getRowNumbers(),
                                        DMNMessageType.DECISION_TABLE_ANALYSIS_ERROR,
                                        issue.getRowNumbers());
    }

    private DMNMessage.Severity convert(final Severity severity) {
        switch (severity) {
            case ERROR:
                return DMNMessage.Severity.ERROR;
            case WARNING:
                return DMNMessage.Severity.WARN;
            case NOTE:
            default:
                return DMNMessage.Severity.INFO;
        }
    }
}

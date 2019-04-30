/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.validation.dtanalysis.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessage.Severity;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.lang.ast.DashNode;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.HitPolicy;
import org.kie.dmn.validation.dtanalysis.DMNDTAnalysisMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DTAnalysis {

    private static final Logger LOG = LoggerFactory.getLogger(DTAnalysis.class);

    private final List<Hyperrectangle> gaps = new ArrayList<>();
    private final List<Overlap> overlaps = new ArrayList<>();
    private final List<MaskedRule> maskedRules = new ArrayList<>();
    private final List<MisleadingRule> misleadingRules = new ArrayList<>();
    private final List<Subsumption> subsumptions = new ArrayList<>();
    private final List<Contraction> contractions = new ArrayList<>();
    private final Map<Integer, Collection<Integer>> cacheNonContractingRules = new HashMap<>();
    private final DecisionTable sourceDT;
    private final Throwable error;
    private final DDTATable ddtaTable;
    private final Collection passThruMessages = new ArrayList<>();


    public DTAnalysis(DecisionTable sourceDT, DDTATable ddtaTable) {
        this.sourceDT = sourceDT;
        this.error = null;
        this.ddtaTable = ddtaTable;
    }

    private DTAnalysis(DecisionTable sourceDT, Throwable error) {
        this.sourceDT = sourceDT;
        this.error = error;
        this.ddtaTable = null;
    }

    public static DTAnalysis ofError(DecisionTable sourceDT, Throwable error) {
        return new DTAnalysis(sourceDT, error);
    }

    public boolean isError() {
        return error != null;
    }

    public DDTATable getDdtaTable() {
        return ddtaTable;
    }

    public Collection<Hyperrectangle> getGaps() {
        return Collections.unmodifiableList(gaps);
    }

    public void addGap(Hyperrectangle gap) {
        this.gaps.add(gap);
    }

    public DMNModelInstrumentedBase getSource() {
        return sourceDT;
    }

    public List<Overlap> getOverlaps() {
        return Collections.unmodifiableList(overlaps);
    }

    public void addOverlap(Overlap overlap) {
        this.overlaps.add(overlap);
    }

    public void normalize() {
        int prevSize = this.overlaps.size();
        internalNormalize();
        int curSize = this.overlaps.size();
        if (curSize != prevSize) {
            normalize();
        }
    }

    private void internalNormalize() {
        List<Overlap> newOverlaps = new ArrayList<>();
        List<Overlap> overlapsProcessing = new ArrayList<>();
        overlapsProcessing.addAll(overlaps);
        while (!overlapsProcessing.isEmpty()) {
            List<Overlap> toBeRemoved = new ArrayList<>();
            List<Overlap> toBeAdded = new ArrayList<>();
            Overlap curOverlap = overlapsProcessing.remove(0);
            for (Overlap otherOverlap : overlapsProcessing) {
                if (curOverlap == null) {
                    break;
                }
                int x = curOverlap.contigousOnDimension(otherOverlap);
                if (x > 0) {
                    Overlap mergedOverlap = Overlap.newByMergeOnDimension(curOverlap, otherOverlap, x);
                    curOverlap = null;
                    toBeRemoved.add(otherOverlap);
                    toBeAdded.add(mergedOverlap);
                }
            }
            for (Overlap x : toBeRemoved) {
                overlapsProcessing.remove(x);
            }
            for (Overlap x : toBeAdded) {
                overlapsProcessing.add(0, x);
            }
            if (curOverlap != null) {
                newOverlaps.add(curOverlap);
            }
        }
        this.overlaps.clear();
        this.overlaps.addAll(newOverlaps);
    }

    public List<DMNMessage> asDMNMessages() {
        List<DMNMessage> results = new ArrayList<>();
        if (isError()) {
            DMNMessage m = new DMNDTAnalysisMessage(this,
                                                    Severity.WARN,
                                                    MsgUtil.createMessage(Msg.DTANALYSIS_ERROR_ANALYSIS_SKIPPED,
                                                                          sourceDT.getOutputLabel(),
                                                                          error.getMessage()),
                                                    Msg.DTANALYSIS_ERROR_ANALYSIS_SKIPPED.getType());
            results.add(m);
            return results;
        }
        results.addAll(passThruMessages());
        results.addAll(gapsAsMessages());
        results.addAll(overlapsAsMessages());
        warnAboutHitPolicyFirst(results);
        results.addAll(maskedAndMisleadingRulesAsMessagesIfPriority());
        results.addAll(subsumptionsAsMessages());
        results.addAll(contractionsAsMessages());

        // keep last.
        if (results.isEmpty()) {
            DMNMessage m = new DMNDTAnalysisMessage(this,
                                                    Severity.INFO,
                                                    MsgUtil.createMessage(Msg.DTANALYSIS_EMPTY,
                                                                          sourceDT.getOutputLabel()),
                                                    Msg.DTANALYSIS_EMPTY.getType());
            results.add(m);
            return results;
        }
        return results;
    }

    private Collection<? extends DMNMessage> maskedAndMisleadingRulesAsMessagesIfPriority() {
        if (sourceDT.getHitPolicy() != HitPolicy.PRIORITY) {
            return Collections.emptyList();
        }
        List<DMNDTAnalysisMessage> results = new ArrayList<>();
        for (MaskedRule masked : maskedRules) {
            results.add(new DMNDTAnalysisMessage(this,
                                                 Severity.ERROR,
                                                 MsgUtil.createMessage(Msg.DTANALYSIS_HITPOLICY_PRIORITY_MASKED_RULE,
                                                                       masked.maskedRule,
                                                                       masked.maskedBy),
                                                 Msg.DTANALYSIS_HITPOLICY_PRIORITY_MASKED_RULE.getType()));
        }
        for (MisleadingRule misleading : misleadingRules) {
            results.add(new DMNDTAnalysisMessage(this,
                                                 Severity.WARN,
                                                 MsgUtil.createMessage(Msg.DTANALYSIS_HITPOLICY_PRIORITY_MISLEADING_RULE,
                                                                       misleading.misleadingRule,
                                                                       misleading.misleadRule),
                                                 Msg.DTANALYSIS_HITPOLICY_PRIORITY_MISLEADING_RULE.getType()));
        }
        return results;
    }

    private Collection<? extends DMNMessage> subsumptionsAsMessages() {
        List<DMNDTAnalysisMessage> results = new ArrayList<>();
        for (Subsumption s : subsumptions) {
            results.add(new DMNDTAnalysisMessage(this,
                                                 Severity.WARN,
                                                 MsgUtil.createMessage(Msg.DTANALYSIS_SUBSUMPTION_RULE,
                                                                       s.rule,
                                                                       s.includedRule,
                                                                       s.rule,
                                                                       s.includedRule),
                                                 Msg.DTANALYSIS_SUBSUMPTION_RULE.getType()));
        }
        return results;
    }

    private Collection<? extends DMNMessage> contractionsAsMessages() {
        List<DMNDTAnalysisMessage> results = new ArrayList<>();
        for (Contraction x : contractions) {
            results.add(new DMNDTAnalysisMessage(this,
                                                 Severity.WARN,
                                                 MsgUtil.createMessage(Msg.DTANALYSIS_CONTRACTION_RULE,
                                                                       x.rule,
                                                                       x.pairedRule),
                                                 Msg.DTANALYSIS_CONTRACTION_RULE.getType()));
        }
        return results;
    }

    private void warnAboutHitPolicyFirst(final List<DMNMessage> results) {
        if (sourceDT.getHitPolicy() == HitPolicy.FIRST) {
            results.add(new DMNDTAnalysisMessage(this,
                                                 Severity.WARN,
                                                 MsgUtil.createMessage(Msg.DTANALYSIS_HITPOLICY_FIRST,
                                                                       sourceDT.getOutputLabel()),
                                                 Msg.DTANALYSIS_HITPOLICY_FIRST.getType()));
        }
    }

    private Collection passThruMessages() {
        return passThruMessages;
    }

    private Collection overlapsAsMessages() {
        List<DMNDTAnalysisMessage> results = new ArrayList<>();
        for (Overlap overlap : overlaps) {
            switch (sourceDT.getHitPolicy()) {
                case UNIQUE:
                    results.add(new DMNDTAnalysisMessage(this,
                                                         Severity.ERROR,
                                                         MsgUtil.createMessage(Msg.DTANALYSIS_OVERLAP_HITPOLICY_UNIQUE,
                                                                               overlap.asHumanFriendly(ddtaTable)),
                                                         Msg.DTANALYSIS_OVERLAP_HITPOLICY_UNIQUE.getType()));
                    break;
                case ANY:
                    List<Comparable<?>> prevValue = ddtaTable.getRule().get(overlap.getRules().get(0) - 1).getOutputEntry();
                    for (int i = 1; i < overlap.getRules().size(); i++) { // deliberately start index 1 for 2nd overlapping rule number.
                        int curIndex = overlap.getRules().get(i) - 1;
                        List<Comparable<?>> curValue = ddtaTable.getRule().get(curIndex).getOutputEntry();
                        if (!prevValue.equals(curValue)) {
                            results.add(new DMNDTAnalysisMessage(this,
                                                                 Severity.ERROR,
                                                                 MsgUtil.createMessage(Msg.DTANALYSIS_OVERLAP_HITPOLICY_ANY,
                                                                                       overlap.asHumanFriendly(ddtaTable)),
                                                                 Msg.DTANALYSIS_OVERLAP_HITPOLICY_ANY.getType()));
                            break;
                        } else {
                            prevValue = curValue;
                        }
                    }
                    break;
                default:
                    results.add(overlapToStandardDMNMessage(overlap));
                    break;
            }
        }
        return results;
    }

    private DMNDTAnalysisMessage overlapToStandardDMNMessage(Overlap overlap) {
        return new DMNDTAnalysisMessage(this,
                                        Severity.WARN,
                                        MsgUtil.createMessage(Msg.DTANALYSIS_OVERLAP,
                                                              overlap.asHumanFriendly(ddtaTable)),
                                        Msg.DTANALYSIS_OVERLAP.getType());
    }

    private Collection gapsAsMessages() {
        List<DMNDTAnalysisMessage> results = new ArrayList<>();
        for (Hyperrectangle gap : gaps) {
            results.add(new DMNDTAnalysisMessage(this,
                                                 Severity.WARN,
                                                 MsgUtil.createMessage(Msg.DTANALYSIS_GAP,
                                                                       gap.asHumanFriendly(ddtaTable)),
                                                 Msg.DTANALYSIS_GAP.getType()));
        }
        return results;
    }

    public void computeMaskedRules() {
        if (sourceDT.getHitPolicy() != HitPolicy.PRIORITY) {
            return;
        }
        for (Overlap overlap : overlaps) {
            analyseOverlapForMaskedRules(overlap);
        }
    }

    private void analyseOverlapForMaskedRules(Overlap overlap) {
        for (Integer ruleId : overlap.getRules()) {
            List<Comparable<?>> curValues = ddtaTable.getRule().get(ruleId - 1).getOutputEntry();

            for (int jOutputIdx = 0; jOutputIdx < ddtaTable.outputCols(); jOutputIdx++) {
                DDTAOutputClause curOutputClause = ddtaTable.getOutputs().get(jOutputIdx);
                if (curOutputClause.isDiscreteDomain()) {
                    int curOutputIdx = curOutputClause.getOutputOrder().indexOf(curValues.get(jOutputIdx));

                    List<Integer> otherRules = listWithoutElement(overlap.getRules(), ruleId);

                    for (Integer otherRuleID : otherRules) {
                        List<Comparable<?>> otherRuleValues = ddtaTable.getRule().get(otherRuleID - 1).getOutputEntry();
                        int otherOutputIdx = curOutputClause.getOutputOrder().indexOf(otherRuleValues.get(jOutputIdx));
                        if (curOutputIdx > otherOutputIdx) {
                            try {
                                boolean isOtherRuleWider = comparingRulesIsRightWider(ruleId, otherRuleID);
                                if (isOtherRuleWider) {
                                    maskedRules.add(new MaskedRule(ruleId, otherRuleID));
                                }
                            } catch (ComparingRulesWithMultipleInputEntries e) {
                                passThruMessages.add(new DMNDTAnalysisMessage(this,
                                                                              Severity.WARN,
                                                                              MsgUtil.createMessage(Msg.DTANALYSIS_HITPOLICY_PRIORITY_ANALYSIS_SKIPPED,
                                                                                                    sourceDT.getOutputLabel(),
                                                                                                    ruleId, otherRuleID),
                                                                              Msg.DTANALYSIS_HITPOLICY_PRIORITY_ANALYSIS_SKIPPED.getType()));
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean comparingRulesIsRightWider(int ruleId, int isWiderRuleId) throws ComparingRulesWithMultipleInputEntries {
        boolean isOtherRuleWider = true;
        for (int jInputIdx = 0; isOtherRuleWider && jInputIdx < ddtaTable.inputCols(); jInputIdx++) {
            DDTAInputEntry ruleIdInputAtIdx = ddtaTable.getRule().get(ruleId - 1).getInputEntry().get(jInputIdx);
            DDTAInputEntry otherRuleInputAtIdx = ddtaTable.getRule().get(isWiderRuleId - 1).getInputEntry().get(jInputIdx);
            if (ruleIdInputAtIdx.getIntervals().size() != 1 || otherRuleInputAtIdx.getIntervals().size() != 1) {
                throw new ComparingRulesWithMultipleInputEntries("Multiple entries not supported");
            } else {
                Interval ruleIdInterval = ruleIdInputAtIdx.getIntervals().get(0);
                Interval otherRuleInterval = otherRuleInputAtIdx.getIntervals().get(0);
                isOtherRuleWider = isOtherRuleWider && otherRuleInterval.includes(ruleIdInterval);
            }
        }
        return isOtherRuleWider;
    }

    public class ComparingRulesWithMultipleInputEntries extends Exception {

        public ComparingRulesWithMultipleInputEntries(String message) {
            super(message);
        }

    }

    public List<MaskedRule> getMaskedRules() {
        return Collections.unmodifiableList(maskedRules);
    }

    public void computeMisleadingRules() {
        if (sourceDT.getHitPolicy() != HitPolicy.PRIORITY) {
            return;
        }
        for (Overlap overlap : overlaps) {
            analyseOverlapForMisleadingRules(overlap);
        }
    }

    private void analyseOverlapForMisleadingRules(Overlap overlap) {
        for (Integer ruleId : overlap.getRules()) {
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
                            List<Integer> otherRules = listWithoutElement(overlap.getRules(), ruleId);
                            for (Integer otherRuleID : otherRules) {
                                List<Comparable<?>> otherRuleValues = ddtaTable.getRule().get(otherRuleID - 1).getOutputEntry();
                                int otherOutputIdx = curOutputClause.getOutputOrder().indexOf(otherRuleValues.get(jOutputIdx));
                                if (otherOutputIdx > curOutputIdx) {
                                    misleadingRules.add(new MisleadingRule(ruleId, otherRuleID));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static <T> List<T> listWithoutElement(List<T> coll, T elem) {
        List<T> others = new ArrayList<>(coll);
        others.remove(elem);
        return others;
    }

    public List<MisleadingRule> getMisleadingRules() {
        return Collections.unmodifiableList(misleadingRules);
    }

    public void computeSubsumptions() {
        for (Overlap overlap : overlaps) {
            analyseOverlapForSubsumptions(overlap);
        }
    }

    private void analyseOverlapForSubsumptions(Overlap overlap) {
        Set<List<Comparable<?>>> outputEntries = new HashSet<>();
        for (Integer ruleId : overlap.getRules()) {
            List<Comparable<?>> curValues = ddtaTable.getRule().get(ruleId - 1).getOutputEntry();
            outputEntries.add(curValues);
        }
        for (List<Comparable<?>> curOutputEntry : outputEntries) {
            List<Integer> rulesWithGivenOutputEntry = new ArrayList<>();
            for (Integer ruleId : overlap.getRules()) {
                List<Comparable<?>> curValues = ddtaTable.getRule().get(ruleId - 1).getOutputEntry();
                if (curValues.equals(curOutputEntry)) {
                    rulesWithGivenOutputEntry.add(ruleId);
                }
            }
            for (Integer ruleId : rulesWithGivenOutputEntry) {
                List<DDTAInputEntry> curInputEntries = ddtaTable.getRule().get(ruleId - 1).getInputEntry();
                List<Integer> otherRules = listWithoutElement(rulesWithGivenOutputEntry, ruleId);
                for (Integer otherRuleId : otherRules) {
                    List<DDTAInputEntry> otherInputEntries = ddtaTable.getRule().get(otherRuleId - 1).getInputEntry();
                    boolean inputEntriesIncludeAll = DDTARule.inputEntriesIncludeAll(curInputEntries, otherInputEntries);
                    if (inputEntriesIncludeAll) {
                        subsumptions.add(new Subsumption(ruleId, otherRuleId));
                    }
                }
            }
        }
    }

    public List<Subsumption> getSubsumptions() {
        return Collections.unmodifiableList(subsumptions);
    }

    private boolean areRulesSubsumption(Integer a, Integer b) {
        return subsumptions.stream().filter(s -> (s.rule == a && s.includedRule == b) || (s.rule == b && s.includedRule == a)).findAny().isPresent();
    }

    private boolean areRulesContraction(Integer a, Integer b) {
        return contractions.stream().filter(s -> (s.rule == b && s.pairedRule == a) || (s.rule == a && s.pairedRule == b)).findAny().isPresent();
    }

    private boolean areRulesInNonContractionCache(Integer a, Integer b) {
        return cacheNonContractingRules.getOrDefault(a, Collections.emptySet()).contains(b) || cacheNonContractingRules.getOrDefault(b, Collections.emptySet()).contains(a);
    }

    public void computeContractions() {
        Set<List<Comparable<?>>> outputEntries = new HashSet<>();
        for (DDTARule rule : ddtaTable.getRule()) {
            List<Comparable<?>> curValues = rule.getOutputEntry();
            outputEntries.add(curValues);
        }
        for (List<Comparable<?>> curOutputEntry : outputEntries) {
            List<Integer> rulesWithGivenOutputEntry = new ArrayList<>();
            for (int i = 0; i < ddtaTable.getRule().size(); i++) {
                List<Comparable<?>> curValues = ddtaTable.getRule().get(i).getOutputEntry();
                if (curValues.equals(curOutputEntry)) {
                    rulesWithGivenOutputEntry.add(i + 1);
                }
            }
            for (Integer ruleId : rulesWithGivenOutputEntry) {
                List<DDTAInputEntry> curInputEntries = ddtaTable.getRule().get(ruleId - 1).getInputEntry();
                List<Integer> otherRules = listWithoutElement(rulesWithGivenOutputEntry, ruleId);
                for (Integer otherRuleId : otherRules) {
                    if (areRulesSubsumption(ruleId, otherRuleId) || areRulesContraction(ruleId, otherRuleId) || areRulesInNonContractionCache(ruleId, otherRuleId)) {
                        continue;
                    }
                    LOG.debug("computeContractions ruleId {} otherRuleId {}", ruleId, otherRuleId);
                    List<DDTAInputEntry> otherInputEntries = ddtaTable.getRule().get(otherRuleId - 1).getInputEntry();
                    boolean detectedAdjacentOrOverlap = false;
                    boolean allEqualsAllowingOneAdjOverlap = true;
                    for (int i = 0; i < curInputEntries.size(); i++) {
                        DDTAInputEntry curIE = curInputEntries.get(i);
                        DDTAInputEntry otherIE = otherInputEntries.get(i);
                        boolean intervalsAreEqual = curIE.getIntervals().equals(otherIE.getIntervals());
                        if (intervalsAreEqual) {
                            continue;
                        }
                        boolean canOverlapThisDimention = !detectedAdjacentOrOverlap && curIE.adjOrOverlap(otherIE);
                        if (canOverlapThisDimention) {
                            detectedAdjacentOrOverlap = true;
                            continue;
                        }
                        allEqualsAllowingOneAdjOverlap = false;
                    }
                    if (allEqualsAllowingOneAdjOverlap) {
                        contractions.add(new Contraction(ruleId, otherRuleId));
                    } else {
                        cacheNonContractingRules.computeIfAbsent(otherRuleId, x -> new HashSet<>()).add(ruleId);
                        cacheNonContractingRules.computeIfAbsent(ruleId, x -> new HashSet<>()).add(otherRuleId);
                    }
                }
            }
        }
    }

    public List<Contraction> getContractions() {
        return Collections.unmodifiableList(contractions);
    }
}

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.validation.dtanalysis.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessage.Severity;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.lang.ast.DashNode;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.HitPolicy;
import org.kie.dmn.model.api.InputClause;
import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.validation.ValidatorUtil;
import org.kie.dmn.validation.dtanalysis.DMNDTAnalysisMessage;
import org.kie.dmn.validation.dtanalysis.mcdc.MCDCAnalyser.PosNegBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DTAnalysis {

    private static final Logger LOG = LoggerFactory.getLogger(DTAnalysis.class);

    private final List<Hyperrectangle> gaps = new ArrayList<>();
    private final List<Overlap> overlaps = new ArrayList<>();
    private final List<MaskedRule> maskedRules = new ArrayList<>();
    private final Set<MisleadingRule> misleadingRules = new HashSet<>();
    private final List<Subsumption> subsumptions = new ArrayList<>();
    private final List<Contraction> contractions = new ArrayList<>();
    private final Map<Integer, Collection<Integer>> cacheNonContractingRules = new HashMap<>();
    private boolean c1stNFViolation = false;
    private Collection<Collection<Integer>> cOfDuplicateRules = Collections.emptyList();
    private Collection<Contraction> contractionsViolating2ndNF = new ArrayList<>();
    private Collection<RuleColumnCoordinate> cellsViolating2ndNF = new ArrayList<>();
    private final DecisionTable sourceDT;
    private final Throwable error;
    private final DDTATable ddtaTable;
    private final Collection<DMNMessage> passThruMessages = new ArrayList<>();
    private List<PosNegBlock> selectedBlocks;

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

    public DecisionTable getSource() {
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
                                                                          nameOrIDOfTable(),
                                                                          error.getMessage()),
                                                    Msg.DTANALYSIS_ERROR_ANALYSIS_SKIPPED.getType());
            results.add(m);
            return results;
        }
        results.addAll(passThruMessages());
        results.addAll(gapsAsMessages());
        results.addAll(overlapsAsMessages());
        results.addAll(maskedAndMisleadingRulesAsMessagesIfPriority());
        results.addAll(subsumptionsAsMessages());
        results.addAll(contractionsAsMessages());
        results.addAll(check1stNFViolationAsMessages());
        results.addAll(check2ndNFViolationAsMessages());

        // keep last.
        if (results.isEmpty()) {
            DMNMessage m = new DMNDTAnalysisMessage(this,
                                                    Severity.INFO,
                                                    MsgUtil.createMessage(Msg.DTANALYSIS_EMPTY,
                                                                          nameOrIDOfTable()),
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
                                                 Msg.DTANALYSIS_HITPOLICY_PRIORITY_MASKED_RULE.getType(), Collections.singletonList(masked.maskedRule)));
        }
        for (MisleadingRule misleading : misleadingRules) {
            boolean duplicatesAMasked = maskedRules.stream().anyMatch(masked -> masked.maskedBy == misleading.misleadingRule && masked.maskedRule == misleading.misleadRule);
            if (!duplicatesAMasked) {
                results.add(new DMNDTAnalysisMessage(this,
                                                     Severity.WARN,
                                                     MsgUtil.createMessage(Msg.DTANALYSIS_HITPOLICY_PRIORITY_MISLEADING_RULE,
                                                                           misleading.misleadingRule,
                                                                           misleading.misleadRule),
                                                     Msg.DTANALYSIS_HITPOLICY_PRIORITY_MISLEADING_RULE.getType(), Collections.singletonList(misleading.misleadingRule)));
            } else {
                LOG.debug("Misleading record is not displayed as message because it is redundant to a Masked rule message: {}", misleading);
            }
        }
        return results;
    }

    private Collection<? extends DMNMessage> subsumptionsAsMessages() {
        List<DMNDTAnalysisMessage> results = new ArrayList<>();
        for (Subsumption s : subsumptions) {
            List<Integer> inNaturalOrder = Arrays.asList(s.rule, s.includedRule);
            List<Integer> inReversedOrder = Arrays.asList(s.includedRule, s.rule);
            boolean subsumptionIsA1NFdup = getDuplicateRulesTuples().stream().anyMatch(tuple -> tuple.equals(inNaturalOrder) || tuple.equals(inReversedOrder));
            if (!subsumptionIsA1NFdup) {
                results.add(new DMNDTAnalysisMessage(this,
                                                     Severity.WARN,
                                                     MsgUtil.createMessage(Msg.DTANALYSIS_SUBSUMPTION_RULE,
                                                                           s.rule,
                                                                           s.includedRule,
                                                                           s.rule,
                                                                           s.includedRule),
                                                     Msg.DTANALYSIS_SUBSUMPTION_RULE.getType(), Collections.singletonList(s.rule)));
            } else {
                LOG.debug("skipping Subsumption message because it is actually redundant to the 1st NF duplicate rule ERROR: {}", s);
            }
        }
        return results;
    }

    private Collection<? extends DMNMessage> contractionsAsMessages() {
        List<DMNDTAnalysisMessage> results = new ArrayList<>();
        for (Contraction x : contractions) {
            results.add(new DMNDTAnalysisMessage(this,
                                                 Severity.WARN,
                                                 MsgUtil.createMessage(Msg.DTANALYSIS_CONTRACTION_RULE,
                                                                       x.impactedRules(),
                                                                       x.adjacentDimension),
                                                 Msg.DTANALYSIS_CONTRACTION_RULE.getType(), x.impactedRules()));
        }
        return results;
    }

    private Collection<? extends DMNMessage> check1stNFViolationAsMessages() {
        if (!is1stNFViolation()) {
            return Collections.emptyList();
        }
        List<DMNDTAnalysisMessage> results = new ArrayList<>();
        if (sourceDT.getHitPolicy() == HitPolicy.FIRST) {
            results.add(new DMNDTAnalysisMessage(this,
                                                 Severity.WARN,
                                                 MsgUtil.createMessage(Msg.DTANALYSIS_1STNFVIOLATION_FIRST),
                                                 Msg.DTANALYSIS_1STNFVIOLATION_FIRST.getType()));
        }
        if (sourceDT.getHitPolicy() == HitPolicy.RULE_ORDER) {
            results.add(new DMNDTAnalysisMessage(this,
                                                 Severity.WARN,
                                                 MsgUtil.createMessage(Msg.DTANALYSIS_1STNFVIOLATION_RULE_ORDER),
                                                 Msg.DTANALYSIS_1STNFVIOLATION_RULE_ORDER.getType()));
        }
        for (Collection<Integer> duplicateRulesTuple : getDuplicateRulesTuples()) {
            results.add(new DMNDTAnalysisMessage(this,
                                                 sourceDT.getHitPolicy() == HitPolicy.COLLECT ? Severity.WARN : Severity.ERROR,
                                                 MsgUtil.createMessage(Msg.DTANALYSIS_1STNFVIOLATION_DUPLICATE_RULES,
                                                                       duplicateRulesTuple),
                                                 Msg.DTANALYSIS_1STNFVIOLATION_DUPLICATE_RULES.getType(), duplicateRulesTuple));
        }
        return results;
    }

    private Collection<? extends DMNMessage> check2ndNFViolationAsMessages() {
        if (!is2ndNFViolation()) {
            return Collections.emptyList();
        }
        List<DMNDTAnalysisMessage> results = new ArrayList<>();
        for (Contraction c : getContractionsViolating2ndNF()) {
            results.add(new DMNDTAnalysisMessage(this,
                                                 Severity.WARN,
                                                 MsgUtil.createMessage(Msg.DTANALYSIS_2NDNFVIOLATION,
                                                                       c.adjacentDimension,
                                                                       c.impactedRules()),
                                                 Msg.DTANALYSIS_2NDNFVIOLATION.getType(), c.impactedRules()));
        }
        for (RuleColumnCoordinate c : getCellsViolating2ndNF()) {
            results.add(new DMNDTAnalysisMessage(this,
                                                 Severity.WARN,
                                                 MsgUtil.createMessage(Msg.DTANALYSIS_2NDNFVIOLATION_WAS_DASH,
                                                                       c.feelText,
                                                                       c.rule,
                                                                       c.column),
                                                 Msg.DTANALYSIS_2NDNFVIOLATION.getType(), List.of(c.rule)));
        }
        return results;
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
                                                         Msg.DTANALYSIS_OVERLAP_HITPOLICY_UNIQUE.getType(), overlap.getRules()));
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
                                                                 Msg.DTANALYSIS_OVERLAP_HITPOLICY_ANY.getType(), overlap.getRules()));
                            break;
                        } else {
                            prevValue = curValue;
                        }
                    }
                    break;
                case PRIORITY:
                case FIRST:
                default:
                    LOG.debug("In case of any other HitPolicy no overalps is reported, DROOLS-5363: {}", overlap);
                    break;
            }
        }
        return results;
    }

    private Collection gapsAsMessages() {
        List<DMNDTAnalysisMessage> results = new ArrayList<>();
        if (!ddtaTable.getColIDsStringWithoutEnum().isEmpty()) {
            List<String> names = ddtaTable.getColIDsStringWithoutEnum()
                                          .stream()
                                          .map(id -> sourceDT.getInput().get(id - 1))
                                          .map(InputClause::getInputExpression)
                                          .map(LiteralExpression::getText)
                                          .collect(Collectors.toList());
            results.add(new DMNDTAnalysisMessage(this,
                                                 Severity.WARN,
                                                 MsgUtil.createMessage(Msg.DTANALYSIS_GAP_SKIPPED_BECAUSE_FREE_STRING,
                                                                       names),
                                                 Msg.DTANALYSIS_GAP_SKIPPED_BECAUSE_FREE_STRING.getType()));
            return results;
        }
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
                                                                              Msg.DTANALYSIS_HITPOLICY_PRIORITY_ANALYSIS_SKIPPED.getType(), Collections.singletonList(ruleId)));
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
                        for (int col = 0; col < inputEntry.size(); col++) {
                            boolean thisColIsHypen = inputEntry.get(col).getUts().stream().anyMatch(DashNode.class::isInstance);
                            if (thisColIsHypen) {
                                List<Integer> otherRules = listWithoutElement(overlap.getRules(), ruleId);
                                for (Integer otherRuleID : otherRules) {
                                    List<Comparable<?>> otherRuleValues = ddtaTable.getRule().get(otherRuleID - 1).getOutputEntry();
                                    int otherOutputIdx = curOutputClause.getOutputOrder().indexOf(otherRuleValues.get(jOutputIdx));
                                    List<DDTAInputEntry> otherRuleInputEntry = ddtaTable.getRule().get(otherRuleID - 1).getInputEntry();
                                    boolean thatColIsHypen = otherRuleInputEntry.get(col).getUts().stream().anyMatch(DashNode.class::isInstance);
                                    if (otherOutputIdx > curOutputIdx && !thatColIsHypen) {
                                        misleadingRules.add(new MisleadingRule(ruleId, otherRuleID));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static <T> List<T> listWithoutElement(Collection<T> coll, T elem) {
        List<T> others = new ArrayList<>(coll);
        others.remove(elem);
        return others;
    }

    public Collection<MisleadingRule> getMisleadingRules() {
        return Collections.unmodifiableSet(misleadingRules);
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
            Set<Integer> rulesWithGivenOutputEntry = new LinkedHashSet<>();
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
        return contractions.stream().filter(s -> (s.rule == b && s.pairedRules.contains(a)) || (s.rule == a && s.pairedRules.contains(b))).findAny().isPresent();
    }

    private boolean areRulesInNonContractionCache(Integer a, Integer b) {
        return cacheNonContractingRules.getOrDefault(a, Collections.emptySet()).contains(b) || cacheNonContractingRules.getOrDefault(b, Collections.emptySet()).contains(a);
    }

    public void computeContractions() {
        Set<List<Comparable<?>>> outputEntries = ddtaTable.outputEntries();
        for (List<Comparable<?>> curOutputEntry : outputEntries) {
            List<Integer> rulesWithGivenOutputEntry = ddtaTable.ruleIDsByOutputEntry(curOutputEntry);
            for (Integer ruleId : rulesWithGivenOutputEntry) {
                List<DDTAInputEntry> curInputEntries = ddtaTable.getRule().get(ruleId - 1).getInputEntry();
                List<Integer> otherRules = listWithoutElement(rulesWithGivenOutputEntry, ruleId);
                for (Integer otherRuleId : otherRules) {
                    if (areRulesSubsumption(ruleId, otherRuleId) || areRulesContraction(ruleId, otherRuleId) || areRulesInNonContractionCache(ruleId, otherRuleId)) {
                        continue;
                    }
                    LOG.debug("computeContractions ruleId {} otherRuleId {}", ruleId, otherRuleId);
                    List<DDTAInputEntry> otherInputEntries = ddtaTable.getRule().get(otherRuleId - 1).getInputEntry();
                    int detectedAdjacentOrOverlap = 0;
                    boolean allEqualsAllowingOneAdjOverlap = true;
                    for (int i = 0; i < curInputEntries.size(); i++) {
                        DDTAInputEntry curIE = curInputEntries.get(i);
                        DDTAInputEntry otherIE = otherInputEntries.get(i);
                        boolean intervalsAreEqual = curIE.getIntervals().equals(otherIE.getIntervals());
                        if (intervalsAreEqual) {
                            continue;
                        }
                        boolean canOverlapThisDimention = detectedAdjacentOrOverlap == 0 && curIE.adjOrOverlap(otherIE);
                        if (canOverlapThisDimention) {
                            detectedAdjacentOrOverlap = i + 1;
                            continue;
                        }
                        allEqualsAllowingOneAdjOverlap = false;
                    }
                    if (allEqualsAllowingOneAdjOverlap) {
                        List<Interval> allIntervals = new ArrayList<>();
                        allIntervals.addAll(curInputEntries.get(detectedAdjacentOrOverlap - 1).getIntervals());
                        allIntervals.addAll(otherInputEntries.get(detectedAdjacentOrOverlap - 1).getIntervals());
                        List<Interval> flatten = Interval.flatten(allIntervals);
                        DDTAInputClause ddtaInputClause = ddtaTable.getInputs().get(detectedAdjacentOrOverlap - 1);
                        if (ddtaInputClause.isDiscreteDomain()) {
                            flatten = Interval.normalizeDiscrete(flatten, ddtaInputClause.getDiscreteValues());
                        }
                        Contraction contraction = new Contraction(ruleId, List.of(otherRuleId), detectedAdjacentOrOverlap, flatten);
                        LOG.debug("NEW CONTRACTION: {}", contraction);
                        contractions.add(contraction);
                    } else {
                        cacheNonContractingRules.computeIfAbsent(otherRuleId, x -> new HashSet<>()).add(ruleId);
                        cacheNonContractingRules.computeIfAbsent(ruleId, x -> new HashSet<>()).add(otherRuleId);
                    }
                }
            }
        }
        if (!this.contractions.isEmpty()) {
            normalizeContractions(); // early normalization call to suite for consistent 2NF computations.
        }
    }

    private void normalizeContractions() {
        int prevSize = this.overlaps.size();
        internalNormalizeContractions();
        int curSize = this.overlaps.size();
        if (curSize != prevSize) {
            normalizeContractions();
        }
    }

    private void internalNormalizeContractions() {
        List<Contraction> newCollection = new ArrayList<>();
        List<Contraction> collectionProcessing = new ArrayList<>();
        collectionProcessing.addAll(this.contractions);
        while (!collectionProcessing.isEmpty()) {
            List<Contraction> toBeRemoved = new ArrayList<>();
            List<Contraction> toBeAdded = new ArrayList<>();
            Contraction cur = collectionProcessing.remove(0);
            for (Contraction other : collectionProcessing) {
                if (cur == null) {
                    break;
                }
                if (cur.adjacentDimension == other.adjacentDimension && Interval.adjOrOverlap(cur.dimensionAsContracted, other.dimensionAsContracted)) {
                    List<Interval> intervals = new ArrayList<>();
                    intervals.addAll(cur.dimensionAsContracted);
                    intervals.addAll(other.dimensionAsContracted);
                    List<Interval> flatten = Interval.flatten(intervals);
                    DDTAInputClause ddtaInputClause = ddtaTable.getInputs().get(cur.adjacentDimension - 1);
                    if (ddtaInputClause.isDiscreteDomain()) {
                        flatten = Interval.normalizeDiscrete(flatten, ddtaInputClause.getDiscreteValues());
                    }
                    Set<Integer> allRules = new HashSet<>();
                    allRules.add(cur.rule);
                    allRules.add(other.rule);
                    allRules.addAll(cur.pairedRules);
                    allRules.addAll(other.pairedRules);
                    Integer mainRuleId = Collections.min(allRules);
                    allRules.remove(mainRuleId);
                    Contraction merged = new Contraction(mainRuleId, allRules, cur.adjacentDimension, flatten);
                    LOG.debug("MERGED CONTRACTION: {}", merged);
                    cur = null;
                    toBeRemoved.add(other);
                    toBeAdded.add(merged);
                }
            }
            for (Contraction x : toBeRemoved) {
                collectionProcessing.remove(x);
            }
            for (Contraction x : toBeAdded) {
                collectionProcessing.add(0, x);
            }
            if (cur != null) {
                newCollection.add(cur);
            }
        }
        this.contractions.clear();
        this.contractions.addAll(newCollection);
    }

    public List<Contraction> getContractions() {
        return Collections.unmodifiableList(contractions);
    }

    public void compute1stNFViolations() {
        if (sourceDT.getHitPolicy() == HitPolicy.FIRST || sourceDT.getHitPolicy() == HitPolicy.RULE_ORDER) {
            c1stNFViolation = true;
        }
        cOfDuplicateRules = ddtaTable.getCacheByInputEntry().values().stream().filter(c -> c.size() > 1).collect(Collectors.toList());
        if (!cOfDuplicateRules.isEmpty()) {
            c1stNFViolation = true;
        }
        LOG.debug("compute1stNFViolations() c1stNFViolation result: {}", c1stNFViolation);
    }

    public boolean is1stNFViolation() {
        return c1stNFViolation;
    }

    public Collection<Collection<Integer>> getDuplicateRulesTuples() {
        return Collections.unmodifiableCollection(cOfDuplicateRules);
    }

    public void compute2ndNFViolations() {
        if (is1stNFViolation()) {
            LOG.debug("Violated already at 1st NF.");
            return;
        }
        for (Contraction c : contractions) { // is a contraction resulting in a 2NF violation?
            if (c.dimensionAsContracted.size() == 1) {
                Interval domainMinMax = ddtaTable.getInputs().get(c.adjacentDimension - 1).getDomainMinMax();
                if (domainMinMax.equals(c.dimensionAsContracted.get(0))) {
                    LOG.debug("compute2ndNFViolations() Contraction: {} violates 2NF", c);
                    contractionsViolating2ndNF.add(c);
                }
            }
        }
        for (int r = 0; r < ddtaTable.getRule().size(); r++) { // is a cell equivalent to a Dash `-` in DMN decision table?
            DDTARule rule = ddtaTable.getRule().get(r);
            for (int c = 0; c < ddtaTable.getInputs().size(); c++) {
                if (rule.getInputEntry().get(c).getIntervals().size() != 1) {
                    continue;
                }
                Interval int0 = rule.getInputEntry().get(c).getIntervals().get(0);
                if (!(rule.getInputEntry().get(c).getUts().get(0) instanceof DashNode) &&
                    int0.getLowerBound().getBoundaryType() == RangeBoundary.CLOSED &&
                    int0.getUpperBound().getBoundaryType() == RangeBoundary.CLOSED &&
                    !int0.getLowerBound().getValue().equals(int0.getUpperBound().getValue())) { // a normalized closed-interval, but not a `-`: but is it equivalent to it?
                    DDTAInputClause col = ddtaTable.getInputs().get(c);
                    boolean includes = int0.includes(col.getDomainMinMax());
                    if (includes) {
                        RuleColumnCoordinate rc = new RuleColumnCoordinate(r + 1, c + 1, sourceDT.getRule().get(r).getInputEntry().get(c).getText());
                        LOG.debug("compute2ndNFViolations() Cell: {} violates 2NF", rc);
                        cellsViolating2ndNF.add(rc);
                    }
                }
            }
        }
        LOG.debug("compute2ndNFViolations() c2ndNFViolation result: {}", is2ndNFViolation());
    }

    public boolean is2ndNFViolation() {
        return !contractionsViolating2ndNF.isEmpty() || !cellsViolating2ndNF.isEmpty();
    }

    public Collection<Contraction> getContractionsViolating2ndNF() {
        return Collections.unmodifiableCollection(contractionsViolating2ndNF);
    }

    public Collection<RuleColumnCoordinate> getCellsViolating2ndNF() {
        return Collections.unmodifiableCollection(cellsViolating2ndNF);
    }

    public void computeHitPolicyRecommender() {
        if (!gaps.isEmpty() || !isHitPolicySingle(sourceDT.getHitPolicy())) {
            return;
        }
        if (overlaps.isEmpty() && sourceDT.getHitPolicy() != HitPolicy.UNIQUE) {
            passThruMessages.add(new DMNDTAnalysisMessage(this,
                                                          Severity.WARN,
                                                          MsgUtil.createMessage(Msg.DTANALYSIS_HITPOLICY_RECOMMENDER_UNIQUE,
                                                                                nameOrIDOfTable()),
                                                          Msg.DTANALYSIS_HITPOLICY_RECOMMENDER_UNIQUE.getType()));
        } else if (!overlaps.isEmpty()) {
            boolean overlapsShareSameOutput = true;
            for (Overlap ol : overlaps) {
                List<Integer> rules = ol.getRules();
                Set<List<Comparable<?>>> outputsForOverlap = new HashSet<>();
                for (Integer ruleID : rules) {
                    outputsForOverlap.add(ddtaTable.getRule().get(ruleID - 1).getOutputEntry());
                }
                overlapsShareSameOutput &= outputsForOverlap.size() == 1;
            }
            if (overlapsShareSameOutput && sourceDT.getHitPolicy() != HitPolicy.ANY) {
                passThruMessages.add(new DMNDTAnalysisMessage(this,
                                                              sourceDT.getHitPolicy() == HitPolicy.UNIQUE ? Severity.ERROR : Severity.WARN,
                                                              MsgUtil.createMessage(Msg.DTANALYSIS_HITPOLICY_RECOMMENDER_ANY,
                                                                                    nameOrIDOfTable()),
                                                              Msg.DTANALYSIS_HITPOLICY_RECOMMENDER_ANY.getType()));
            } else if (!overlapsShareSameOutput && sourceDT.getHitPolicy() != HitPolicy.PRIORITY) {
                passThruMessages.add(new DMNDTAnalysisMessage(this,
                                                              sourceDT.getHitPolicy() == HitPolicy.FIRST ? Severity.WARN : Severity.ERROR,
                                                              MsgUtil.createMessage(Msg.DTANALYSIS_HITPOLICY_RECOMMENDER_PRIORITY,
                                                                                    nameOrIDOfTable()),
                                                              Msg.DTANALYSIS_HITPOLICY_RECOMMENDER_PRIORITY.getType()));
            }
        }
    }

    public boolean isHitPolicySingle(HitPolicy hp) {
        return hp == HitPolicy.UNIQUE || hp == HitPolicy.ANY || hp == HitPolicy.PRIORITY || hp == HitPolicy.FIRST;
    }

    public String nameOrIDOfTable() {
        return ValidatorUtil.nameOrIDOfTable(sourceDT);
    }

    public void computeOutputInLOV() {
        for (int ruleIdx = 0; ruleIdx < ddtaTable.getRule().size(); ruleIdx++) {
            DDTARule rule = ddtaTable.getRule().get(ruleIdx);
            for (int outputIdx = 0; outputIdx < ddtaTable.getOutputs().size(); outputIdx++) {
                Comparable<?> value = rule.getOutputEntry().get(outputIdx);
                if (value instanceof DDTAOutputEntryExpression) {
                    continue; // we can't say _statically_ if the output expression is in the output LoV; skipping
                }
                DDTAOutputClause outputClause = ddtaTable.getOutputs().get(outputIdx);
                if (outputClause.isDiscreteDomain() && !outputClause.getDiscreteValues().contains(value)) {
                    passThruMessages.add(new DMNDTAnalysisMessage(this,
                                                                  Severity.ERROR,
                                                                  MsgUtil.createMessage(Msg.DTANALYSIS_ERROR_RULE_OUTPUT_OUTSIDE_LOV,
                                                                                        ruleIdx + 1,
                                                                                        value,
                                                                                        outputIdx + 1,
                                                                                        outputClause.getDiscreteValues()),
                                                                  Msg.DTANALYSIS_ERROR_RULE_OUTPUT_OUTSIDE_LOV.getType()));
                }
            }
        }
    }

    public void setMCDCSelectedBlocks(List<PosNegBlock> selectedBlocks) {
        this.selectedBlocks = selectedBlocks;
    }

    public List<PosNegBlock> getMCDCSelectedBlocks() {
        return Collections.unmodifiableList(selectedBlocks);
    }
}

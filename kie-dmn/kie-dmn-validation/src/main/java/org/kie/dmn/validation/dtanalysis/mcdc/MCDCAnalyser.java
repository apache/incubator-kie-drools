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
package org.kie.dmn.validation.dtanalysis.mcdc;

import java.math.BigDecimal;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.HitPolicy;
import org.kie.dmn.validation.dtanalysis.model.Bound;
import org.kie.dmn.validation.dtanalysis.model.DDTAInputEntry;
import org.kie.dmn.validation.dtanalysis.model.DDTARule;
import org.kie.dmn.validation.dtanalysis.model.DDTATable;
import org.kie.dmn.validation.dtanalysis.model.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MCDCAnalyser {

    private static final Logger LOG = LoggerFactory.getLogger(MCDCAnalyser.class);

    private final DDTATable ddtaTable;
    private final DecisionTable dt;

    private Optional<Integer> elseRuleIdx = Optional.empty();
    private List<List<?>> allEnumValues = new ArrayList<>();
    private List<PosNegBlock> selectedBlocks = new ArrayList<>();

    public MCDCAnalyser(DDTATable ddtaTable, DecisionTable dt) {
        this.ddtaTable = ddtaTable;
        this.dt = dt;
    }

    public List<PosNegBlock> compute() {
        if (dt.getHitPolicy() != HitPolicy.UNIQUE && dt.getHitPolicy() != HitPolicy.ANY && dt.getHitPolicy() != HitPolicy.PRIORITY) {
            return Collections.emptyList(); // cannot analyse.
        }
        if (!ddtaTable.getColIDsStringWithoutEnum().isEmpty()) {
            return Collections.emptyList(); // if not enumerated output values, cannot analyse.
        }

        // Step1
        calculateElseRuleIdx();
        calculateAllEnumValues();

        // Step2, 3
        int i = 1;
        while (areInputsYetToBeVisited()) {
            LOG.debug("=== Step23, iteration {}", i);
            step23();
            i++;
        }

        while (!step4whichOutputYetToVisit().isEmpty()) {
            step4();
        }

        LOG.info("The final results are as follows. (marked with R the 'red color' records which are duplicates, changing input is marked with * sign)");
        LOG.info("Left Hand Side for Positive:");
        Set<Record> mcdcRecords = new LinkedHashSet<>();
        // cycle positive side first
        for (PosNegBlock b : selectedBlocks) {
            boolean add = mcdcRecords.add(b.posRecord);
            if (add) {
                LOG.info("+ {}", b.posRecord.toString(b.cMarker));
            } else {
                LOG.info("R {}", b.posRecord.toString(b.cMarker));
            }
        }
        // cycle negative side
        LOG.info("Right Hand Side for Negative:");
        for (PosNegBlock b : selectedBlocks) {
            for (Record negRecord : b.negRecords) {
                boolean add = mcdcRecords.add(negRecord);
                if (add) {
                    LOG.info("- {}", negRecord);
                } else {
                    LOG.info("R {}", negRecord);
                }
            }
            LOG.info(" ");
        }
        LOG.info("total of cases: {}", mcdcRecords.size());
        return selectedBlocks;
    }

    private void step4() {
        Set<List<Comparable<?>>> outYetToVisit = step4whichOutputYetToVisit();
        Optional<List<Comparable<?>>> findFirst = outYetToVisit.stream().findFirst();
        if (findFirst.isEmpty()) {
            throw new IllegalArgumentException("step4 was invoked despite there are no longer output to visit.");
        }
        List<Comparable<?>> pickOutToVisit = findFirst.get();
        List<Integer> rules = new ArrayList<>();
        for (int ruleIdx = 0; ruleIdx < ddtaTable.getRule().size(); ruleIdx++) {
            if (ddtaTable.getRule().get(ruleIdx).getOutputEntry().equals(pickOutToVisit)) {
                rules.add(ruleIdx);
            }
        }
        LOG.trace("rules {}", rules);
        List<Entry<PosNegBlock, Integer>> blocks = new ArrayList<>();
        for (int ruleIdx : rules) {
            List<Object[]> valuesForRule = negBlockValuesForRule(ruleIdx);
            if (valuesForRule.isEmpty()) {
                LOG.debug("step4, while looking for candidate values for rule {} I could NOT re-use from a negative case, computing new set.", ruleIdx);
                Object[] posCandidate = findValuesForRule(ruleIdx, Collections.unmodifiableList(allEnumValues));
                if (Stream.of(posCandidate).anyMatch(x -> x == null)) {
                    throw new IllegalStateException();
                }
                valuesForRule.add(posCandidate);
            }
            for (Object[] posCandidate : valuesForRule) {
                LOG.trace("ruleIdx {} values {}", ruleIdx + 1, posCandidate);
                Record posCandidateRecord = new Record(ruleIdx, posCandidate, ddtaTable.getRule().get(ruleIdx).getOutputEntry());
                for (int chgInput = 0; chgInput < ddtaTable.getInputs().size(); chgInput++) {
                    Optional<PosNegBlock> calculatePosNegBlock = calculatePosNegBlock(chgInput, posCandidate[chgInput], posCandidateRecord, Collections.unmodifiableList(allEnumValues));
                    if (calculatePosNegBlock.isPresent()) {
                        PosNegBlock posNegBlock = calculatePosNegBlock.get();
                        int w = computeAdditionalWeightIntroBlock(posNegBlock);
                        LOG.trace("{} weight: {}", posNegBlock, w);
                        blocks.add(new SimpleEntry<>(posNegBlock, w));
                    }
                }
            }
        }
        Optional<PosNegBlock> posNegBlockFirst = blocks.stream().sorted(Entry.comparingByValue()).map(Entry::getKey).findFirst();
        if (posNegBlockFirst.isEmpty()) {
            throw new IllegalStateException("there is no candidable posNegBlocks.");
        }
        PosNegBlock posNegBlock = posNegBlockFirst.get();
        LOG.trace("step4 selecting block: \n{}", posNegBlock);
        selectBlock(posNegBlock);
    }

    private Set<List<Comparable<?>>> step4whichOutputYetToVisit() {
        Set<List<Comparable<?>>> outYetToVisit = ddtaTable.getRule().stream().map(r -> r.getOutputEntry()).collect(Collectors.toSet());
        outYetToVisit.removeAll(getVisitedPositiveOutput());
        LOG.trace("outYetToVisit {}", outYetToVisit);
        if (outYetToVisit.size() > 1 && elseRuleIdx.isPresent() && outYetToVisit.contains(ddtaTable.getRule().get(elseRuleIdx.get()).getOutputEntry())) {
            LOG.trace("outYetToVisit will be filtered of the Else rule's output {}.", ddtaTable.getRule().get(elseRuleIdx.get()).getOutputEntry());
            outYetToVisit.remove(ddtaTable.getRule().get(elseRuleIdx.get()).getOutputEntry());
            LOG.trace("outYetToVisit {}", outYetToVisit);
        }
        return outYetToVisit;
    }

    private List<Object[]> negBlockValuesForRule(int ruleIdx) {
        List<Object[]> result = new ArrayList<>();
        for (PosNegBlock b : selectedBlocks) {
            for (Record nr : b.negRecords) {
                if (nr.ruleIdx == ruleIdx) {
                    result.add(nr.enums);
                }
            }
        }
        return result;
    }

    private boolean areInputsYetToBeVisited() {
        List<Integer> idx = getAllColumnIndexes();
        idx.removeAll(getAllColumnVisited());
        return idx.size() > 0;
    }

    private void step23() {
        LOG.debug("step23() ------------------------------");
        List<Integer> visitedIndexes = getAllColumnVisited();
        LOG.debug("Visited Inputs: {}", debugListPlusOne(visitedIndexes));
        List<List<Comparable<?>>> visitedPositiveOutput = getVisitedPositiveOutput();
        LOG.debug("Visited positive Outputs: {}", visitedPositiveOutput);
        debugAllEnumValues();

        List<Integer> allIndexes = getAllColumnIndexes();
        allIndexes.removeAll(visitedIndexes);
        LOG.debug("Inputs yet to be analysed: {}", debugListPlusOne(allIndexes));

        List<Integer> idxMoreEnums = whichIndexHasMoreEnums(allIndexes);
        LOG.debug("2.a Pick the input with greatest number of enum values {} ? it's: {}",
                  debugListPlusOne(allIndexes),
                  debugListPlusOne(idxMoreEnums));

        Integer idxMostMatchingRules = idxMoreEnums.stream()
                                                   .map(i -> new SimpleEntry<Integer, Integer>(i, matchingRulesForInput(i, allEnumValues.get(i).get(0)).size()))
                                                   .max(Entry.comparingByValue())
                                                   .map(Entry::getKey)
                                                   .orElseThrow(() -> new RuntimeException());

        LOG.debug("2.b Choose the input with the greatest number of rules matching that enum {} ? it's: {}",
                  idxMoreEnums.stream()
                              .map(i -> new SimpleEntry<Integer, Integer>(i, matchingRulesForInput(i, allEnumValues.get(i).get(0)).size()))
                              .collect(Collectors.toList()),
                  idxMostMatchingRules + 1);

        List<PosNegBlock> candidateBlocks = new ArrayList<>();
        Object value = allEnumValues.get(idxMostMatchingRules).get(0);
        List<Integer> matchingRulesForInput = matchingRulesForInput(idxMostMatchingRules, value);
        for (int ruleIdx : matchingRulesForInput) {
            Object[] knownValues = new Object[ddtaTable.getInputs().size()];
            knownValues[idxMostMatchingRules] = value;
            List<Object[]> valuesForRule = combinatorialValuesForRule(ruleIdx, knownValues, Collections.unmodifiableList(allEnumValues));
            for (Object[] posCandidate : valuesForRule) {
                LOG.trace("ruleIdx {} values {}", ruleIdx + 1, posCandidate);
                if (Stream.of(posCandidate).anyMatch(x -> x == null)) {
                    continue;
                }
                List<Integer> ruleIndexesMatchingValues = ruleIndexesMatchingValues(posCandidate);
                if (!ruleIndexesMatchingValues.remove((Integer) ruleIdx)) {
                    continue; // the posCandidate is actually matching another rule (in priorities)
                }
                if (ruleIndexesMatchingValues.size() > 0) {
                    LOG.debug("Skipping posCandidate {} as it could also match rules {}, besides the one currently under calculus {}", posCandidate, ruleIndexesMatchingValues, ruleIdx);
                    continue;
                }
                Record posCandidateRecord = new Record(ruleIdx, posCandidate, ddtaTable.getRule().get(ruleIdx).getOutputEntry());
                Optional<PosNegBlock> calculatePosNegBlock = calculatePosNegBlock(idxMostMatchingRules, value, posCandidateRecord, Collections.unmodifiableList(allEnumValues));
                if (calculatePosNegBlock.isPresent()) {
                    candidateBlocks.add(calculatePosNegBlock.get());
                }
            }
        }
        LOG.trace("3. Input {}, initial candidate blocks \n{}", idxMostMatchingRules + 1, candidateBlocks);

        Set<List<Comparable<?>>> filter1outs = candidateBlocks.stream().map(b -> b.posRecord.output).collect(Collectors.toSet());
        LOG.trace("filter1outs {}", filter1outs);

        if (filter1outs.stream().anyMatch(not(getVisitedPositiveOutput()::contains))) {
            LOG.trace("Trying to prioritize non-yet visited outputs...");
            Set<List<Comparable<?>>> hypo = new HashSet<>(filter1outs);
            hypo.removeAll(getVisitedPositiveOutput());
            if (elseRuleIdx.isPresent() && hypo.size() == 1 && hypo.iterator().next().equals(ddtaTable.getRule().get(elseRuleIdx.get()).getOutputEntry())) {
                LOG.trace("...won't be prioritizing non-yet visited outputs, otherwise I would prioritize the Else rules.");
            } else {
                filter1outs.removeAll(getVisitedPositiveOutput());
                LOG.trace("I recomputed filter1outs to prioritize non-yet visited outputs {}", filter1outs);
            }
        }
        if (filter1outs.size() > 1 && elseRuleIdx.isPresent() && filter1outs.contains(ddtaTable.getRule().get(elseRuleIdx.get()).getOutputEntry())) {
            LOG.trace("filter1outs will be filtered of the Else rule's output {}.", ddtaTable.getRule().get(elseRuleIdx.get()).getOutputEntry());
            filter1outs.remove(ddtaTable.getRule().get(elseRuleIdx.get()).getOutputEntry());
            LOG.trace("filter1outs {}", filter1outs);
        }

        List<SimpleEntry<List<Comparable<?>>, Integer>> filter1outsMatchingRules = filter1outs.stream()
                                                                                              .map(out -> new SimpleEntry<>(out, (int) ddtaTable.getRule().stream().filter(r -> r.getOutputEntry().equals(out)).count()))
                                                                                              .sorted(Entry.comparingByValue())
                                                                                              .collect(Collectors.toList());
        LOG.trace("3. of those blocks positive outputs, how many rule do they match? {}", filter1outsMatchingRules);
        
        List<Comparable<?>> filter1outWithLessMatchingRules = filter1outsMatchingRules.get(0).getKey();
        LOG.trace("3. positive output of those block with the less matching rules? {}", filter1outWithLessMatchingRules);

        List<PosNegBlock> filter2 = candidateBlocks.stream().filter(b -> b.posRecord.output.equals(filter1outWithLessMatchingRules)).collect(Collectors.toList());
        LOG.trace("3.FILTER-2 blocks with output corresponding to the less matching rules {}, the blocks are: \n{}", filter1outWithLessMatchingRules, filter2);

        List<SimpleEntry<PosNegBlock, Integer>> blockWeighted = filter2.stream()
                                                                       .map(b -> new SimpleEntry<>(b, computeAdditionalWeightIntroBlock(b)))
                                                                       .sorted(Entry.comparingByValue())
                                                                       .collect(Collectors.toList());
        LOG.trace("3. blocks sorted by fewest new cases (natural weight order): \n{}", blockWeighted);

        PosNegBlock selectedBlock = blockWeighted.get(0).getKey();
        LOG.trace("3. I select the first, chosen block to be select: \n{}", selectedBlock);
        selectBlock(selectedBlock);
    }

    /**
     * JDK-11 polyfill 
     */
    public static <T> Predicate<T> not(Predicate<T> t) {
        return t.negate();
    }

    private int computeAdditionalWeightIntroBlock(PosNegBlock newBlock) {
        int score = 0;
        Record posRecord = newBlock.posRecord;
        if (selectedBlocks.stream().noneMatch(vb -> vb.posRecord.equals(posRecord))) {
            score++;
        }
        for (Record negRecord : newBlock.negRecords) {
            if (selectedBlocks.stream().flatMap(vb -> vb.negRecords.stream()).noneMatch(nr -> nr.equals(negRecord))) {
                score++;
            }
        }
        return score;
    }

    private void selectBlock(PosNegBlock selected) {
        selectedBlocks.add(selected);
        //        int index = selected.cMarker;
        //        allEnumValues.get(index).removeAll(Arrays.asList(selected.posRecord.enums[index]));
    }

    private List<List<Comparable<?>>> getVisitedPositiveOutput() {
        return selectedBlocks.stream().map(b -> b.posRecord.output).collect(Collectors.toList());
    }

    private List<Integer> getAllColumnVisited() {
        return selectedBlocks.stream().map(b -> b.cMarker).collect(Collectors.toList());
    }

    private List<Integer> getAllColumnIndexes() {
        return IntStream.range(0, ddtaTable.getInputs().size()).boxed().collect(Collectors.toList());
    }

    private List<Integer> debugListPlusOne(List<Integer> input) {
        return input.stream().map(x -> x + 1).collect(Collectors.toList());
    }

    private List<Integer> whichIndexHasMoreEnums(List<Integer> allIndexes) {
        Map<Integer, List<?>> byIndex = new HashMap<>();
        for (Integer idx : allIndexes) {
            byIndex.put(idx, allEnumValues.get(idx));
        }
        Integer max = byIndex.values().stream().map(List::size).max(Integer::compareTo).orElse(0);
        List<Integer> collect = byIndex.entrySet().stream().filter(kv -> kv.getValue().size() == max).map(Entry::getKey).collect(Collectors.toList());
        return collect;
    }

    private Optional<PosNegBlock> calculatePosNegBlock(Integer idx, Object value, Record posCandidate, List<List<?>> allEnumValues) {
        List<Comparable<?>> posOutput = posCandidate.output;
        List<?> enumValues = allEnumValues.get(idx);
        List<?> allOtherEnumValues = new ArrayList<>(enumValues);
        allOtherEnumValues.remove(value);
        List<Record> negativeRecords = new ArrayList<>();
        for (Object otherEnumValue : allOtherEnumValues) {
            Object[] negCandidate = Arrays.copyOf(posCandidate.enums, posCandidate.enums.length);
            negCandidate[idx] = otherEnumValue;
            Record negRecordForNegCandidate = null;
            for (int i = 0; negRecordForNegCandidate == null && i < ddtaTable.getRule().size(); i++) {
                DDTARule rule = ddtaTable.getRule().get(i);
                boolean ruleMatches = ruleMatches(rule, negCandidate);
                if (ruleMatches) {
                    negRecordForNegCandidate = new Record(i, negCandidate, rule.getOutputEntry());
                }
            }
            if (negRecordForNegCandidate != null) {
                negativeRecords.add(negRecordForNegCandidate);
            }
        }
        boolean allNegValuesDiffer = true;
        for (Record record : negativeRecords) {
            allNegValuesDiffer &= !record.output.equals(posOutput);
        }
        if (allNegValuesDiffer) {
            PosNegBlock posNegBlock = new PosNegBlock(idx, posCandidate, negativeRecords);
            return Optional.of(posNegBlock);
        }
        LOG.trace("For In{}={} and candidate positive of {}, it cannot be a matching rule because some negative case had SAME output {}", idx + 1, value, posCandidate, negativeRecords);
        return Optional.empty();
    }

    private static boolean ruleMatches(DDTARule rule, Object[] values) {
        boolean ruleMatches = true;
        for (int c = 0; ruleMatches && c < rule.getInputEntry().size(); c++) {
            Object cValue = values[c];
            ruleMatches &= rule.getInputEntry().get(c).getIntervals().stream().anyMatch(interval -> interval.asRangeIncludes(cValue));
        }
        return ruleMatches;
    }

    private List<Integer> ruleIndexesMatchingValues(Object[] values) {
        List<Integer> ruleIndexes = new ArrayList<>();
        for (int i = 0; i < ddtaTable.getRule().size(); i++) {
            DDTARule rule = ddtaTable.getRule().get(i);
            if (ruleMatches(rule, values)) {
                ruleIndexes.add(i);
            }
        }
        if (dt.getHitPolicy() == HitPolicy.PRIORITY) {
            List<List<Comparable<?>>> outputs = new ArrayList<>();
            for (Integer ruleIdx : ruleIndexes) {
                DDTARule rule = ddtaTable.getRule().get(ruleIdx);
                List<Comparable<?>> ruleOutput = rule.getOutputEntry();
                outputs.add(ruleOutput);
            }
            List<Comparable<?>> computedOutput = new ArrayList<>();
            for (int i = 0; i < ddtaTable.getOutputs().size(); i++) {
                List outputOrder = ddtaTable.getOutputs().get(i).getOutputOrder();
                int outputCursor = Integer.MAX_VALUE;
                for (List<Comparable<?>> outs : outputs) {
                    Comparable<?> out = outs.get(i);
                    if (outputOrder.indexOf(out) < outputCursor) {
                        outputCursor = outputOrder.indexOf(out);
                    }
                }
                computedOutput.add((Comparable<?>) outputOrder.get(outputCursor));
            }
            List<Integer> pIndexes = new ArrayList<>();
            for (Integer ruleIdx : ruleIndexes) {
                DDTARule rule = ddtaTable.getRule().get(ruleIdx);
                List<Comparable<?>> ruleOutput = rule.getOutputEntry();
                if (ruleOutput.equals(computedOutput)) {
                    pIndexes.add(ruleIdx);
                }
            }
            return pIndexes;
        }
        return ruleIndexes;
    }

    public static class PosNegBlock {

        public final int cMarker;
        public final Record posRecord;
        public final List<Record> negRecords;

        public PosNegBlock(int cMarker, Record posRecord, List<Record> negRecords) {
            this.cMarker = cMarker;
            this.posRecord = posRecord;
            this.negRecords = negRecords;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("PosNeg block In ").append(cMarker + 1).append("=").append(posRecord.enums[cMarker]).append("\n");
            sb.append(" + ").append(posRecord).append("\n");
            for (Record negRecord : negRecords) {
                sb.append(" - ").append(negRecord).append("\n");
            }
            return sb.toString();
        }
    }

    public static class Record {

        public final int ruleIdx;
        public final Object[] enums;
        public final List<Comparable<?>> output;

        public Record(int ruleIdx, Object[] enums, List<Comparable<?>> output) {
            this.ruleIdx = ruleIdx;
            this.enums = enums;
            this.output = output;
        }

        @Override
        public String toString() {
            return String.format("%2s", ruleIdx + 1) + " [" + Arrays.stream(enums).map(Object::toString).collect(Collectors.joining("; ")) + "] -> " + output;
        }

        public String toString(int cMarker) {
            StringBuilder ts = new StringBuilder(String.format("%2s", ruleIdx + 1));
            ts.append(" [");
            for (int i = 0; i < enums.length; i++) {
                if (i == cMarker) {
                    ts.append("*");
                }
                ts.append(enums[i]);
                if (i + 1 < enums.length) {
                    ts.append("; ");
                }
            }
            ts.append("] -> ").append(output);
            return ts.toString();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.deepHashCode(enums);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Record other = (Record) obj;
            if (!Arrays.deepEquals(enums, other.enums))
                return false;
            return true;
        }

    }

    private List<Object[]> combinatorialValuesForRule(int ruleIdx, Object[] knownValues, List<List<?>> allEnumValues) {
        List<Object[]> result = new ArrayList<>();
        List<DDTAInputEntry> inputEntry = ddtaTable.getRule().get(ruleIdx).getInputEntry();
        List<List<?>> validEnumValues = new ArrayList<>();
        for (int i = 0; i < inputEntry.size(); i++) {
            List<Object> enumForI = new ArrayList<>();
            if (knownValues[i] == null) {
                DDTAInputEntry ddtaInputEntry = inputEntry.get(i);
                List<?> enumValues = allEnumValues.get(i);
                for (Object object : enumValues) {
                    if (ddtaInputEntry.getIntervals().stream().anyMatch(interval -> interval.asRangeIncludes(object))) {
                        enumForI.add(object);
                    }
                }
            } else {
                enumForI.add(knownValues[i]);
            }
            validEnumValues.add(enumForI);
        }

        List<List<Object>> combinatorial = new ArrayList<>();
        combinatorial.add(new ArrayList<>());
        for (int i = 0; i < inputEntry.size(); i++) {
            List<List<Object>> combining = new ArrayList<>();
            for (List<Object> existing : combinatorial) {
                for (Object enumForI : validEnumValues.get(i)) {
                    List<Object> building = new ArrayList<>(existing);
                    building.add(enumForI);
                    combining.add(building);
                }
            }
            combinatorial = combining;
        }
        return combinatorial.stream().map(List::toArray).collect(Collectors.toList());
    }

    private Object[] findValuesForRule(int ruleIdx, Object[] knownValues, List<List<?>> allEnumValues) {
        Object[] result = Arrays.copyOf(knownValues, knownValues.length);
        List<DDTAInputEntry> inputEntry = ddtaTable.getRule().get(ruleIdx).getInputEntry();
        for (int i = 0; i < inputEntry.size(); i++) {
            if (result[i] == null) {
                DDTAInputEntry ddtaInputEntry = inputEntry.get(i);
                List<?> enumValues = allEnumValues.get(i);
                Interval interval0 = ddtaInputEntry.getIntervals().get(0);
                if (interval0.isSingularity()) {
                    result[i] = interval0.getLowerBound().getValue();
                } else if (interval0.getLowerBound().getBoundaryType() == RangeBoundary.CLOSED && interval0.getLowerBound().getValue() != Interval.NEG_INF) {
                    result[i] = interval0.getLowerBound().getValue();
                } else if (interval0.getUpperBound().getBoundaryType() == RangeBoundary.CLOSED && interval0.getUpperBound().getValue() != Interval.POS_INF) {
                    result[i] = interval0.getUpperBound().getValue();
                }

                if (!enumValues.contains(result[i])) {
                    result[i] = null; // invalidating if the chosen enum is not part of the plausible ones
                }

                if (result[i] == null) {
                    for (Object object : enumValues) {
                        if (ddtaInputEntry.getIntervals().stream().anyMatch(interval -> interval.asRangeIncludes(object))) {
                            result[i]= object;
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    private Object[] findValuesForRule(int ruleIdx, List<List<?>> allEnumValues) {
        Object[] result = new Object[ddtaTable.getInputs().size()];
        List<DDTAInputEntry> inputEntry = ddtaTable.getRule().get(ruleIdx).getInputEntry();
        for (int i = 0; i < inputEntry.size(); i++) {
            if (result[i] == null) {
                DDTAInputEntry ddtaInputEntry = inputEntry.get(i);
                List<?> enumValues = allEnumValues.get(i);

                for (Object object : enumValues) {
                    if (ddtaInputEntry.getIntervals().stream().anyMatch(interval -> interval.asRangeIncludes(object))) {
                        result[i] = object;
                        break;
                    }
                }

            }
        }
        return result;
    }

    private List<Integer> matchingRulesForInput(int colIdx, Object value) {
        List<Integer> results = new ArrayList<>();
        List<DDTARule> rules = ddtaTable.getRule();
        for (int i = 0; i < rules.size(); i++) {
            List<Interval> intervals = rules.get(i).getInputEntry().get(colIdx).getIntervals();
            if (intervals.stream().anyMatch(interval -> interval.asRangeIncludes(value))) {
                results.add(i);
            }
        }
        LOG.trace("Considering just In{}={} in the original decision tables matches rules: {} total of {} rules.", colIdx + 1, value, debugListPlusOne(results), results.size());
        return results;
    }

    private void calculateAllEnumValues() {
        for (int idx = 0; idx < ddtaTable.inputCols(); idx++) {
            if (ddtaTable.getInputs().get(idx).isDiscreteDomain()) {
                List<?> discreteValues = new ArrayList<>(ddtaTable.getInputs().get(idx).getDiscreteDMNOrder());
                allEnumValues.add(discreteValues); // add _the collection_
                continue;
            }
            List<Interval> colIntervals = ddtaTable.projectOnColumnIdx(idx);
            List<Bound> bounds = colIntervals.stream().flatMap(i -> Stream.of(i.getLowerBound(), i.getUpperBound())).collect(Collectors.toList());
            Collections.sort(bounds);
            LOG.trace("bounds (sorted) {}", bounds);

            List<Object> enumValues = new ArrayList<>();

            Bound<?> prevBound = bounds.remove(0);
            while (bounds.size() > 0 && bounds.get(0).compareTo(prevBound) == 0) {
                prevBound = bounds.remove(0); //look-ahead.
            }
            while (bounds.size() > 0) {
                Bound<?> curBound = bounds.remove(0);
                while (bounds.size() > 0 && bounds.get(0).compareTo(curBound) == 0) {
                    curBound = bounds.remove(0); //look-ahead.
                }
                
                LOG.trace("prev {}, cur {}", prevBound, curBound);
                if (prevBound.isUpperBound() && curBound.isLowerBound()) {
                    // do nothing.
                } else if (prevBound.isUpperBound() && curBound.isUpperBound()) {
                    if (curBound.getBoundaryType() == RangeBoundary.CLOSED && !isBoundInfinity(curBound)) {
                        enumValues.add(curBound.getValue());
                    } else {
                        LOG.trace("looking for value in-between {} {} ", prevBound, curBound);
                        enumValues.add(inBetween(prevBound, curBound));
                    }
                } else if (prevBound.isLowerBound() && curBound.isLowerBound()) {
                    if (prevBound.getBoundaryType() == RangeBoundary.CLOSED && !isBoundInfinity(prevBound)) {
                        enumValues.add(prevBound.getValue());
                    } else {
                        LOG.trace("looking for value in-between {} {} ", prevBound, curBound);
                        enumValues.add(inBetween(prevBound, curBound));
                    }
                } else {
                    if (prevBound.getBoundaryType() == RangeBoundary.CLOSED && !isBoundInfinity(prevBound)) {
                        enumValues.add(prevBound.getValue());
                    } else if (curBound.getBoundaryType() == RangeBoundary.CLOSED && !isBoundInfinity(curBound)) {
                        enumValues.add(curBound.getValue());
                    } else {
                        LOG.trace("looking for value in-between {} {} ", prevBound, curBound);
                        enumValues.add(inBetween(prevBound, curBound));
                    }
                }

                prevBound = curBound;
                while (bounds.size() > 0 && bounds.get(0).compareTo(prevBound) == 0) {
                    prevBound = bounds.remove(0); //look-ahead.
                }
            }

            LOG.trace("enumValues: {}", enumValues);
            allEnumValues.add(enumValues);
        }

        for (int in=0; in<allEnumValues.size(); in++) {
            final int inIdx = in;
            final List<?> inX = allEnumValues.get(in);
            List<Pair> sorted = inX.stream().map(v -> new Pair((Comparable<?>) v, matchingRulesForInput(inIdx, v).size()))
                                   .sorted()
                                   .collect(Collectors.toList());
            LOG.debug("Input {} sorted by number of matching rules: {}", inIdx + 1, sorted);
            List<?> sortedByMatchingRules = sorted.stream()
                                                  .map(Pair::getKey)
                                                  .collect(Collectors.toList());
            allEnumValues.set(inIdx, sortedByMatchingRules);
        }

        debugAllEnumValues();
    }
    
    public static class Pair implements Comparable<Pair> {

        private final Comparable key;
        private final int occurences;
        private final Comparator<Pair> c1 = Comparator.comparing(Pair::getOccurences).reversed();
        
        public Pair(Comparable<?> key, int occurences) {
            this.key = key;
            this.occurences = occurences;
        }

        public Comparable<?> getKey() {
            return key;
        }

        public int getOccurences() {
            return occurences;
        }

        @Override
        public int compareTo(Pair o) {
            if (c1.compare(this, o) != 0) {
                return c1.compare(this, o);
            } else {
                return -1 * this.key.compareTo(o.key);
            }
        }

        @Override
        public String toString() {
            return "{" + key + "=" + occurences + "}";
        }

    }

    private void debugAllEnumValues() {
        LOG.debug("allEnumValues:");
        for (int idx = 0; idx < allEnumValues.size(); idx++) {
            LOG.debug("allEnumValues In{}= {}", idx + 1, allEnumValues.get(idx));
        }
    }

    private void calculateElseRuleIdx() {
        if (dt.getHitPolicy() == HitPolicy.PRIORITY) {// calculate "else" rule if present.
            for (int ruleIdx = ddtaTable.getRule().size() - 1; ruleIdx>=0 && elseRuleIdx.isEmpty(); ruleIdx--) {
                DDTARule rule = ddtaTable.getRule().get(ruleIdx);
                List<DDTAInputEntry> ie = rule.getInputEntry();
                boolean checkAll = true;
                for (int colIdx = 0; colIdx < ie.size() && checkAll; colIdx++) {
                    DDTAInputEntry ieIDX = ie.get(colIdx);
                    boolean idIDXsize1 = ieIDX.getIntervals().size() == 1;
                    Interval ieIDXint0 = ieIDX.getIntervals().get(0);
                    Interval domainMinMax = ddtaTable.getInputs().get(colIdx).getDomainMinMax();
                    boolean equals = ieIDXint0.equals(domainMinMax);
                    checkAll &= idIDXsize1 && equals;
                }
                if (checkAll) {
                    LOG.debug("I believe P table with else rule: {}", ruleIdx + 1);
                    elseRuleIdx = Optional.of(ruleIdx);
                }
            }
        }
    }

    private Object inBetween(Bound a, Bound b) {
        if (a.getValue() instanceof BigDecimal || b.getValue() instanceof BigDecimal) {
            BigDecimal aValue = a.getValue() == Interval.NEG_INF ? ((BigDecimal) b.getValue()).add(new BigDecimal(-2)) : (BigDecimal) a.getValue();
            BigDecimal bValue = b.getValue() == Interval.POS_INF ? ((BigDecimal) a.getValue()).add(new BigDecimal(+2)) : (BigDecimal) b.getValue();
            BigDecimal guessWork = new BigDecimal(aValue.intValue() + 1);
            if (bValue.compareTo(guessWork) > 0) {
                return guessWork;
            } else if (bValue.compareTo(guessWork) == 0 && b.isLowerBound() && b.getBoundaryType() == RangeBoundary.OPEN) {
                return guessWork;
            } else {
                throw new UnsupportedOperationException();
            }
        }
        throw new UnsupportedOperationException();
    }

    private boolean isBoundInfinity(Bound b) {
        return b.getValue() == Interval.NEG_INF || b.getValue() == Interval.POS_INF;
    }
}

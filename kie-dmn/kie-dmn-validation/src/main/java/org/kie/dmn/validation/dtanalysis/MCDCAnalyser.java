package org.kie.dmn.validation.dtanalysis;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
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

    public void compute() {
        if (dt.getHitPolicy() != HitPolicy.UNIQUE && dt.getHitPolicy() != HitPolicy.ANY && dt.getHitPolicy() != HitPolicy.PRIORITY) {
            return; // cannot analyse.
        }
        // TODO if not enumerated output values, cannot analyse.

        // Step1
        calculateElseRuleIdx();
        calculateAllEnumValues();

        // Step2
        int i = 1;
        while (areInputsYetToBeVisited()) {
            LOG.debug("=== Step2, iteration {}", i);
            step2();
            i++;
        }

        LOG.info("the final results are as follows.\nLeft Hand Side for Positive:");
        Set<Record> mcdcRecords = new LinkedHashSet<>();
        // cycle positive side first
        for (PosNegBlock b : selectedBlocks) {
            boolean add = mcdcRecords.add(b.posRecord);
            if (add) {
                LOG.info("+ {}", b.posRecord);
            } else {
                throw new UnsupportedOperationException("A positive record was not added to the mcdc");
            }
        }
        // cycle negative side
        LOG.info("Right Hand Side for Negative: (marked with R the 'red color' records which are duplicates)");
        for (PosNegBlock b : selectedBlocks) {
            for (Record negRecord : b.negRecords) {
                boolean add = mcdcRecords.add(negRecord);
                if (add) {
                    LOG.info("- {}", negRecord);
                } else {
                    LOG.info("R {}", negRecord);
                }
            }
            LOG.info("");
        }
    }

    private boolean areInputsYetToBeVisited() {
        List<Integer> idx = getAllColumnIndexes();
        idx.removeAll(getAllColumnVisited());
        return idx.size() > 0;
    }

    private void step2() {
        List<Integer> visitedIndexes = getAllColumnVisited();
        LOG.debug("Visited Inputs: {}", debugListPlusOne(visitedIndexes));
        List<List<Comparable<?>>> visitedPositiveOutput = getVisitedPositiveOutput();
        LOG.debug("Visited positive Outputs: {}", visitedPositiveOutput);
        debugAllEnumValues();

        List<Integer> allIndexes = getAllColumnIndexes();
        allIndexes.removeAll(visitedIndexes);
        LOG.debug("Inputs yet to be analysed: {}", debugListPlusOne(allIndexes));

        List<Integer> idxMoreEnums = whichIndexHasMoreEnums(allIndexes);
        LOG.debug("Which Input with greatest number of enum between {} ? it's: {}",
                  debugListPlusOne(allIndexes),
                  debugListPlusOne(idxMoreEnums));

        List<Entry<Integer, List<PosNegBlock>>> idxFewestRulesMatching = whichIndexHasFewestNumberOfRulesMatchingTheFirstInputValue(idxMoreEnums);
        LOG.debug("Which Input has fewest number of rules matching the first input enum value, between Inputs {} ? it's: {}",
                  debugListPlusOne(idxMoreEnums),
                  debugListPlusOne(idxFewestRulesMatching.stream().map(Entry::getKey).collect(Collectors.toList())));
        if (idxFewestRulesMatching.size() == 1) {
            Entry<Integer, List<PosNegBlock>> idx0blocks = idxFewestRulesMatching.get(0);
            Integer index = idx0blocks.getKey();
            StringBuilder sb = new StringBuilder("\n");
            for (PosNegBlock b : idx0blocks.getValue()) {
                sb.append(b);
            }
            LOG.debug("Only a single Input has fewest rule matching: In{}, and with these blocks {}", index + 1, sb.toString());

            List<PosNegBlock> filtered = new ArrayList<>(idx0blocks.getValue());
            filtered.removeIf(block -> selectedBlocks.stream().anyMatch(sbb -> sbb.posRecord.output.equals(block.posRecord.output)));
            LOG.debug("For step2 I am considering only {} of those {} blocks, as others are matching for positive case output which is already visited.", filtered.size(), idx0blocks.getValue().size());
            LOG.trace("{}", filtered);

            Optional<PosNegBlock> check2bi = check2bi(filtered);
            if (check2bi.isPresent()) {
                PosNegBlock b = check2bi.get();
                // When 2bi is satisfied and all the negative cases has output values different from the prior positive cases, ...
                boolean allNegOutputDiff = !b.negRecords.stream()
                                                        .map(nr -> nr.output)
                                                        .anyMatch(nro -> selectedBlocks.stream()
                                                                                       .map(pr -> pr.posRecord.output)
                                                                                       .anyMatch(pro -> pro.equals(nro)));
                List<PosNegBlock> appended = new ArrayList<>();
                if (allNegOutputDiff) {
                    LOG.debug("Acting on 2bi is satisfied and all the negative cases has output values different from the prior positive cases, ...");
                    for (Record negRecord : b.negRecords) {
                        Optional<PosNegBlock> negIntoPositive = calculatePosNegBlock(b.cMarker, negRecord.enums[b.cMarker], negRecord, allEnumValues);
                        if (negIntoPositive.isPresent()) {
                            LOG.debug("negative into positive block: \n{}", negIntoPositive.get());
                            appended.add(negIntoPositive.get());
                        } else {
                            throw new UnsupportedOperationException("I was unable to transform a negative into a positive");
                        }
                    }
                }
                selectBlock(b);
                selectedBlocks.addAll(appended); // appended last to avoid cutting the enumValue.
                return;
            }

            Optional<PosNegBlock> check2bii = check2bii(filtered);
            if (check2bii.isPresent()) {
                PosNegBlock b = check2bii.get();
                selectBlock(b);
                return;
            }

            Optional<Check2biii_iv> check2biii_iv = check2biii_iv(filtered);
            if (check2biii_iv.isPresent()) {
                PosNegBlock selected = check2biii_iv.get().block;
                selectBlock(selected);
                return;
            }

            PosNegBlock arbitrarySelectFirst = filtered.get(0);
            LOG.warn("I have reached the end of step2 without having selected a block, will select the first one \n{}", arbitrarySelectFirst);
            selectBlock(arbitrarySelectFirst);
            return;
        } else {
            throw new UnsupportedOperationException("TODO");
        }
    }

    private void selectBlock(PosNegBlock selected) {
        selectedBlocks.add(selected);
        //        int index = selected.cMarker;
        //        allEnumValues.get(index).removeAll(Arrays.asList(selected.posRecord.enums[index]));
    }

    public static class Check2biii_iv {

        public final int numOfDifferent;
        public final PosNegBlock block;

        public Check2biii_iv(int numOfDifferent, PosNegBlock block) {
            this.numOfDifferent = numOfDifferent;
            this.block = block;
        }

    }

    private Optional<Check2biii_iv> check2biii_iv(List<PosNegBlock> value) {
        Check2biii_iv candidate = new Check2biii_iv(0, null);
        for (PosNegBlock b : value) {
            Set<List<Comparable<?>>> bNegOutput = b.negRecords.stream().map(nr -> nr.output).collect(Collectors.toSet());
            bNegOutput.removeAll(getVisitedPositiveOutput());
            if (bNegOutput.size() > candidate.numOfDifferent) {
                candidate = new Check2biii_iv(bNegOutput.size(), b);
            }
        }
        LOG.debug("check2biii_iv candidate: \n{}", candidate.block);
        if (candidate.block != null) {
            return Optional.of(candidate);
        } else {
            return Optional.empty();
        }
    }

    private Optional<PosNegBlock> check2bii(List<PosNegBlock> value) {
        for (PosNegBlock b : value) {
            List<Record> negRecords = b.negRecords;
            boolean anyMatch = negRecords.stream().anyMatch(nr -> selectedBlocks.stream().map(sb -> sb.posRecord).anyMatch(pr -> pr.enums.equals(nr.enums)));
            if (anyMatch) {
                LOG.debug("check2bii identified; one negative case is a duplicate of a prior (positive) case; this happens for in the block. \n{}", b);
                return Optional.of(b);
            }
        }
        LOG.debug("None check2bii.");
        return Optional.empty();
    }

    private Optional<PosNegBlock> check2bi(List<PosNegBlock> value) {
        for (PosNegBlock b : value) {
            Set<List<Comparable<?>>> negOutputSet = b.negRecords.stream().map(neg -> neg.output).collect(Collectors.toSet());
            if (negOutputSet.size() > 1 && negOutputSet.size() == b.negRecords.size()) {
                LOG.debug("check2bi identified; outputs {} of the block are all different and they are more than one. \n{}", negOutputSet, b);
                return Optional.of(b);
            }
        }
        LOG.debug("None check2bi.");
        return Optional.empty();
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

    private List<Entry<Integer, List<PosNegBlock>>> whichIndexHasFewestNumberOfRulesMatchingTheFirstInputValue(List<Integer> indexesWithMoreElements) {
        Map<Integer, List<PosNegBlock>> results = new HashMap<>();
        indexesWithMoreElements.forEach(i -> results.put(i, new ArrayList<>()));
        for (Integer idx : indexesWithMoreElements) {
            Object value = allEnumValues.get(idx).get(0);
            List<Integer> matchingRulesForInput = matchingRulesForInput(idx, value);
            for (int ruleIdx : matchingRulesForInput) {
                Object[] knownValues = new Object[ddtaTable.getInputs().size()];
                knownValues[idx] = value;
                Object[] posCandidate = findValuesForRule(ruleIdx, knownValues, Collections.unmodifiableList(allEnumValues));
                if (Stream.of(posCandidate).anyMatch(x -> x == null)) {
                    continue;
                }
                Record posCandidateRecord = new Record(ruleIdx, posCandidate, ddtaTable.getRule().get(ruleIdx).getOutputEntry());
                Optional<PosNegBlock> calculatePosNegBlock = calculatePosNegBlock(idx, value, posCandidateRecord, Collections.unmodifiableList(allEnumValues));
                if (calculatePosNegBlock.isPresent()) {
                    results.get(idx).add(calculatePosNegBlock.get());
                }
            }
        }

        for (Entry<Integer, List<PosNegBlock>> e : results.entrySet()) {
            LOG.trace("Input {}, has the following number of rules matching the first input value \n{}", e.getKey() + 1, e.getValue());
        }

        Integer min = results.values().stream().map(List::size).min(Integer::compareTo).orElse(0);
        List<Entry<Integer, List<PosNegBlock>>> collect = results.entrySet().stream().filter(kv -> kv.getValue().size() == min).collect(Collectors.toList());
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

    private List<Integer> indexesWithMoreElements(List<List<?>> cc) {
        Integer max = cc.stream().map(List::size).max(Integer::compareTo).orElse(0);
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < cc.size(); i++) {
            if (cc.get(i).size() == max) {
                indexes.add(i);
            }
        }
        return indexes;
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
                
                LOG.trace("prev {} {}, cur {} {}", prevBound, null, curBound, null);
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
            for (int ruleIdx = ddtaTable.getRule().size() - 1; ruleIdx>=0 && !elseRuleIdx.isPresent(); ruleIdx--) {
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

    private boolean isBoundOnElseRule(Bound b) {
        return elseRuleIdx.orElse(-1).equals(b.getParent().getRule() - 1);
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

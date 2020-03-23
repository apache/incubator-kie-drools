package org.kie.dmn.validation.dtanalysis;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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

    public MCDCAnalyser(DDTATable ddtaTable, DecisionTable dt) {
        this.ddtaTable = ddtaTable;
        this.dt = dt;
    }

    public void compute() {
        if (dt.getHitPolicy() != HitPolicy.UNIQUE && dt.getHitPolicy() != HitPolicy.ANY && dt.getHitPolicy() != HitPolicy.PRIORITY) {
            return; // cannot analyse.
        }
        // TODO if not enumerated output values, cannot analyse.

        calculateElseRuleIdx();
        calculateAllEnumValues();
        List<Integer> indexesWithMoreElements = indexesWithMoreElements(allEnumValues);
        for (Integer idx : indexesWithMoreElements) {
            System.out.println(idx);
            Object value = allEnumValues.get(idx).get(0);
            System.out.println(value);
            List<Integer> matchingRulesForInput = matchingRulesForInput(idx, value);
            System.out.println(matchingRulesForInput);
            for (int ruleIdx : matchingRulesForInput) {
                Object[] knownValues = new Object[ddtaTable.getInputs().size()];
                knownValues[idx] = value;
                Object[] posCandidate = findValuesForRule(ruleIdx, knownValues, Collections.unmodifiableList(allEnumValues));
                Record posCandidateRecord = new Record(ruleIdx, posCandidate, ddtaTable.getRule().get(ruleIdx).getOutputEntry());
                calculatePosNegBlock(idx, value, posCandidateRecord, Collections.unmodifiableList(allEnumValues));
            }
        }
    }

    private void calculatePosNegBlock(Integer idx, Object value, Record posCandidate, List<List<?>> allEnumValues) {
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
            System.out.println(posNegBlock);
        }
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
            StringBuilder sb = new StringBuilder("PosNeg block column index: ").append(cMarker).append("\n");
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
            return String.format("%2s", ruleIdx) + " [" + Arrays.stream(enums).map(Object::toString).collect(Collectors.joining("; ")) + "] -> " + output;
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
                } else {
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
        LOG.trace("matchingRulesForInput column index {} value {} matching rules: {}", colIdx, value, results);
        return results;
    }

    private void calculateAllEnumValues() {
        for (int idx = 0; idx < ddtaTable.inputCols(); idx++) {
            if (ddtaTable.getInputs().get(idx).isDiscreteDomain()) {
                allEnumValues.add(ddtaTable.getInputs().get(idx).getDiscreteValues()); // add _the collection_
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

        if (elseRuleIdx.isPresent()) {
            for (int idx = 0; idx < allEnumValues.size(); idx++) {
                List<Integer> rulesMatchingLast = matchingRulesForInput(idx, allEnumValues.get(idx).get(allEnumValues.get(idx).size() - 1));
                List<Integer> rulesMatchingFirst = matchingRulesForInput(idx, allEnumValues.get(idx).get(0));
                if (rulesMatchingLast.size() == 1 && elseRuleIdx.get().equals(rulesMatchingLast.get(0)) && !rulesMatchingFirst.stream().allMatch(elseRuleIdx.get()::equals)) {
                    // do nothing, already ordered with else rule last.
                } else if (rulesMatchingFirst.size() == 1 && elseRuleIdx.get().equals(rulesMatchingFirst.get(0)) && !rulesMatchingLast.stream().allMatch(elseRuleIdx.get()::equals)) {
                    List<?> reversing = new ArrayList<>(allEnumValues.get(idx));
                    Collections.reverse(reversing);
                    allEnumValues.set(idx, reversing);
                } else {
                    throw new UnsupportedOperationException("TODO");
                }
            }
        }

        LOG.debug("allEnumValues:");
        for (int idx = 0; idx < allEnumValues.size(); idx++) {
            LOG.debug("allEnumValues {}: {}", idx, allEnumValues.get(idx));
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
                    LOG.debug("I believe P table with else rule: {}", ruleIdx);
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

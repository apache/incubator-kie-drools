package org.kie.dmn.validation.dtanalysis;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    public MCDCAnalyser(DDTATable ddtaTable, DecisionTable dt) {
        this.ddtaTable = ddtaTable;
        this.dt = dt;
    }

    public void compute() {
        if (dt.getHitPolicy() != HitPolicy.UNIQUE && dt.getHitPolicy() != HitPolicy.ANY && dt.getHitPolicy() != HitPolicy.PRIORITY) {
            return; // cannot analyse.
        }
        // TODO if not enumerated output values, cannot analyse.

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

        List<List<?>> allEnumValues = new ArrayList<>();
        for (int idx = 0; idx < ddtaTable.inputCols(); idx++) {
            List<Interval> colIntervals = ddtaTable.projectOnColumnIdx(idx);
            List<Bound> bounds = colIntervals.stream().flatMap(i -> Stream.of(i.getLowerBound(), i.getUpperBound())).collect(Collectors.toList());
            Collections.sort(bounds);
            LOG.debug("bounds (sorted) {}", bounds);

            ColEnumValues enumValues = new ColEnumValues();
            Set<Integer> seenRules = new HashSet<>();

            Set<Integer> ofPrev = new HashSet<>();
            Set<Integer> ofCur = new HashSet<>();

            Bound<?> prevBound = bounds.remove(0);
            boolean prevBoundIsElse = isBoundOnElseRule(prevBound);
            if (prevBound.isLowerBound()) {
                addToActive(prevBound, ofPrev);
            }
            while (bounds.size() > 0 && bounds.get(0).compareTo(prevBound) == 0) {
                prevBound = bounds.remove(0); //look-ahead.
                prevBoundIsElse &= isBoundOnElseRule(prevBound);
                if (prevBound.isLowerBound()) {
                    addToActive(prevBound, ofPrev);
                }
            }
            while (bounds.size() > 0) {
                Bound<?> curBound = bounds.remove(0);
                boolean curBoundIsElse = isBoundOnElseRule(curBound);
                if (curBound.isUpperBound()) {
                    addToActive(curBound, ofCur);
                }
                while (bounds.size() > 0 && bounds.get(0).compareTo(curBound) == 0) {
                    curBound = bounds.remove(0); //look-ahead.
                    curBoundIsElse &= isBoundOnElseRule(curBound);
                    if (curBound.isUpperBound()) {
                        addToActive(curBound, ofCur);
                    }
                }
                
                LOG.debug("prev {} {}, cur {} {}", prevBound, prevBoundIsElse, curBound, curBoundIsElse);
                if (prevBound.isUpperBound() && curBound.isLowerBound()) {
                    // do nothing.
                } else if (seenRules.containsAll(ofPrev) && seenRules.containsAll(ofCur)) {
                    LOG.debug("containsAll");
                    // do nothing.
                } else if (prevBound.isUpperBound() && curBound.isUpperBound()) {
                    if (curBound.getBoundaryType() == RangeBoundary.CLOSED && !isBoundInfinity(curBound)) {
                        enumValues.safeAdd(curBound.getValue(), curBoundIsElse);
                    } else {
                        LOG.debug("looking for value in-between {} {} ", prevBound, curBound);
                        enumValues.safeAdd(inBetween(prevBound, curBound), prevBoundIsElse || curBoundIsElse);
                    }
                } else if (prevBound.isLowerBound() && curBound.isLowerBound()) {
                    if (prevBound.getBoundaryType() == RangeBoundary.CLOSED && !isBoundInfinity(prevBound)) {
                        enumValues.safeAdd(prevBound.getValue(), prevBoundIsElse);
                    } else {
                        LOG.debug("looking for value in-between {} {} ", prevBound, curBound);
                        enumValues.safeAdd(inBetween(prevBound, curBound), prevBoundIsElse || curBoundIsElse);
                    }
                } else {
                    if (prevBound.getBoundaryType() == RangeBoundary.CLOSED && !isBoundInfinity(prevBound)) {
                        enumValues.safeAdd(prevBound.getValue(), prevBoundIsElse);
                    } else if (curBound.getBoundaryType() == RangeBoundary.CLOSED && !isBoundInfinity(curBound)) {
                        enumValues.safeAdd(curBound.getValue(), curBoundIsElse);
                    } else {
                        LOG.debug("looking for value in-between {} {} ", prevBound, curBound);
                        enumValues.safeAdd(inBetween(prevBound, curBound), prevBoundIsElse || curBoundIsElse);
                    }
                }

                seenRules.addAll(ofPrev);
                seenRules.addAll(ofCur);
                seenRules.remove(elseRuleIdx.orElse(-1) + 1);
                ofPrev = new HashSet<>();
                ofCur = new HashSet<>();

                prevBound = curBound;
                prevBoundIsElse = curBoundIsElse;
                if (prevBound.isLowerBound()) {
                    addToActive(prevBound, ofPrev);
                }
                while (bounds.size() > 0 && bounds.get(0).compareTo(prevBound) == 0) {
                    prevBound = bounds.remove(0); //look-ahead.
                    prevBoundIsElse &= isBoundOnElseRule(prevBound);
                    if (prevBound.isLowerBound()) {
                        addToActive(prevBound, ofPrev);
                    }
                }
            }

            LOG.debug("enumValues: {}", enumValues);
        }
    }

    public static class ColEnumValues {

        private List<Object> enumValues = new ArrayList<>();
        private Object elseValue = null;

        public void safeAdd(Object value, boolean isElseValue) {
            if (!isElseValue) {
                enumValues.add(value);
            } else if (elseValue == null) {
                elseValue = value;
            } else {
                LOG.debug("will not overwrite elseValue: {}", value);
            }
        }

        @Override
        public String toString() {
            return "ColEnumValues [enumValues= " + enumValues.stream().map(Object::toString).collect(Collectors.joining(", ")) + " ; elseValue=" + elseValue + "]";
        }

    }


    private void addToActive(Bound<?> prevBound, Set<Integer> activeRules) {
        activeRules.add(prevBound.getParent().getRule());
    }

    private boolean isBoundOnElseRule(Bound b) {
        return elseRuleIdx.orElse(-1).equals(b.getParent().getRule() - 1);
    }

    private Object inBetween(Bound a, Bound b) {
        if (a.getValue() instanceof BigDecimal || b.getValue() instanceof BigDecimal) {
            BigDecimal aValue = a.getValue() == Interval.NEG_INF ? ((BigDecimal) b.getValue()).add(new BigDecimal(-2)) : (BigDecimal) a.getValue();
            BigDecimal bValue = b.getValue() == Interval.POS_INF ? ((BigDecimal) a.getValue()).add(new BigDecimal(+2)) : (BigDecimal) b.getValue();
            if (bValue.compareTo(new BigDecimal(aValue.intValue() + 1)) > 0) {
                return aValue.intValue() + 1;
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

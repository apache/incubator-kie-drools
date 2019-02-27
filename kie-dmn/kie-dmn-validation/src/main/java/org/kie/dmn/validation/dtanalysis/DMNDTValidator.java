package org.kie.dmn.validation.dtanalysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.feel.codegen.feel11.ProcessedUnaryTest;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.DashNode;
import org.kie.dmn.feel.lang.ast.NumberNode;
import org.kie.dmn.feel.lang.ast.RangeNode;
import org.kie.dmn.feel.lang.ast.UnaryTestListNode;
import org.kie.dmn.feel.lang.ast.UnaryTestNode;
import org.kie.dmn.feel.lang.ast.UnaryTestNode.UnaryOperator;
import org.kie.dmn.feel.lang.impl.UnaryTestInterpretedExecutableExpression;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.model.api.DecisionRule;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.api.InputClause;
import org.kie.dmn.model.api.UnaryTests;
import org.kie.dmn.validation.dtanalysis.model.Bound;
import org.kie.dmn.validation.dtanalysis.model.DDTAInputClause;
import org.kie.dmn.validation.dtanalysis.model.DDTAInputEntry;
import org.kie.dmn.validation.dtanalysis.model.DDTARule;
import org.kie.dmn.validation.dtanalysis.model.DDTATable;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;
import org.kie.dmn.validation.dtanalysis.model.Hyperrectangle;
import org.kie.dmn.validation.dtanalysis.model.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNDTValidator {

    private static final Logger LOG = LoggerFactory.getLogger(DMNDTValidator.class);
    private final org.kie.dmn.feel.FEEL FEEL;

    public DMNDTValidator(List<DMNProfile> dmnProfiles) {
        FEEL = org.kie.dmn.feel.FEEL.newInstance((List) dmnProfiles);
    }

    public List<DTAnalysis> validate(DMNModel model) {
        List<DTAnalysis> results = new ArrayList<>();

        for (DecisionNode dn : model.getDecisions()) {
            Expression expression = dn.getDecision().getExpression();
            if (expression instanceof DecisionTable) {
                DecisionTable decisionTable = (DecisionTable) expression;
                try {
                    droolsVerifierSystout(model, dn, decisionTable);
                    DTAnalysis result = dmnDTAnalysis(model, dn, decisionTable);
                    results.add(result);
                } catch (Throwable t) {
                    LOG.warn("Failed dmnDTAnalysis for table:" + decisionTable.getId(), t);
                    throw new DMNDTValidatorException(t, decisionTable);
                }
            }
        }

        return results;
    }

    private DTAnalysis dmnDTAnalysis(DMNModel model, DecisionNode dn, DecisionTable dt) {
        DDTATable ddtaTable = new DDTATable();
        for (int jColIdx = 0; jColIdx < dt.getInput().size(); jColIdx++) {
            InputClause ie = dt.getInput().get(jColIdx);
            Interval infDomain = new Interval(RangeBoundary.CLOSED, Interval.NEG_INF, Interval.POS_INF, RangeBoundary.CLOSED, 0, jColIdx + 1);
            if (ie.getInputValues() != null) {
                ProcessedUnaryTest compileUnaryTests = (ProcessedUnaryTest) FEEL.compileUnaryTests(ie.getInputValues().getText(), FEEL.newCompilerContext());
                UnaryTestInterpretedExecutableExpression interpreted = compileUnaryTests.getInterpreted();
                UnaryTestListNode utln = (UnaryTestListNode) interpreted.getExpr().getExpression();
                if (utln.getElements().size() != 1) {
                    throw new DMNDTValidatorException("Multiple constraint on column not supported: " + utln, dt);
                }
                UnaryTestNode utn0 = (UnaryTestNode) utln.getElements().get(0);
                Interval interval = utnToInterval(utn0, infDomain, 0, jColIdx + 1);
                DDTAInputClause ic = new DDTAInputClause(interval);
                ddtaTable.getInputs().add(ic);
            } else {
                DDTAInputClause ic = new DDTAInputClause(infDomain);
                ddtaTable.getInputs().add(ic);
            }
        }
        for (int jRowIdx = 0; jRowIdx < dt.getRule().size(); jRowIdx++) {
            DecisionRule r = dt.getRule().get(jRowIdx);

            DDTARule ddtaRule = new DDTARule();
            int jColIdx = 0;
            for (UnaryTests ie : r.getInputEntry()) {
                ProcessedUnaryTest compileUnaryTests = (ProcessedUnaryTest) FEEL.compileUnaryTests(ie.getText(), FEEL.newCompilerContext());
                UnaryTestInterpretedExecutableExpression interpreted = compileUnaryTests.getInterpreted();
                UnaryTestListNode utln = (UnaryTestListNode) interpreted.getExpr().getExpression();

                DDTAInputClause ddtaInputClause = ddtaTable.getInputs().get(jColIdx);

                DDTAInputEntry ddtaInputEntry = new DDTAInputEntry(utln.getElements(), toIntervals(utln.getElements(), ddtaInputClause.getDomainMinMax(), jRowIdx + 1, jColIdx + 1));
                // TODO: check all inputEntries is within the Domain min/max.
                ddtaRule.getInputEntry().add(ddtaInputEntry);
                jColIdx++;
            }
            // output not in scope for processing: // for (LiteralExpression oe : r.getOutputEntry()) { }
            ddtaTable.getRule().add(ddtaRule);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("{}", ddtaTable);
            LOG.debug("project on columns.");
            for (int colIdx = 0; colIdx < ddtaTable.inputCols(); colIdx++) {
                LOG.debug("colIdx " + colIdx);
                List<Interval> intervals = ddtaTable.projectOnColumnIdx(colIdx);
                LOG.debug("{}", intervals);
                List<Bound> bounds = intervals.stream().flatMap(i -> Stream.of(i.getLowerBound(), i.getUpperBound())).collect(Collectors.toList());
                LOG.debug("{}", bounds);
                Collections.sort(bounds);
                LOG.debug("{}", bounds);
            }
        }
        DTAnalysis analysis = new DTAnalysis(dt);
        LOG.debug("findGaps");
        findGaps(analysis, ddtaTable, 0, new Interval[ddtaTable.inputCols()], Collections.emptyList());
        return analysis;
    }

    private static void findGaps(DTAnalysis analysis, DDTATable ddtaTable, int jColIdx, Interval[] currentIntervals, List<Number> activeRules) {
        if (jColIdx < ddtaTable.inputCols()) {
            List<Interval> activeIntervals = new ArrayList<>();
            List<Interval> intervals = ddtaTable.projectOnColumnIdx(jColIdx);
            if (!activeRules.isEmpty()) {
                // TODO verify, I don't think this need to include activeRules from ALL the previous dimensions, but better prove it.
                intervals = intervals.stream().filter(i -> activeRules.contains(i.getRule())).collect(Collectors.toList());
            }
            List<Bound> bounds = intervals.stream().flatMap(i -> Stream.of(i.getLowerBound(), i.getUpperBound())).collect(Collectors.toList());
            Collections.sort(bounds);

            Interval domainRange = ddtaTable.getInputs().get(jColIdx).getDomainMinMax();
            // TODO: filter for only those bounds in the typeRef domain range. Might not be needed if during compilation of DT the rule range is checked within the domain.

            // artificial low/high bounds:
            bounds.add(domainRange.getUpperBound());
            Bound<?> lastBound = domainRange.getLowerBound();
            for (Bound<?> currentBound : bounds) {
                if (activeIntervals.isEmpty() && !adOrOver(lastBound, currentBound)) {
                    Interval missingInterval = lastDimensionUncoveredInterval(lastBound, currentBound, domainRange);
                    currentIntervals[jColIdx] = missingInterval;

                    // TODO: merge hyperRectangle

                    List<Interval> edges = new ArrayList<>();
                    for (int p = 0; p <= jColIdx; p++) {
                        edges.add(currentIntervals[p]);
                    }
                    Hyperrectangle gap = new Hyperrectangle(ddtaTable.inputCols(), edges);
                    LOG.debug("{}", gap);
                    analysis.addGap(gap);
                }
                if (!activeIntervals.isEmpty()) {
                    Interval missingInterval = Interval.newFromBounds(lastBound, currentBound);
                    currentIntervals[jColIdx] = missingInterval;
                    findGaps(analysis, ddtaTable, jColIdx + 1, currentIntervals, activeIntervals.stream().map(Interval::getRule).collect(Collectors.toList()));
                }
                if (currentBound.isLowerBound()) {
                    activeIntervals.add(currentBound.getParent());
                } else {
                    activeIntervals.remove(currentBound.getParent());
                }
                lastBound = currentBound;
            }
        }
    }

    private static Interval lastDimensionUncoveredInterval(Bound<?> l, Bound<?> r, Interval domain) {
        boolean isLmin = l.equals(domain.getLowerBound());
        boolean isRmax = r.equals(domain.getUpperBound());
        return new Interval(isLmin ? domain.getLowerBound().getBoundaryType() : invertBoundary(l.getBoundaryType()),
                            l.getValue(),
                            r.getValue(),
                            isRmax ? domain.getUpperBound().getBoundaryType() : invertBoundary(r.getBoundaryType()),
                            0, 0);
    }

    private static Range.RangeBoundary invertBoundary(Range.RangeBoundary b) {
        if (b == RangeBoundary.OPEN) {
            return RangeBoundary.CLOSED;
        } else if (b == RangeBoundary.CLOSED) {
            return RangeBoundary.OPEN;
        } else {
            throw new IllegalStateException("invertBoundary for: " + b);
        }
    }

    /**
     * Returns true if left is overlapping or adjacent to right
     */
    private static boolean adOrOver(Bound<?> left, Bound<?> right) {
        boolean isValueEqual = left.getValue().equals(right.getValue());
        boolean isBothOpen = left.getBoundaryType() == RangeBoundary.OPEN && right.getBoundaryType() == RangeBoundary.OPEN;
        return isValueEqual && !isBothOpen;
    }

    private static List<Interval> toIntervals(List<BaseNode> elements, Interval minMax, long rule, long col) {
        List<Interval> results = new ArrayList<>();
        for (BaseNode n : elements) {
            if (n instanceof DashNode) {
                results.add(new Interval(minMax.getLowerBound().getBoundaryType(), minMax.getLowerBound().getValue(), minMax.getUpperBound().getValue(), minMax.getUpperBound().getBoundaryType(), rule, col));
                continue;
            }
            UnaryTestNode ut = (UnaryTestNode) n;
            results.add(utnToInterval(ut, minMax, rule, col));
        }
        return results;
    }

    private static Interval utnToInterval(UnaryTestNode ut, Interval minMax, long rule, long col) {
        if (ut.getOperator() == UnaryOperator.LTE) {
            return new Interval(minMax.getLowerBound().getBoundaryType(), minMax.getLowerBound().getValue(), valueFromNode(ut.getValue()), RangeBoundary.CLOSED, rule, col);
        } else if (ut.getOperator() == UnaryOperator.LT) {
            return new Interval(minMax.getLowerBound().getBoundaryType(), minMax.getLowerBound().getValue(), valueFromNode(ut.getValue()), RangeBoundary.OPEN, rule, col);
        } else if (ut.getOperator() == UnaryOperator.GT) {
            return new Interval(RangeBoundary.OPEN, valueFromNode(ut.getValue()), minMax.getUpperBound().getValue(), minMax.getUpperBound().getBoundaryType(), rule, col);
        } else if (ut.getOperator() == UnaryOperator.GTE) {
            return new Interval(RangeBoundary.CLOSED, valueFromNode(ut.getValue()), minMax.getUpperBound().getValue(), minMax.getUpperBound().getBoundaryType(), rule, col);
        } else if (ut.getValue() instanceof RangeNode) {
            RangeNode rangeNode = (RangeNode) ut.getValue();
            return new Interval(RangeBoundary.CLOSED, valueFromNode(rangeNode.getStart()), valueFromNode(rangeNode.getEnd()), RangeBoundary.CLOSED, rule, col);
        } else {
            throw new UnsupportedOperationException("UnaryTest type not supported: " + ut);
        }
    }

    private static Comparable<?> valueFromNode(BaseNode node) {
        if (node instanceof NumberNode) {
            NumberNode numberNode = (NumberNode) node;
            return numberNode.getValue(); 
            //        } else if (node instanceof StringNode) {
            //            StringNode stringNode = (StringNode) node;
            //            return stringNode.getText();
        } else {
            throw new UnsupportedOperationException("valueFromNode not supported: " + node);
        }
    }

    private void droolsVerifierSystout(DMNModel model, DecisionNode dn, Expression expression) {
        try {
            new DroolsVerifierDTValidator(this.FEEL).validateDT(model, dn, (DecisionTable) expression);
        } catch (Throwable e) {
            e.printStackTrace();
            LOG.debug("droolsVerifierSystout", e);
            throw new RuntimeException(e);
        }
    }



}

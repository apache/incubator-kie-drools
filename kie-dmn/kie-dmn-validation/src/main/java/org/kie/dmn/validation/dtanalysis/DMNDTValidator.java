package org.kie.dmn.validation.dtanalysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.feel.codegen.feel11.ProcessedUnaryTest;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.DashNode;
import org.kie.dmn.feel.lang.ast.NumberNode;
import org.kie.dmn.feel.lang.ast.RangeNode;
import org.kie.dmn.feel.lang.ast.StringNode;
import org.kie.dmn.feel.lang.ast.UnaryTestListNode;
import org.kie.dmn.feel.lang.ast.UnaryTestNode;
import org.kie.dmn.feel.lang.ast.UnaryTestNode.UnaryOperator;
import org.kie.dmn.feel.lang.impl.UnaryTestInterpretedExecutableExpression;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.model.api.DecisionRule;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.model.api.UnaryTests;
import org.kie.dmn.validation.dtanalysis.model.Bound;
import org.kie.dmn.validation.dtanalysis.model.DDTAInputEntry;
import org.kie.dmn.validation.dtanalysis.model.DDTARule;
import org.kie.dmn.validation.dtanalysis.model.DDTATable;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;
import org.kie.dmn.validation.dtanalysis.model.Hyperrectangle;
import org.kie.dmn.validation.dtanalysis.model.Interval;

public class DMNDTValidator {

    private final org.kie.dmn.feel.FEEL FEEL;

    public DMNDTValidator(List<DMNProfile> dmnProfiles) {
        FEEL = org.kie.dmn.feel.FEEL.newInstance((List) dmnProfiles);
    }

    public List<DTAnalysis> validate(DMNModel model) {
        List<DTAnalysis> results = new ArrayList<>();
        try {
            for (DecisionNode dn : model.getDecisions()) {
                Expression expression = dn.getDecision().getExpression();
                if (expression instanceof DecisionTable) {
                    DecisionTable decisionTable = (DecisionTable) expression;
                    droolsVerifierSystout(model, dn, decisionTable);
                    DTAnalysis result = dmnDTAnalysis(model, dn, decisionTable);
                    results.add(result);
                }
            }
        } catch (Throwable t) {
            // TODO this is used for current developments.
            t.printStackTrace();
            throw new RuntimeException(t);
        }
        return results;
    }

    private DTAnalysis dmnDTAnalysis(DMNModel model, DecisionNode dn, DecisionTable dt) {
        DDTATable ddtaTable = new DDTATable();
        for (int jRowIdx = 0; jRowIdx < dt.getRule().size(); jRowIdx++) {

            DecisionRule r = dt.getRule().get(jRowIdx);

            DDTARule ddtaRule = new DDTARule();
            int jColIdx = 0;
            for (UnaryTests ie : r.getInputEntry()) {
                ProcessedUnaryTest compileUnaryTests = (ProcessedUnaryTest) FEEL.compileUnaryTests(ie.getText(), FEEL.newCompilerContext());
                UnaryTestInterpretedExecutableExpression interpreted = compileUnaryTests.getInterpreted();
                UnaryTestListNode utln = (UnaryTestListNode) interpreted.getExpr().getExpression();

                String typeRef = Optional.ofNullable(dt.getInput().get(jColIdx).getInputExpression().getTypeRef()).map(QName::getLocalPart).orElse("any");

                DDTAInputEntry ddtaInputEntry = new DDTAInputEntry(utln.getElements(), toIntervals(utln.getElements(), typeRef, jRowIdx + 1, jColIdx + 1));
                ddtaRule.getInputEntry().add(ddtaInputEntry);
                jColIdx++;
            }
            for (LiteralExpression oe : r.getOutputEntry()) {
                // do I need output?
            }
            ddtaTable.getRule().add(ddtaRule);
        }
        System.out.println(ddtaTable);
        System.out.println("project on columns.");
        for (int colIdx = 0; colIdx < ddtaTable.inputCols(); colIdx++) {
            System.out.println("colIdx " + colIdx);
            List<Interval> intervals = ddtaTable.projectOnColumnIdx(colIdx);
            System.out.println(intervals);
            List<Bound<?>> bounds = intervals.stream().flatMap(i -> Stream.of(i.getLowerBound(), i.getUpperBound())).collect(Collectors.toList());
            System.out.println(bounds);
            Collections.sort(bounds);
            System.out.println(bounds);
        }
        System.out.println("findGaps");
        DTAnalysis analysis = new DTAnalysis(dt);
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
            List<Bound<?>> bounds = intervals.stream().flatMap(i -> Stream.of(i.getLowerBound(), i.getUpperBound())).collect(Collectors.toList());
            Collections.sort(bounds);

            Interval domainRange = new Interval(RangeBoundary.CLOSED, Interval.NEG_INF, Interval.POS_INF, RangeBoundary.CLOSED, 0, 0);
            // TODO: filter for only those bounds in the typeRef domain range.

            // artificial low/high bounds:
            bounds.add(domainRange.getUpperBound());
            Bound<?> lastBound = domainRange.getLowerBound();
            for (Bound<?> currentBound : bounds) {
                if (activeIntervals.isEmpty() && !currentBound.equals(lastBound)) {
                    Interval missingInterval = Interval.newFromBounds(lastBound, currentBound);
                    currentIntervals[jColIdx] = missingInterval;

                    // TODO: merge hyperRectangle

                    List<Interval> edges = new ArrayList<>();
                    for (int p = 0; p <= jColIdx; p++) {
                        System.out.print(currentIntervals[p]);
                        edges.add(currentIntervals[p]);
                    }
                    System.out.println("");
                    Hyperrectangle gap = new Hyperrectangle(ddtaTable.inputCols(), edges);
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

    private static List<Interval> toIntervals(List<BaseNode> elements, String typeRef, long rule, long col) {
        List<Interval> results = new ArrayList<>();
        for (BaseNode n : elements) {
            if (n instanceof DashNode) {
                results.add(new Interval(RangeBoundary.CLOSED, Interval.NEG_INF, Interval.POS_INF, RangeBoundary.CLOSED, rule, col));
                continue;
            }
            UnaryTestNode ut = (UnaryTestNode) n;
            if (ut.getOperator() == UnaryOperator.LTE) {
                results.add(new Interval(RangeBoundary.CLOSED, Interval.NEG_INF, valueFromNode(ut.getValue()), RangeBoundary.CLOSED, rule, col));
            } else if (ut.getValue() instanceof RangeNode) {
                RangeNode rangeNode = (RangeNode) ut.getValue();
                results.add(new Interval(RangeBoundary.CLOSED, valueFromNode(rangeNode.getStart()), valueFromNode(rangeNode.getEnd()), RangeBoundary.CLOSED, rule, col));
            } else {
                throw new UnsupportedOperationException("TODO");
            }
        }
        return results;
    }

    private static Comparable<?> valueFromNode(BaseNode start) {
        if (start instanceof NumberNode) {
            NumberNode numberNode = (NumberNode) start;
            return numberNode.getValue(); 
        } else if (start instanceof StringNode) {
            StringNode stringNode = (StringNode) start;
            return stringNode.getText();
        } else {
            throw new UnsupportedOperationException("TODO");
        }
    }

    private void droolsVerifierSystout(DMNModel model, DecisionNode dn, Expression expression) {
        try {
            new DroolsVerifierDTValidator(this.FEEL).validateDT(model, dn, (DecisionTable) expression);
        } catch (Throwable e) {
            e.printStackTrace();
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }



}

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

package org.kie.dmn.validation.dtanalysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.codegen.feel11.ProcessedExpression;
import org.kie.dmn.feel.codegen.feel11.ProcessedUnaryTest;
import org.kie.dmn.feel.lang.SimpleType;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.DashNode;
import org.kie.dmn.feel.lang.ast.NullNode;
import org.kie.dmn.feel.lang.ast.RangeNode;
import org.kie.dmn.feel.lang.ast.RangeNode.IntervalBoundary;
import org.kie.dmn.feel.lang.ast.UnaryTestListNode;
import org.kie.dmn.feel.lang.ast.UnaryTestNode;
import org.kie.dmn.feel.lang.ast.UnaryTestNode.UnaryOperator;
import org.kie.dmn.feel.lang.ast.Visitor;
import org.kie.dmn.feel.lang.impl.InterpretedExecutableExpression;
import org.kie.dmn.feel.lang.impl.UnaryTestInterpretedExecutableExpression;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.model.api.DecisionRule;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.HitPolicy;
import org.kie.dmn.model.api.InputClause;
import org.kie.dmn.model.api.ItemDefinition;
import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.model.api.OutputClause;
import org.kie.dmn.model.api.UnaryTests;
import org.kie.dmn.validation.DMNValidator;
import org.kie.dmn.validation.DMNValidator.Validation;
import org.kie.dmn.validation.dtanalysis.DMNDTAnalyserValueFromNodeVisitor.DMNDTAnalyserOutputClauseVisitor;
import org.kie.dmn.validation.dtanalysis.mcdc.MCDCAnalyser;
import org.kie.dmn.validation.dtanalysis.mcdc.MCDCAnalyser.PosNegBlock;
import org.kie.dmn.validation.dtanalysis.model.Bound;
import org.kie.dmn.validation.dtanalysis.model.BoundValueComparator;
import org.kie.dmn.validation.dtanalysis.model.DDTAInputClause;
import org.kie.dmn.validation.dtanalysis.model.DDTAInputEntry;
import org.kie.dmn.validation.dtanalysis.model.DDTAOutputClause;
import org.kie.dmn.validation.dtanalysis.model.DDTARule;
import org.kie.dmn.validation.dtanalysis.model.DDTATable;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;
import org.kie.dmn.validation.dtanalysis.model.Hyperrectangle;
import org.kie.dmn.validation.dtanalysis.model.Interval;
import org.kie.dmn.validation.dtanalysis.model.Overlap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNDTAnalyser implements InternalDMNDTAnalyser {

    private static final Logger LOG = LoggerFactory.getLogger(DMNDTAnalyser.class);
    private final org.kie.dmn.feel.FEEL FEEL;
    private final DMNDTAnalyserValueFromNodeVisitor valueFromNodeVisitor;
    private final DMNDTAnalyserOutputClauseVisitor outputClauseVisitor;

    public DMNDTAnalyser(List<DMNProfile> dmnProfiles) {
        FEEL = org.kie.dmn.feel.FEEL.newInstance((List) dmnProfiles);
        valueFromNodeVisitor = new DMNDTAnalyserValueFromNodeVisitor((List) dmnProfiles);
        outputClauseVisitor = new DMNDTAnalyserOutputClauseVisitor((List) dmnProfiles);
    }

    @Override
    public List<DTAnalysis> analyse(DMNModel model, Set<DMNValidator.Validation> flags) {
        if (!flags.contains(Validation.ANALYZE_DECISION_TABLE)) {
            throw new IllegalArgumentException();
        }
        List<DTAnalysis> results = new ArrayList<>();

        List<? extends DecisionTable> decisionTables = model.getDefinitions().findAllChildren(DecisionTable.class);
        for (DecisionTable dt : decisionTables) {
            try {
                DTAnalysis result = dmnDTAnalysis(model, dt, flags);
                results.add(result);
            } catch (Throwable t) {
                LOG.debug("Skipped dmnDTAnalysis for table: {}", dt.getId(), t);
                DTAnalysis result = DTAnalysis.ofError(dt, t);
                results.add(result);
            }
        }

        return results;
    }

    private DTAnalysis dmnDTAnalysis(DMNModel model, DecisionTable dt, Set<Validation> flags) {
        DDTATable ddtaTable = new DDTATable();
        compileTableInputClauses(model, dt, ddtaTable);
        compileTableOutputClauses(model, dt, ddtaTable);
        compileTableRules(dt, ddtaTable);
        compileTableComputeColStringMissingEnum(model, dt, ddtaTable);
        printDebugTableInfo(ddtaTable);
        DTAnalysis analysis = new DTAnalysis(dt, ddtaTable);
        analysis.computeOutputInLOV();
        if (!dt.getHitPolicy().equals(HitPolicy.COLLECT)) {
            if (ddtaTable.getColIDsStringWithoutEnum().isEmpty()) {
                LOG.debug("findGaps");
                findGaps(analysis, ddtaTable, 0, new Interval[ddtaTable.inputCols()], Collections.emptyList());
            } else {
                LOG.debug("findGaps Skipped because getColIDsStringWithoutEnum is not empty: {}", ddtaTable.getColIDsStringWithoutEnum());
            }
            LOG.debug("findOverlaps");
            findOverlaps(analysis, ddtaTable, 0, new Interval[ddtaTable.inputCols()], Collections.emptyList());
        } else {
            LOG.debug("findGaps(), findOverlaps() are Skipped because getHitPolicy is COLLECT.");
        }
        LOG.debug("computeMaskedRules");
        analysis.computeMaskedRules();
        LOG.debug("computeMisleadingRules");
        analysis.computeMisleadingRules();
        LOG.debug("normalize");
        analysis.normalize();
        LOG.debug("computeSubsumptions");
        analysis.computeSubsumptions();
        LOG.debug("computeContractions");
        analysis.computeContractions();
        LOG.debug("compute1stNFViolations");
        analysis.compute1stNFViolations();
        LOG.debug("compute2ndNFViolations");
        analysis.compute2ndNFViolations();
        LOG.debug("computeHitPolicyRecommender");
        analysis.computeHitPolicyRecommender();
        if (flags.contains(Validation.COMPUTE_DECISION_TABLE_MCDC)) {
            LOG.debug("mcdc.");
            List<PosNegBlock> selectedBlocks = new MCDCAnalyser(ddtaTable, dt).compute();
            analysis.setMCDCSelectedBlocks(selectedBlocks);
        }
        return analysis;
    }

    private void compileTableComputeColStringMissingEnum(DMNModel model, DecisionTable dt, DDTATable ddtaTable) {
        for (int iColIdx = 0; iColIdx < ddtaTable.inputCols(); iColIdx++) {
            InputClause ie = dt.getInput().get(iColIdx);
            QName typeRef = DMNCompilerImpl.getNamespaceAndName(dt, ((DMNModelImpl) model).getImportAliasesForNS(), ie.getInputExpression().getTypeRef(), model.getNamespace());
            if (SimpleType.STRING.equals(typeRef.getLocalPart()) && !ddtaTable.getInputs().get(iColIdx).isDiscreteDomain()) {
                Interval infStringDomain = ddtaTable.getInputs().get(iColIdx).getDomainMinMax();
                boolean areAllSinglePointOrAll = true;
                for (int jRowIdx = 0; jRowIdx < dt.getRule().size() && areAllSinglePointOrAll; jRowIdx++) {
                    List<Interval> r = ddtaTable.getRule().get(jRowIdx).getInputEntry().get(iColIdx).getIntervals();
                    for (Interval interval : r) {
                        areAllSinglePointOrAll = areAllSinglePointOrAll && (infStringDomain.equals(interval) || interval.isSingularity());
                    }
                }
                if (areAllSinglePointOrAll) {
                    ddtaTable.addColIdStringWithoutEnum(iColIdx + 1);
                }
            }
        }
    }

    private void printDebugTableInfo(DDTATable ddtaTable) {
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
            LOG.debug("col IDs being String without Enum: {}", ddtaTable.getColIDsStringWithoutEnum());
        }
    }

    private void compileTableRules(DecisionTable dt, DDTATable ddtaTable) {
        for (int jRowIdx = 0; jRowIdx < dt.getRule().size(); jRowIdx++) {
            DecisionRule r = dt.getRule().get(jRowIdx);

            DDTARule ddtaRule = new DDTARule();
            int jColIdx = 0;
            for (UnaryTests ie : r.getInputEntry()) {
                ProcessedUnaryTest compileUnaryTests = (ProcessedUnaryTest) FEEL.compileUnaryTests(ie.getText(), FEEL.newCompilerContext());
                UnaryTestInterpretedExecutableExpression interpreted = compileUnaryTests.getInterpreted();
                UnaryTestListNode utln = (UnaryTestListNode) interpreted.getASTNode();

                DDTAInputClause ddtaInputClause = ddtaTable.getInputs().get(jColIdx);

                DDTAInputEntry ddtaInputEntry = new DDTAInputEntry(utln.getElements(), toIntervals(utln.getElements(), utln.isNegated(), ddtaInputClause.getDomainMinMax(), ddtaInputClause.getDiscreteValues(), jRowIdx + 1, jColIdx + 1));
                for (Interval interval : ddtaInputEntry.getIntervals()) {
                    Interval domainMinMax = ddtaTable.getInputs().get(jColIdx).getDomainMinMax();
                    if (!domainMinMax.includes(interval)) {
                        throw new IllegalStateException(MsgUtil.createMessage(Msg.DTANALYSIS_ERROR_RULE_OUTSIDE_DOMAIN, jRowIdx + 1, interval, domainMinMax, jColIdx + 1));
                    }
                }
                ddtaRule.getInputEntry().add(ddtaInputEntry);
                jColIdx++;
            }
            for (LiteralExpression oe : r.getOutputEntry()) {
                ProcessedExpression compile = (ProcessedExpression) FEEL.compile(oe.getText(), FEEL.newCompilerContext());
                InterpretedExecutableExpression interpreted = compile.getInterpreted();
                BaseNode outputEntryNode = (BaseNode) interpreted.getASTNode();
                Comparable<?> value = valueFromNode(outputEntryNode, outputClauseVisitor);
                ddtaRule.getOutputEntry().add(value);
                jColIdx++;
            }
            ddtaTable.addRule(ddtaRule);
        }
    }


    private void compileTableInputClauses(DMNModel model, DecisionTable dt, DDTATable ddtaTable) {
        for (int jColIdx = 0; jColIdx < dt.getInput().size(); jColIdx++) {
            InputClause ie = dt.getInput().get(jColIdx);
            Interval infDomain = new Interval(RangeBoundary.CLOSED, Interval.NEG_INF, Interval.POS_INF, RangeBoundary.CLOSED, 0, jColIdx + 1);
            String allowedValues;
            if (ie.getInputValues() != null) {
                allowedValues = ie.getInputValues().getText();
            } else {
                QName typeRef = DMNCompilerImpl.getNamespaceAndName(dt, ((DMNModelImpl) model).getImportAliasesForNS(), ie.getInputExpression().getTypeRef(), model.getNamespace());
                allowedValues = findAllowedValues(model, typeRef);
            }
            if (allowedValues != null) {
                ProcessedUnaryTest compileUnaryTests = (ProcessedUnaryTest) FEEL.compileUnaryTests(allowedValues, FEEL.newCompilerContext());
                UnaryTestInterpretedExecutableExpression interpreted = compileUnaryTests.getInterpreted();
                UnaryTestListNode utln = (UnaryTestListNode) interpreted.getASTNode();
                if (utln.getElements().size() != 1) {
                    verifyUnaryTestsAllEQ(utln, dt);
                    List<Comparable<?>> discreteValues = getDiscreteValues(utln);
                    Collections.sort((List) discreteValues);
                    Interval discreteDomainMinMax = new Interval(RangeBoundary.CLOSED, discreteValues.get(0), discreteValues.get(discreteValues.size() - 1), RangeBoundary.CLOSED, 0, jColIdx + 1);
                    DDTAInputClause ic = new DDTAInputClause(discreteDomainMinMax, discreteValues, getDiscreteValues(utln));
                    ddtaTable.getInputs().add(ic);
                } else if (utln.getElements().size() == 1) {
                    UnaryTestNode utn0 = (UnaryTestNode) utln.getElements().get(0);
                    Interval interval = utnToInterval(utn0, infDomain, null, 0, jColIdx + 1);
                    DDTAInputClause ic = new DDTAInputClause(interval);
                    ddtaTable.getInputs().add(ic);
                } else {
                    throw new IllegalStateException("inputValues not null but utln: " + utln);
                }
            } else {
                DDTAInputClause ic = new DDTAInputClause(infDomain);
                ddtaTable.getInputs().add(ic);
            }
        }
    }

    private void compileTableOutputClauses(DMNModel model, DecisionTable dt, DDTATable ddtaTable) {
        for (int jColIdx = 0; jColIdx < dt.getOutput().size(); jColIdx++) {
            OutputClause oe = dt.getOutput().get(jColIdx);
            Interval infDomain = new Interval(RangeBoundary.CLOSED, Interval.NEG_INF, Interval.POS_INF, RangeBoundary.CLOSED, 0, jColIdx + 1);
            String allowedValues = null;
            if (oe.getOutputValues() != null) {
                allowedValues = oe.getOutputValues().getText();
            } else {
                QName outputTypeRef = (oe.getTypeRef() == null && dt.getOutput().size() == 1) ? dt.getTypeRef() : oe.getTypeRef();
                if (outputTypeRef != null) {
                    QName typeRef = DMNCompilerImpl.getNamespaceAndName(dt, ((DMNModelImpl) model).getImportAliasesForNS(), outputTypeRef, model.getNamespace());
                    allowedValues = findAllowedValues(model, typeRef);
                }
            }
            if (allowedValues != null) {
                ProcessedUnaryTest compileUnaryTests = (ProcessedUnaryTest) FEEL.compileUnaryTests(allowedValues, FEEL.newCompilerContext());
                UnaryTestInterpretedExecutableExpression interpreted = compileUnaryTests.getInterpreted();
                UnaryTestListNode utln = (UnaryTestListNode) interpreted.getASTNode();
                if (utln.getElements().size() != 1) {
                    verifyUnaryTestsAllEQ(utln, dt);
                    List<Comparable<?>> discreteValues = getDiscreteValues(utln);
                    List<Comparable<?>> outputOrder = new ArrayList<>(discreteValues);
                    Collections.sort((List) discreteValues);
                    Interval discreteDomainMinMax = new Interval(RangeBoundary.CLOSED, discreteValues.get(0), discreteValues.get(discreteValues.size() - 1), RangeBoundary.CLOSED, 0, jColIdx + 1);
                    DDTAOutputClause ic = new DDTAOutputClause(discreteDomainMinMax, discreteValues, outputOrder);
                    ddtaTable.getOutputs().add(ic);
                } else if (utln.getElements().size() == 1) {
                    UnaryTestNode utn0 = (UnaryTestNode) utln.getElements().get(0);
                    Interval interval = utnToInterval(utn0, infDomain, null, 0, jColIdx + 1);
                    DDTAOutputClause ic = new DDTAOutputClause(interval);
                    ddtaTable.getOutputs().add(ic);
                } else {
                    throw new IllegalStateException("inputValues not null but utln: " + utln);
                }
            } else {
                DDTAOutputClause ic = new DDTAOutputClause(infDomain);
                ddtaTable.getOutputs().add(ic);
            }
        }
    }

    private void verifyUnaryTestsAllEQ(UnaryTestListNode utln, DecisionTable dt) {
        if (!utln.getElements().stream().allMatch(e -> e instanceof UnaryTestNode && ((UnaryTestNode) e).getOperator() == UnaryOperator.EQ)) {
            throw new DMNDTAnalysisException("Multiple constraint on column: " + utln, dt);
        }
    }

    /**
     * Transform a UnaryTestListNode into a List of discrete values for input/output clause enumeration
     */
    private List<Comparable<?>> getDiscreteValues(UnaryTestListNode utln) {
        List<Comparable<?>> discreteValues = new ArrayList<>();
        for (BaseNode e : utln.getElements()) {
            BaseNode value = ((UnaryTestNode) e).getValue();
            if (!(value instanceof NullNode)) { // to retrieve value from input/output clause enumeration, null is ignored.
                Comparable<?> v = valueFromNode(value);
                discreteValues.add(v);
            }
        }
        return discreteValues;
    }

    private String findAllowedValues(DMNModel model, QName typeRef) {
        if (typeRef.getNamespaceURI().equals(model.getNamespace())) {
            Optional<ItemDefinition> opt = model.getDefinitions().getItemDefinition().stream().filter(id -> id.getName().equals(typeRef.getLocalPart())).findFirst();
            if (opt.isPresent()) {
                ItemDefinition id = opt.get();
                if (id.getAllowedValues() != null) {
                    return id.getAllowedValues().getText();
                }
            } else {
                throw new IllegalStateException("Unable to locate typeRef " + typeRef + " to determine domain.");
            }
        } else if (typeRef.getNamespaceURI().equals(model.getDefinitions().getURIFEEL()) && typeRef.getLocalPart().equals("boolean")) {
            return "false, true";
        }

        List<DMNModel> childModels = ((DMNModelImpl) model).getImportChainDirectChildModels();
        return childModels.stream()
                          .map(childModel -> findAllowedValues(childModel, typeRef))
                          .filter(Objects::nonNull)
                          .findFirst()
                          .orElse(null);
    }

    private void findOverlaps(DTAnalysis analysis, DDTATable ddtaTable, int jColIdx, Interval[] currentIntervals, Collection<Integer> activeRules) {
        LOG.debug("findOverlaps jColIdx {}, currentIntervals {}, activeRules {}", jColIdx, currentIntervals, activeRules);
        if (jColIdx < ddtaTable.inputCols()) {
            List<Bound> bounds = findBoundsSorted(ddtaTable, jColIdx, activeRules);
            List<Interval> activeIntervals = new ArrayList<>();
            Bound<?> lastBound = bounds.get(0);
            for (Bound<?> currentBound : bounds) {
                LOG.debug("lastBound {} currentBound {}      activeIntervals {} == rules {}", lastBound, currentBound, activeIntervals, activeIntervalsToRules(activeIntervals));
                if (activeIntervals.size() > 1 && canBeNewCurrInterval(lastBound, currentBound)) {
                    Interval analysisInterval = new Interval(lastBound.isUpperBound() ? Interval.invertBoundary(lastBound.getBoundaryType()) : lastBound.getBoundaryType(),
                                                             lastBound.getValue(),
                                                             currentBound.getValue(),
                                                             currentBound.isLowerBound() ? Interval.invertBoundary(currentBound.getBoundaryType()) : currentBound.getBoundaryType(),
                                                             0, 0);
                    currentIntervals[jColIdx] = analysisInterval;
                    findOverlaps(analysis, ddtaTable, jColIdx + 1, currentIntervals, activeIntervalsToRules(activeIntervals));
                }
                if (currentBound.isLowerBound()) {
                    activeIntervals.add(currentBound.getParent());
                } else {
                    activeIntervals.remove(currentBound.getParent());
                }
                lastBound = currentBound;
            }
            currentIntervals[jColIdx] = null; // facilitate debugging.
        } else if (jColIdx == ddtaTable.inputCols()) {
            if (activeRules.size() > 1) {
                Hyperrectangle overlap = new Hyperrectangle(ddtaTable.inputCols(), Arrays.asList(currentIntervals));
                LOG.debug("OVERLAP DETECTED {}", overlap);
                analysis.addOverlap(new Overlap(activeRules, overlap));
            }
        } else {
            throw new IllegalStateException();
        }
        LOG.debug(".");
    }

    private static void findGaps(DTAnalysis analysis, DDTATable ddtaTable, int jColIdx, Interval[] currentIntervals, Collection<Integer> activeRules) {
        LOG.debug("findGaps jColIdx {}, currentIntervals {}, activeRules {}", jColIdx, currentIntervals, activeRules);
        if (jColIdx < ddtaTable.inputCols()) {
            findBoundsSorted(ddtaTable, jColIdx, activeRules);
            List<Bound> bounds = findBoundsSorted(ddtaTable, jColIdx, activeRules);
            Interval domainRange = ddtaTable.getInputs().get(jColIdx).getDomainMinMax();

            // from domain start to the 1st bound
            if (!domainRange.getLowerBound().equals(bounds.get(0))) {
                currentIntervals[jColIdx] = lastDimensionUncoveredInterval(domainRange.getLowerBound(), bounds.get(0), domainRange);
                Hyperrectangle gap = new Hyperrectangle(ddtaTable.inputCols(), buildEdgesForHyperrectangleFromIntervals(currentIntervals, jColIdx));
                analysis.addGap(gap);
                LOG.debug("STARTLEFT GAP DETECTED {}", gap);
            }
            // cycle rule's interval bounds
            List<Interval> activeIntervals = new ArrayList<>();
            Bound<?> lastBound = null;
            for (Bound<?> currentBound : bounds) {
                LOG.debug("lastBound {} currentBound {}      activeIntervals {} == rules {}", lastBound, currentBound, activeIntervals, activeIntervalsToRules(activeIntervals));
                if (activeIntervals.isEmpty() && lastBound != null && !Bound.adOrOver(lastBound, currentBound)) {
                    currentIntervals[jColIdx] = lastDimensionUncoveredInterval(lastBound, currentBound, domainRange);
                    Hyperrectangle gap = new Hyperrectangle(ddtaTable.inputCols(), buildEdgesForHyperrectangleFromIntervals(currentIntervals, jColIdx));
                    LOG.debug("GAP DETECTED {}", gap);
                    analysis.addGap(gap);
                }
                if (!activeIntervals.isEmpty() && canBeNewCurrInterval(lastBound, currentBound)) {
                    Interval missingInterval = new Interval(lastBound.isUpperBound() ? Interval.invertBoundary(lastBound.getBoundaryType()) : lastBound.getBoundaryType(),
                                                            lastBound.getValue(),
                                                            currentBound.getValue(),
                                                            currentBound.isLowerBound() ? Interval.invertBoundary(currentBound.getBoundaryType()) : currentBound.getBoundaryType(),
                                                            0, 0);
                    currentIntervals[jColIdx] = missingInterval;
                    findGaps(analysis, ddtaTable, jColIdx + 1, currentIntervals, activeIntervalsToRules(activeIntervals));
                }
                if (currentBound.isLowerBound()) {
                    activeIntervals.add(currentBound.getParent());
                } else {
                    activeIntervals.remove(currentBound.getParent());
                }
                lastBound = currentBound;
            }
            // from last Nth bound, to domain end.
            if (!lastBound.equals(domainRange.getUpperBound())) {
                currentIntervals[jColIdx] = lastDimensionUncoveredInterval(lastBound, domainRange.getUpperBound(), domainRange);
                Hyperrectangle gap = new Hyperrectangle(ddtaTable.inputCols(), buildEdgesForHyperrectangleFromIntervals(currentIntervals, jColIdx));
                LOG.debug("ENDRIGHT GAP DETECTED {}", gap);
                analysis.addGap(gap);
            }
            currentIntervals[jColIdx] = null; // facilitate debugging.
        }
        LOG.debug(".");
    }

    private static List<Bound> findBoundsSorted(DDTATable ddtaTable, int jColIdx, Collection<Integer> activeRules) {
        List<Interval> intervals = ddtaTable.projectOnColumnIdx(jColIdx);
        if (!activeRules.isEmpty()) {
            intervals = intervals.stream().filter(i -> activeRules.contains(i.getRule())).collect(Collectors.toList());
        }
        LOG.debug("intervals {}", intervals);
        List<Bound> bounds = intervals.stream().flatMap(i -> Stream.of(i.getLowerBound(), i.getUpperBound())).collect(Collectors.toList());
        Collections.sort(bounds);
        LOG.debug("bounds (sorted) {}", bounds);
        return bounds;
    }

    private static List<Interval> buildEdgesForHyperrectangleFromIntervals(Interval[] currentIntervals, int intervalsIndex) {
        List<Interval> edges = new ArrayList<>();
        for (int p = 0; p <= intervalsIndex; p++) {
            edges.add(currentIntervals[p]);
        }
        return edges;
    }

    private static Collection<Integer> activeIntervalsToRules(List<Interval> activeIntervals) {
        return activeIntervals.stream().map(Interval::getRule).collect(Collectors.toList());
    }

    /**
     * Avoid a situation to "open" a new currentInterval for pair of same-side equals bounds like: x], x]
     */
    private static boolean canBeNewCurrInterval(Bound<?> lastBound, Bound<?> currentBound) {
        int vCompare = BoundValueComparator.compareValueDispatchingToInf(lastBound, currentBound);
        if (vCompare != 0) {
            return true;
        } else {
            if (lastBound.isLowerBound() && currentBound.isUpperBound()) {
                return true;
            } else if (lastBound.isUpperBound() && lastBound.getBoundaryType() == RangeBoundary.OPEN 
                && currentBound.isLowerBound() && currentBound.getBoundaryType() == RangeBoundary.OPEN) {
                return true; // the case x) (x
            } else {
                return false;
            }
        }
    }

    private static Interval lastDimensionUncoveredInterval(Bound<?> l, Bound<?> r, Interval domain) {
        boolean isLmin = l.isLowerBound() && l.equals(domain.getLowerBound());
        boolean isRmax = r.isUpperBound() && r.equals(domain.getUpperBound());
        return new Interval(isLmin ? domain.getLowerBound().getBoundaryType() : Interval.invertBoundary(l.getBoundaryType()),
                            l.getValue(),
                            r.getValue(),
                            isRmax ? domain.getUpperBound().getBoundaryType() : Interval.invertBoundary(r.getBoundaryType()),
                            0, 0);
    }

    private List<Interval> toIntervals(List<BaseNode> elements, boolean isNegated, Interval minMax, List discreteValues, int rule, int col) {
        List<Interval> results = new ArrayList<>();
        if (elements.size() == 1 && elements.get(0) instanceof UnaryTestNode && ((UnaryTestNode) elements.get(0)).getValue() instanceof NullNode) {
            return Collections.emptyList();
        }
        if (discreteValues != null && !discreteValues.isEmpty() && areAllEQUnaryTest(elements) && elements.size() > 1) {
            int bitsetLogicalSize = discreteValues.size(); // JDK BitSet size will always be larger.
            BitSet hitValues = new BitSet(bitsetLogicalSize);
            for (BaseNode n : elements) {
                Comparable<?> thisValue = valueFromNode(((UnaryTestNode) n).getValue());
                int indexOf = discreteValues.indexOf(thisValue);
                if (indexOf < 0) {
                    throw new IllegalStateException("Unable to determine discreteValue index for: " + n);
                }
                hitValues.set(indexOf);
            }
            if (isNegated) {
                hitValues.flip(0, bitsetLogicalSize);
            }
            int lowerBoundIdx = -1;
            int upperBoundIdx = -1;
            for (int i = 0; i < hitValues.length(); i++) {
                boolean curValue = hitValues.get(i);
                if (curValue) {
                    if (lowerBoundIdx < 0) {
                        lowerBoundIdx = i;
                        upperBoundIdx = i;
                    } else {
                        upperBoundIdx = i;
                    }
                } else {
                    if (lowerBoundIdx >= 0 && upperBoundIdx >=0) {
                        results.add(createIntervalOfRule(discreteValues, rule, col, lowerBoundIdx, upperBoundIdx));
                        lowerBoundIdx = -1;
                        upperBoundIdx = -1;
                    }
                }
            }
            if (lowerBoundIdx >= 0 && upperBoundIdx >= 0) {
                results.add(createIntervalOfRule(discreteValues, rule, col, lowerBoundIdx, upperBoundIdx));
            }
        } else {
            for (BaseNode n : elements) {
                if (n instanceof DashNode) {
                    results.add(new Interval(minMax.getLowerBound().getBoundaryType(), minMax.getLowerBound().getValue(), minMax.getUpperBound().getValue(), minMax.getUpperBound().getBoundaryType(), rule, col));
                    continue;
                }
                UnaryTestNode ut = (UnaryTestNode) n;
                Interval interval = utnToInterval(ut, minMax, discreteValues, rule, col);
                if (isNegated) {
                    results.addAll(Interval.invertOverDomain(interval, minMax));
                } else {
                    results.add(interval);
                }
            }
        }
        return results;
    }

    private static Interval createIntervalOfRule(List discreteValues, int rule, int col, int lowerBoundIdx, int upperBoundIdx) {
        Comparable<?> lowValue = (Comparable<?>) discreteValues.get(lowerBoundIdx);
        Comparable<?> highValue = (Comparable<?>) discreteValues.get(upperBoundIdx);
        if (upperBoundIdx + 1 == discreteValues.size()) {
            return new Interval(RangeBoundary.CLOSED, lowValue, highValue, RangeBoundary.CLOSED, rule, col);
        } else {
            return new Interval(RangeBoundary.CLOSED, lowValue, (Comparable<?>) discreteValues.get(upperBoundIdx + 1), RangeBoundary.OPEN, rule, col);
        }
    }

    private static boolean areAllEQUnaryTest(List<BaseNode> elements) {
        try {
            boolean result = true;
            for (BaseNode n : elements) {
                result = result && ((UnaryTestNode) n).getOperator() == UnaryOperator.EQ;
            }
            return result;
        } catch (Throwable e) {
            return false;    
        }
    }

    private Interval utnToInterval(UnaryTestNode ut, Interval minMax, List discreteValues, int rule, int col) {
        if (ut.getOperator() == UnaryOperator.EQ) {
            if (discreteValues == null || discreteValues.isEmpty()) {
                return new Interval(RangeBoundary.CLOSED, valueFromNode(ut.getValue()), valueFromNode(ut.getValue()), RangeBoundary.CLOSED, rule, col);
            } else {
                Comparable<?> thisValue = valueFromNode(ut.getValue());
                int indexOf = discreteValues.indexOf(thisValue);
                if (indexOf < 0) {
                    throw new IllegalStateException("Unable to determine discreteValue index for: " + ut);
                }
                if (indexOf + 1 == discreteValues.size()) {
                    return new Interval(RangeBoundary.CLOSED, thisValue, thisValue, RangeBoundary.CLOSED, rule, col);
                }
                return new Interval(RangeBoundary.CLOSED, thisValue, (Comparable<?>) discreteValues.get(indexOf + 1), RangeBoundary.OPEN, rule, col);
            }
        } else if (ut.getOperator() == UnaryOperator.LTE) {
            return new Interval(minMax.getLowerBound().getBoundaryType(), minMax.getLowerBound().getValue(), valueFromNode(ut.getValue()), RangeBoundary.CLOSED, rule, col);
        } else if (ut.getOperator() == UnaryOperator.LT) {
            return new Interval(minMax.getLowerBound().getBoundaryType(), minMax.getLowerBound().getValue(), valueFromNode(ut.getValue()), RangeBoundary.OPEN, rule, col);
        } else if (ut.getOperator() == UnaryOperator.GT) {
            return new Interval(RangeBoundary.OPEN, valueFromNode(ut.getValue()), minMax.getUpperBound().getValue(), minMax.getUpperBound().getBoundaryType(), rule, col);
        } else if (ut.getOperator() == UnaryOperator.GTE) {
            return new Interval(RangeBoundary.CLOSED, valueFromNode(ut.getValue()), minMax.getUpperBound().getValue(), minMax.getUpperBound().getBoundaryType(), rule, col);
        } else if (ut.getValue() instanceof RangeNode) {
            RangeNode rangeNode = (RangeNode) ut.getValue();
            if (!(rangeNode.getStart() instanceof NullNode || rangeNode.getEnd() instanceof NullNode)) {
                return new Interval(rangeNode.getLowerBound() == IntervalBoundary.OPEN ? RangeBoundary.OPEN : RangeBoundary.CLOSED,
                                    valueFromNode(rangeNode.getStart()),
                                    valueFromNode(rangeNode.getEnd()),
                                    rangeNode.getUpperBound() == IntervalBoundary.OPEN ? RangeBoundary.OPEN : RangeBoundary.CLOSED,
                                    rule,
                                    col);
            } else if (rangeNode.getStart() instanceof NullNode) {
                return new Interval(minMax.getLowerBound().getBoundaryType(),
                                    minMax.getLowerBound().getValue(),
                                    valueFromNode(rangeNode.getEnd()),
                                    rangeNode.getUpperBound() == IntervalBoundary.OPEN ? RangeBoundary.OPEN : RangeBoundary.CLOSED,
                                    rule,
                                    col);
            } else {
                return new Interval(rangeNode.getLowerBound() == IntervalBoundary.OPEN ? RangeBoundary.OPEN : RangeBoundary.CLOSED,
                                    valueFromNode(rangeNode.getStart()),
                                    minMax.getUpperBound().getValue(),
                                    minMax.getUpperBound().getBoundaryType(),
                                    rule,
                                    col);
            }
        } else {
            throw new UnsupportedOperationException("UnaryTest type: " + ut);
        }
    }

    private Comparable<?> valueFromNode(BaseNode node, Visitor<Comparable<?>> visitor) {
        return node.accept(visitor);
    }

    private Comparable<?> valueFromNode(BaseNode node) {
        return valueFromNode(node, valueFromNodeVisitor);
    }
}

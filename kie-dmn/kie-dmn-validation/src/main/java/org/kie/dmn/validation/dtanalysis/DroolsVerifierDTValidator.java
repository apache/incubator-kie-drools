/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.verifier.api.Status;
import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.configuration.CheckConfiguration;
import org.drools.verifier.core.index.Index;
import org.drools.verifier.core.index.IndexImpl;
import org.drools.verifier.core.index.model.Rule;
import org.drools.verifier.core.main.Analyzer;
import org.drools.verifier.core.main.Reporter;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.codegen.feel11.ProcessedUnaryTest;
import org.kie.dmn.feel.lang.ast.UnaryTestListNode;
import org.kie.dmn.feel.lang.impl.UnaryTestInterpretedExecutableExpression;
import org.kie.dmn.model.api.DecisionRule;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.model.api.UnaryTests;
import org.kie.dmn.validation.dtanalysis.DMNDTAnalyserValueFromNodeVisitor.DMNDTAnalyserOutputClauseVisitor;
import org.kie.dmn.validation.dtanalysis.verifier.DMNCondition;
import org.kie.dmn.validation.dtanalysis.verifier.ResolverProvider;
import org.kie.dmn.validation.dtanalysis.verifier.Util;

public class DroolsVerifierDTValidator {

    private static final AnalyzerConfiguration analyzerConfiguration = new DMNAnalyzerConfiguration(makeDMNCheckConfiguration());
    private final org.kie.dmn.feel.FEEL feel;
    private final Util util;
    private final Set<Issue> result = new HashSet<>();

    public DroolsVerifierDTValidator(final FEEL feel,
                                     final DMNDTAnalyserValueFromNodeVisitor valueFromNodeVisitor,
                                     final DMNDTAnalyserOutputClauseVisitor outputClauseVisitor) {
        super();
        this.feel = feel;
        util = new Util(feel,
                        valueFromNodeVisitor,
                        outputClauseVisitor);
    }

    private static CheckConfiguration makeDMNCheckConfiguration() {
        final CheckConfiguration checkConfiguration = CheckConfiguration.newEmpty();

        checkConfiguration.getCheckConfiguration().add(CheckType.OVERLAPPING_ROWS);
        checkConfiguration.getCheckConfiguration().add(CheckType.MISSING_RANGE);
        checkConfiguration.getCheckConfiguration().add(CheckType.SUBSUMPTANT_ROWS);
//        checkConfiguration.getCheckConfiguration().add(CheckType.REDUNDANT_ROWS);

        return checkConfiguration;
    }

    public Set<Issue> validateDT(final DecisionTable dt) {
        final List<Rule> rules = new ArrayList<>();
        final Index index = new IndexImpl(analyzerConfiguration);

        final ResolverProvider resolverProvider = new ResolverProvider(dt,
                                                                       index,
                                                                       util,
                                                                       analyzerConfiguration);

        for (int jRowIdx = 0; jRowIdx < dt.getRule().size(); jRowIdx++) {
            int columnIndex = 0;

            final DecisionRule decisionRule = dt.getRule().get(jRowIdx);

            final Rule row = new Rule(jRowIdx,
                                      analyzerConfiguration);

            for (final UnaryTests ie : decisionRule.getInputEntry()) {
                visitInput(resolverProvider, columnIndex, row, ie);

                columnIndex++;
            }
            for (final LiteralExpression literalExpression : decisionRule.getOutputEntry()) {

                visitOutput(resolverProvider, columnIndex, row, literalExpression);
                columnIndex++;
            }

            rules.add(row);
        }

        final Analyzer analyzer = new Analyzer(makeReporter(),
                                               rules,
                                               analyzerConfiguration);

        analyzer.start();

        return result;
    }

    private void visitOutput(final ResolverProvider resolverProvider,
                             final int columnIndex,
                             final Rule row,
                             final LiteralExpression literalExpression) {
        final DMNAction action = resolverProvider.getOutputResolver()
                .with(literalExpression)
                .with(columnIndex)
                .with(row)
                .build();

        row.getActions().add(action);
    }

    private void visitInput(final ResolverProvider resolverProvider,
                            final int columnIndex,
                            final Rule row,
                            final UnaryTests ie) {
        final ProcessedUnaryTest compileUnaryTests = (ProcessedUnaryTest) feel.compileUnaryTests(ie.getText(), feel.newCompilerContext());
        final UnaryTestInterpretedExecutableExpression interpreted = compileUnaryTests.getInterpreted();
        final UnaryTestListNode utln = (UnaryTestListNode) interpreted.getASTNode();

        for (DMNCondition dmnCondition : resolverProvider.getInputBuilder()
                .with(columnIndex)
                .with(row)
                .build(utln)) {

            row.getConditions().add(dmnCondition);
        }
    }

    private Reporter makeReporter() {
        return new Reporter() {

            @Override
            public void sendReport(final Set<Issue> issues) {
                result.addAll(issues);
            }

            @Override
            public void sendStatus(final Status status) {
                // One bulk run so no updates needed.
            }
        };
    }
}

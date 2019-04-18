/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dmn.validation.dtanalysis.verifier;

import java.util.ArrayList;
import java.util.List;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.model.ColumnType;
import org.drools.verifier.core.index.model.Rule;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.DashNode;
import org.kie.dmn.feel.lang.ast.NullNode;
import org.kie.dmn.feel.lang.ast.RangeNode;
import org.kie.dmn.feel.lang.ast.UnaryTestListNode;
import org.kie.dmn.feel.lang.ast.UnaryTestNode;

import static org.kie.dmn.validation.dtanalysis.verifier.OperatorResolver.validatorStringOperatorFromUTOperator;

public class InputBuilder {

    private final Util util;
    private final ResolverProvider resolverProvider;
    private final AnalyzerConfiguration configuration;
    private Rule row;
    private int columnIndex;

    public InputBuilder(final Util util,
                        final ResolverProvider resolverProvider,
                        final AnalyzerConfiguration configuration) {

        this.util = util;
        this.resolverProvider = resolverProvider;
        this.configuration = configuration;
    }

    public List<DMNCondition> build(final UnaryTestListNode utln) {

        final List<DMNCondition> result = new ArrayList<>();
        if (isCroupingPossible(utln)) {
            result.addAll(build(utln,
                                utln.isNegated()));
        } else {

            for (final BaseNode node : utln.getElements()) {
                if (node instanceof DashNode) {
                    result.addAll(buildDash());
                    continue;
                }

                result.addAll(build((UnaryTestNode) node,
                                    utln.isNegated()));
            }
        }

        return result;
    }

    public InputBuilder with(final Rule row) {
        this.row = row;
        return this;
    }

    private boolean isCroupingPossible(final UnaryTestListNode utln) {
        return utln.getElements().size() > 1 && utln.getElements().stream().allMatch(x -> x instanceof UnaryTestNode)
                && utln.getElements().stream().allMatch(x -> ((UnaryTestNode) x).getOperator().equals(UnaryTestNode.UnaryOperator.EQ));
    }

    public List<DMNCondition> buildDash() {
        final List<DMNCondition> result = new ArrayList<>();

        final DMNCell dmnCell = resolverProvider.getDMNCellResolver()
                .with(columnIndex)
                .with(row)
                .resolver();
        final DMNCondition dmnCondition = new DMNCondition(resolverProvider.getColumnResolver().resolve(columnIndex, ColumnType.LHS),
                                                           dmnCell,
                                                           "in",
                                                           new Values(),
                                                           configuration);
        result.add(dmnCondition);
        dmnCell.getConditions().add(dmnCondition);

        return result;
    }

    public List<DMNCondition> build(final UnaryTestListNode unaryTestListNodet,
                                    final boolean isNegated) {
        final List<DMNCondition> result = new ArrayList<>();

        final DMNCell dmnCell = resolverProvider.getDMNCellResolver()
                .with(columnIndex)
                .with(row)
                .resolver();
        Values<?> values = util.valuesFromUnaryTestListNode(unaryTestListNodet);
        String operator = isNegated ? "not in" : "in";
        final DMNCondition dmnCondition = new DMNCondition(resolverProvider.getColumnResolver().resolve(columnIndex, ColumnType.LHS),
                                                           dmnCell,
                                                           operator,
                                                           values,
                                                           configuration);
        result.add(dmnCondition);
        dmnCell.getConditions().add(dmnCondition);

        return result;
    }

    public List<DMNCondition> build(final UnaryTestNode ut,
                                    final boolean isNegated) {
        final List<DMNCondition> result = new ArrayList<>();

        final DMNCell dmnCell = resolverProvider.getDMNCellResolver()
                .with(columnIndex)
                .with(row)
                .resolver();

        if (ut.getOperator() == UnaryTestNode.UnaryOperator.EQ || ut.getOperator() == UnaryTestNode.UnaryOperator.GT || ut.getOperator() == UnaryTestNode.UnaryOperator.GTE || ut.getOperator() == UnaryTestNode.UnaryOperator.LT || ut
                .getOperator() == UnaryTestNode.UnaryOperator.LTE) {

            final DMNCondition dmnCondition = new DMNCondition(resolverProvider.getColumnResolver().resolve(columnIndex, ColumnType.LHS),
                                                               dmnCell,
                                                               validatorStringOperatorFromUTOperator(isNegated,
                                                                                                     ut.getOperator()),
                                                               util.valuesFromNode(ut.getValue()),
                                                               configuration);
            result.add(dmnCondition);
            dmnCell.getConditions().add(dmnCondition);
        } else if (ut.getValue() instanceof RangeNode) {

            final RangeNode rangeNode = (RangeNode) ut.getValue();

            if (!(rangeNode.getStart() instanceof NullNode)) {
                final DMNCondition dmnCondition1 = new DMNCondition(resolverProvider.getColumnResolver().resolve(columnIndex, ColumnType.LHS),
                                                                    dmnCell,
                                                                    rangeNode.getLowerBound() == RangeNode.IntervalBoundary.OPEN ? ">" : ">=",
                                                                    util.valuesFromNode(rangeNode.getStart()),
                                                                    configuration);
                result.add(dmnCondition1);
                dmnCell.getConditions().add(dmnCondition1);
            }

            if (!(rangeNode.getEnd() instanceof NullNode)) {
                final DMNCondition dmnCondition2 = new DMNCondition(resolverProvider.getColumnResolver().resolve(columnIndex, ColumnType.LHS),
                                                                    dmnCell,
                                                                    rangeNode.getUpperBound() == RangeNode.IntervalBoundary.OPEN ? "<" : "<=",
                                                                    util.valuesFromNode(rangeNode.getEnd()),
                                                                    configuration);
                result.add(dmnCondition2);
                dmnCell.getConditions().add(dmnCondition2);
            }
        }

        return result;
    }

    public InputBuilder with(final int columnIndex) {
        this.columnIndex = columnIndex;
        return this;
    }
}

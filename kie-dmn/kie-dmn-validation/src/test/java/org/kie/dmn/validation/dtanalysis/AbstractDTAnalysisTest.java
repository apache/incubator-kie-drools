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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.printer.PrettyPrinterConfiguration;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.table.TableRow;
import net.steppschuh.markdowngenerator.util.StringUtil;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.model.api.DecisionRule;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.InputClause;
import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.model.api.OutputClause;
import org.kie.dmn.model.api.RuleAnnotation;
import org.kie.dmn.model.api.RuleAnnotationClause;
import org.kie.dmn.model.api.UnaryTests;
import org.kie.dmn.validation.AbstractValidatorTest;
import org.kie.dmn.validation.ValidatorUtil;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;
import org.kie.dmn.validation.dtanalysis.model.Hyperrectangle;
import org.kie.dmn.validation.dtanalysis.model.Overlap;
import org.kie.dmn.validation.dtanalysis.utils.DTAnalysisMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public abstract class AbstractDTAnalysisTest extends AbstractValidatorTest {

    public static final Logger LOG = LoggerFactory.getLogger(AbstractDTAnalysisTest.class);

    protected DTAnalysis getAnalysis(List<DMNMessage> dmnMessages, String id) {
        assertThat("Expected to find DTAnalysis but messages are empty.", dmnMessages, not(empty()));

        if (LOG.isDebugEnabled() ) {
            LOG.debug("List<DMNMessage> dmnMessages: \n{}", ValidatorUtil.formatMessages(dmnMessages));
        }

        Map<String, DTAnalysis> as = new HashMap<>();
        for (DMNMessage dmnMessage : dmnMessages) {
            if (dmnMessage.getSourceId().equals(id) && dmnMessage instanceof DMNDTAnalysisMessage) {
                DMNDTAnalysisMessage dmndtAnalysisMessage = (DMNDTAnalysisMessage) dmnMessage;
                if (as.containsKey(id)) {
                    assertThat("Inconsistency detected", as.get(id), is(dmndtAnalysisMessage.getAnalysis()));
                } else {
                    as.put(id, dmndtAnalysisMessage.getAnalysis());
                }
            }
        }

        DTAnalysis analysis = as.get(id);
        assertThat("Null analysis value for key.", analysis, notNullValue());

        debugAnalysis(analysis);

        return analysis;
    }

    protected void debugValidatorMsg(List<DMNMessage> dmnMessages) {
        if (!LOG.isDebugEnabled()) {
            return;
        }
        StringBuilder sbGaps = new StringBuilder("Validator messages:\n");
        for (DMNMessage msg : dmnMessages) {
            sbGaps.append(String.format("%8s %s", msg.getLevel(), msg.getMessage()));
            sbGaps.append("\n");
        }
        LOG.debug(sbGaps.toString());
    }

    protected void debugValidatorMsg(DTAnalysis analysis, List<DMNMessage> dmnMessages) {
        if (!LOG.isDebugEnabled()) {
            return;
        }

        DecisionTable dt = (DecisionTable) analysis.getSource();
        List<String> headers = new ArrayList<>();
        for (InputClause input : dt.getInput()) {
            headers.add(input.getInputExpression().getText());
        }
        if (dt.getOutput().size() == 1) {
            headers.add(dt.getOutputLabel());
        } else {
            for (OutputClause output : dt.getOutput()) {
                headers.add(output.getName());
            }
        }
        for (RuleAnnotationClause a : dt.getAnnotation()) {
            headers.add(a.getName());
        }
        Table.Builder mdTable = new Table.Builder();
        List<Integer> alignments = new ArrayList<>();
        for (int i = 0; i < headers.size() - dt.getAnnotation().size(); i++) {
            alignments.add(Table.ALIGN_CENTER);
        }
        for (int i = 0; i < dt.getAnnotation().size(); i++) {
            alignments.add(Table.ALIGN_LEFT);
        }
        mdTable.withAlignments(alignments);
        mdTable.addRow(headers.toArray());
        for (DecisionRule rule : dt.getRule()) {
            List<String> row = new ArrayList<>();
            for (UnaryTests i : rule.getInputEntry()) {
                row.add(i.getText());
            }
            for (LiteralExpression o : rule.getOutputEntry()) {
                row.add(o.getText());
            }
            for (RuleAnnotation a : rule.getAnnotationEntry()) {
                row.add(a.getText());
            }
            mdTable.addRow(row.toArray());
        }
        Table.Builder metaMdTable = new Table.Builder();
        metaMdTable.withAlignments(alignments);
        metaMdTable.addRow(headers.subList(0, headers.size() - dt.getAnnotation().size()).toArray());
        List<String> row = new ArrayList<>();
        for (InputClause input : dt.getInput()) {
            row.add("INPUT");
        }
        for (OutputClause output : dt.getOutput()) {
            row.add("OUTPUT");
        }
        metaMdTable.addRow(row.toArray());
        row = new ArrayList<>();
        for (InputClause input : dt.getInput()) {
            row.add(input.getInputExpression().getTypeRef().toString());
        }
        if (dt.getOutput().size() == 1) {
            row.add(dt.getTypeRef().toString());
        } else {
            for (OutputClause output : dt.getOutput()) {
                row.add(output.getTypeRef().toString());
            }
        }
        metaMdTable.addRow(row.toArray());
        row = new ArrayList<>();
        for (InputClause input : dt.getInput()) {
            row.add(input.getInputValues() != null ? input.getInputValues().getText() : null);
        }
        for (OutputClause output : dt.getOutput()) {
            row.add(output.getOutputValues() != null ? output.getOutputValues().getText() : null);
        }
        metaMdTable.addRow(row.toArray());

        StringBuilder sb = new StringBuilder("\nTable metadata:\n");
        sb.append(metaMdTable.build());
        sb.append("\n");
        sb.append("\n");
        sb.append("Hit Policy: ").append(dt.getHitPolicy()).append("\n");
        sb.append(mdTable.build());
        sb.append("\n");
        sb.append("\n");
        sb.append("Validator messages:\n");
        for (DMNMessage msg : dmnMessages) {
            sb.append(String.format("%8s %s", msg.getLevel(), msg.getMessage()));
            sb.append("\n");
        }
        LOG.debug(sb.toString());
    }

    public static class MHTable extends Table {

        private int hLines;

        public MHTable(int hLines) {
            super();
            this.hLines = hLines;
        }

        public static class Builder {

            private MHTable table;

            public Builder(int hLines) {
                table = new MHTable(hLines);
            }

            public Builder addRow(Object... objects) {
                TableRow tableRow = new TableRow(Arrays.asList(objects));
                table.getRows().add(tableRow);
                return this;
            }

            public Builder withAlignments(List<Integer> alignments) {
                table.setAlignments(alignments);
                return this;
            }

            public Table build() {
                return table;
            }

        }

        public String serialize() {
            Map<Integer, Integer> columnWidths = getColumnWidths(getRows(), getMinimumColumnWidth());

            StringBuilder sb = new StringBuilder();

            String headerSeperator = generateHeaderSeperator(columnWidths, getAlignments());
            boolean headerSeperatorAdded = !isFirstRowHeader();
            if (!isFirstRowHeader()) {
                sb.append(headerSeperator).append(System.lineSeparator());
            }

            for (int i = 0; i < getRows().size(); i++) {
                TableRow row = getRows().get(i);
                for (int columnIndex = 0; columnIndex < columnWidths.size(); columnIndex++) {
                    sb.append(SEPERATOR);

                    String value = "";
                    if (row.getColumns().size() > columnIndex) {
                        Object valueObject = row.getColumns().get(columnIndex);
                        if (valueObject != null) {
                            value = valueObject.toString();
                        }
                    }

                    if (value.equals(getTrimmingIndicator())) {
                        value = StringUtil.fillUpLeftAligned(value, getTrimmingIndicator(), columnWidths.get(columnIndex));
                        value = StringUtil.surroundValueWith(value, WHITESPACE);
                    } else {
                        int alignment = getAlignment(getAlignments(), columnIndex);
                        value = StringUtil.surroundValueWith(value, WHITESPACE);
                        value = StringUtil.fillUpAligned(value, WHITESPACE, columnWidths.get(columnIndex) + 2, alignment);
                    }

                    sb.append(value);

                    if (columnIndex == row.getColumns().size() - 1) {
                        sb.append(SEPERATOR);
                    }
                }

                if (getRows().indexOf(row) < getRows().size() - 1) {
                    sb.append(System.lineSeparator());
                }

                if (i == hLines - 1 && !headerSeperatorAdded) {
                    sb.append(headerSeperator).append(System.lineSeparator());
                    headerSeperatorAdded = true;
                }
            }
            return sb.toString();
        }
    }

    protected void debugAnalysis(DTAnalysis analysis) {
        if (!LOG.isDebugEnabled()) {
            return;
        }
        StringBuilder sbGaps = new StringBuilder("\nGaps:\n");
        for (Hyperrectangle gap : analysis.getGaps()) {
            sbGaps.append(gap.toString());
            sbGaps.append("\n");
        }
        LOG.debug(sbGaps.toString());

        PrettyPrinterConfiguration prettyPrintConfig = new PrettyPrinterConfiguration();
        prettyPrintConfig.setColumnAlignFirstMethodChain(true);
        prettyPrintConfig.setColumnAlignParameters(true);

        Expression printGaps = DTAnalysisMeta.printGaps(analysis);
        LOG.debug("\n" + printGaps.toString(prettyPrintConfig));

        StringBuilder sbOverlaps = new StringBuilder("\nOverlaps:\n");
        for (Overlap overlap : analysis.getOverlaps()) {
            sbOverlaps.append(overlap.toString());
            sbOverlaps.append("\n");
        }
        LOG.debug(sbOverlaps.toString());

        Expression printOverlaps = DTAnalysisMeta.printOverlaps(analysis);
        LOG.debug("\n" + printOverlaps.toString(prettyPrintConfig));
    }
}

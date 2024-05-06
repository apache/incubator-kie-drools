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

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.InputClause;
import org.kie.dmn.model.api.OutputClause;
import org.kie.dmn.validation.dtanalysis.AbstractDTAnalysisTest;
import org.kie.dmn.validation.dtanalysis.mcdc.MCDCAnalyser.PosNegBlock;
import org.kie.dmn.validation.dtanalysis.mcdc.MCDCAnalyser.Record;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;
import static org.kie.dmn.validation.DMNValidator.Validation.COMPUTE_DECISION_TABLE_MCDC;

public class ExampleMCDCTest extends AbstractDTAnalysisTest {

    private static final Logger LOG = LoggerFactory.getLogger(ExampleMCDCTest.class);

    @Test
    void test1() throws Exception {
        final String resourceFileName = "example1.dmn";
        List<DMNMessage> validate = validator.validate(getReader(resourceFileName), ANALYZE_DECISION_TABLE, COMPUTE_DECISION_TABLE_MCDC);

        DTAnalysis analysis = getAnalysis(validate, "_452a0adf-dd49-47c3-b02d-fe0ad45902c7");
        Collection<Record> mcdcCases = computeMCDCCases(analysis.getMCDCSelectedBlocks());
        assertThat(mcdcCases).hasSize(16);

        assertMCDCCases(resourceFileName, analysis.getSource(), mcdcCases);
        //debugOutputAndOpenXLSX(analysis.getSource(), analysis.getMCDCSelectedBlocks());
        String mcdc2tck = MCDC2TCKGenerator.mcdc2tck(analysis.getSource(), analysis.getMCDCSelectedBlocks());
        //debugTCKXML(analysis.getSource(), mcdc2tck);
    }

    @Test
    void test2() throws Exception {
        final String resourceFileName = "example2.dmn";
        List<DMNMessage> validate = validator.validate(getReader(resourceFileName), ANALYZE_DECISION_TABLE, COMPUTE_DECISION_TABLE_MCDC);

        DTAnalysis analysis = getAnalysis(validate, "_e31c78b7-63ef-4112-a0bc-b0546043ebe9");
        Collection<Record> mcdcCases = computeMCDCCases(analysis.getMCDCSelectedBlocks());
        assertThat(mcdcCases).hasSize(14);

        assertMCDCCases(resourceFileName, analysis.getSource(), mcdcCases);
        //debugOutputAndOpenXLSX(analysis.getSource(), analysis.getMCDCSelectedBlocks());
        String mcdc2tck = MCDC2TCKGenerator.mcdc2tck(analysis.getSource(), analysis.getMCDCSelectedBlocks());
        //debugTCKXML(analysis.getSource(), mcdc2tck);
    }

    public static class MCDCListener implements DMNRuntimeEventListener {

        /** The listener reports the ID accordingly to the DMN Specification, that is 1-based. */
        public final List<Integer> selectedRule = new ArrayList<>();

        @Override
        public void afterEvaluateDecisionTable(AfterEvaluateDecisionTableEvent event) {
            selectedRule.addAll(event.getSelected());
        }

    }

    private void assertMCDCCases(final String resourceFileName, DecisionTable sourceDT, Collection<Record> mcdcCases) {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime(resourceFileName, ExampleMCDCTest.class);
        MCDCListener mcdcListener = new MCDCListener();
        runtime.addListener(mcdcListener);
        DMNModel dmnModel = runtime.getModels().get(0);
        assertThat(dmnModel).isNotNull();

        for (Record mcdcCase : mcdcCases) {
            mcdcListener.selectedRule.clear();
            DMNContext context = runtime.newContext();
            for (int i = 0; i < mcdcCase.enums.length; i++) {
                context.set(sourceDT.getInput().get(i).getInputExpression().getText(), mcdcCase.enums[i]);
            }
            DMNResult evaluateAll = runtime.evaluateAll(dmnModel, context);
            LOG.debug("{}", evaluateAll);
            assertThat(mcdcListener.selectedRule).contains(mcdcCase.ruleIdx + 1);
        }
    }

    private Collection<Record> computeMCDCCases(List<PosNegBlock> mcdcSelectedBlocks) {
        Set<Record> mcdcRecords = new LinkedHashSet<>();
        for (PosNegBlock b : mcdcSelectedBlocks) {
            mcdcRecords.add(b.posRecord);
            mcdcRecords.addAll(b.negRecords);
        }
        return mcdcRecords;
    }

    public static void debugOutputAndOpenXLSX(DecisionTable dt, List<PosNegBlock> selectedBlocks) {
        XSSFWorkbook wb = new XSSFWorkbook();
        CreationHelper ch = wb.getCreationHelper();
        CellStyle styleColumn = wb.createCellStyle();
        styleColumn.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        styleColumn.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = wb.createFont();
        font.setColor(IndexedColors.RED.getIndex());
        CellStyle styleRepeated = wb.createCellStyle();
        styleRepeated.setFont(font);

        Sheet sheet = wb.createSheet("MC DC Analysis");
        int r = 0, c = 0;
        Row row = sheet.createRow(r++);
        row.createCell(c++).setCellValue(ch.createRichTextString("Rule:"));
        for (InputClause input : dt.getInput()) {
            row.createCell(c++).setCellValue(input.getInputExpression().getText());
        }
        for (OutputClause output : dt.getOutput()) {
            row.createCell(c++).setCellValue(dt.getOutputLabel());
        }
        row.createCell(c++).setCellValue(ch.createRichTextString("Rule:"));
        for (InputClause input : dt.getInput()) {
            row.createCell(c++).setCellValue(input.getInputExpression().getText());
        }
        for (OutputClause output : dt.getOutput()) {
            row.createCell(c++).setCellValue(dt.getOutputLabel());
        }
        Set<Record> mcdcRecords = new LinkedHashSet<>();
        for (PosNegBlock b : selectedBlocks) {
            row = sheet.createRow(r++);
            c = 0;
            boolean add = mcdcRecords.add(b.posRecord);
            Cell ruleIdxCell = row.createCell(c++);
            ruleIdxCell.setCellValue(b.posRecord.ruleIdx + 1);
            if (!add) {
                ruleIdxCell.setCellStyle(styleRepeated);
            }
            for (int i = 0; i < b.posRecord.enums.length; i++) {
                Object en = b.posRecord.enums[i];
                Cell enCell = row.createCell(c++);
                enCell.setCellValue(en.toString());
                if (!add) {
                    enCell.setCellStyle(styleRepeated);
                }
                if (b.cMarker == i) {
                    enCell.setCellStyle(styleColumn);
                }
            }
            for (Object out : b.posRecord.output) {
                Cell outCell = row.createCell(c++);
                outCell.setCellValue(out.toString());
                if (!add) {
                    outCell.setCellStyle(styleRepeated);
                }
            }
            for (int i = 0; i < b.negRecords.size(); i++) {
                Record negRecord = b.negRecords.get(i);
                if (i != 0) {
                    row = sheet.createRow(r++);
                    c = 0 + 1 + b.posRecord.enums.length + b.posRecord.output.size();
                }
                add = mcdcRecords.add(negRecord);
                ruleIdxCell = row.createCell(c++);
                ruleIdxCell.setCellValue(negRecord.ruleIdx + 1);
                if (!add) {
                    ruleIdxCell.setCellStyle(styleRepeated);
                }
                for (Object en : negRecord.enums) {
                    Cell enCell = row.createCell(c++);
                    enCell.setCellValue(en.toString());
                    if (!add) {
                        enCell.setCellStyle(styleRepeated);
                    }
                }
                for (Object out : negRecord.output) {
                    Cell outCell = row.createCell(c++);
                    outCell.setCellValue(out.toString());
                    if (!add) {
                        outCell.setCellStyle(styleRepeated);
                    }
                }
            }
        }
        File file;
        try {
            file = Files.createTempFile("mcdc " + dt.getOutputLabel(), ".xlsx").toFile();
            OutputStream fileOut = new FileOutputStream(file);
            wb.write(fileOut);
            wb.close();
        } catch (IOException e) {
            e.printStackTrace();
            // terminate early;
            return;
        }
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new UnsupportedOperationException();
        }
        LOG.trace(System.getProperty("java.io.tmpdir"));
    }

    public static void debugTCKXML(DecisionTable dt, String xmlContent) throws Exception {
        File file = Files.createTempFile("mcdcTCK " + dt.getOutputLabel(), ".xml").toFile();
        FileWriter fw = new FileWriter(file);
        fw.append(xmlContent);
        fw.close();
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new UnsupportedOperationException();
        }
        LOG.debug(System.getProperty("java.io.tmpdir"));
    }
}

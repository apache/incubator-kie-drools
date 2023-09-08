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
package org.kie.dmn.xls2dmn.cli;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.drools.decisiontable.parser.DecisionTableParser;
import org.drools.decisiontable.parser.xls.ExcelParser;
import org.drools.template.parser.DataListener;
import org.drools.template.parser.DecisionTableParseException;
import org.kie.dmn.api.marshalling.DMNMarshaller;
import org.kie.dmn.backend.marshalling.v1x.DMNMarshallerFactory;
import org.kie.dmn.feel.codegen.feel11.CodegenStringUtil;
import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.DRGElement;
import org.kie.dmn.model.api.Decision;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.HitPolicy;
import org.kie.dmn.model.api.InformationItem;
import org.kie.dmn.model.api.InformationRequirement;
import org.kie.dmn.model.api.InputClause;
import org.kie.dmn.model.api.InputData;
import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.model.api.OutputClause;
import org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase;
import org.kie.dmn.model.v1_2.TDMNElementReference;
import org.kie.dmn.model.v1_2.TDecision;
import org.kie.dmn.model.v1_2.TDecisionTable;
import org.kie.dmn.model.v1_2.TDefinitions;
import org.kie.dmn.model.v1_2.TInformationItem;
import org.kie.dmn.model.v1_2.TInformationRequirement;
import org.kie.dmn.model.v1_2.TInputClause;
import org.kie.dmn.model.v1_2.TInputData;
import org.kie.dmn.model.v1_2.TLiteralExpression;
import org.kie.dmn.model.v1_2.TOutputClause;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XLS2DMNParser implements DecisionTableParser {

    private static final Logger LOG = LoggerFactory.getLogger(XLS2DMNParser.class);
    private final File outFile;

    public XLS2DMNParser(File outFile) {
        this.outFile = outFile;
    }

    @Override
    public void parseFile(InputStream inStream) {
        try {
            parseWorkbook("xls2dmn", WorkbookFactory.create(inStream));
        } catch (IOException e) {
            throw new DecisionTableParseException(
                    "Failed to open Excel stream, " + "please check that the content is xls97 format.", e);
        }
    }

    @Override
    public void parseFile(File file) {
        try {
            parseWorkbook(removeTrailingExtension(file.getName()), WorkbookFactory.create(file, null, true));
        } catch (IOException e) {
            throw new DecisionTableParseException(
                    "Failed to open Excel stream, " + "please check that the content is xls97 format.", e);
        }
    }

    public void parseWorkbook(String dmnModelName, Workbook workbook) {
        Map<String, List<String>> overview = new HashMap<>();
        DataFormatter formatter = new DataFormatter();
        for (int s = 0; s < workbook.getNumberOfSheets(); s++) {
            Sheet sheet = workbook.getSheetAt(s);
            int maxRows = sheet.getLastRowNum();

            for (int i = 0; i <= maxRows; i++) {
                Row row = sheet.getRow(i);
                int lastCellNum = row != null ? row.getLastCellNum() : 0;
                if (lastCellNum == 0) {
                    continue; // skip empty row.
                }
                List<String> header = new ArrayList<>();
                for (Cell c : row) {
                    String text = formatter.formatCellValue(c);
                    header.add(text);
                }
                overview.put(sheet.getSheetName(), header);
                break; // header found.
            }
        }
        overview.entrySet().forEach(e -> LOG.debug("{}", e));
        Map<String, DTHeaderInfo> headerInfos = generateDTHeaderInfo(overview);
        LOG.info("Sheets have been indexed as:");
        headerInfos.entrySet().forEach(e -> LOG.info("{}", e));
        Definitions definitions = new TDefinitions();
        setDefaultNSContext(definitions);
        definitions.setId("dmnid_" + dmnModelName);
        definitions.setName(dmnModelName);
        String namespace = "xls2dmn_" + UUID.randomUUID();
        definitions.setNamespace(namespace);
        definitions.getNsContext().put(XMLConstants.DEFAULT_NS_PREFIX, namespace);
        definitions.setExporter("kie-dmn-xls2dmn");
        appendInputData(definitions, headerInfos);
        appendDecisionDT(definitions, headerInfos);
        final Map<String, List<DataListener>> sheetListeners = new HashMap<>();
        for (DTHeaderInfo hi : headerInfos.values()) {
            String sheetName = hi.getSheetName();
            DRGElement drgElem = definitions.getDrgElement().stream().filter(e -> e.getName().equals(sheetName)).findFirst().orElseThrow(() -> new XLS2DMNException("Unable to locate DRG element for sheet: " + sheetName));
            DecisionTable dt = (DecisionTable) ((Decision) drgElem).getExpression();
            DTSheetListener listener = new DTSheetListener(dt, hi);
            sheetListeners.put(sheetName, List.of(listener));
        }
        new ExcelParser(sheetListeners).parseWorkbook(workbook);
        DMNMarshaller dmnMarshaller = DMNMarshallerFactory.newDefaultMarshaller();
        String xml = dmnMarshaller.marshal(definitions);
        try {
            Files.write(outFile.toPath(), xml.getBytes());
        } catch (IOException e) {
            LOG.error("Unable to write to outputfile.", e);
            throw new XLS2DMNException("Unable to write to outputfile", e);
        }
        LOG.debug("output XML can be displayed at trace level",xml);
        LOG.trace("output XML:\n{}",xml);
    }

    private void appendDecisionDT(Definitions definitions, Map<String, DTHeaderInfo> headerInfos) {
        for (DTHeaderInfo hi : headerInfos.values()) {
            Decision decision = new TDecision();
            decision.setName(hi.getSheetName());
            decision.setId("d_" + CodegenStringUtil.escapeIdentifier(hi.getSheetName()));
            InformationItem variable = new TInformationItem();
            variable.setName(hi.getSheetName());
            variable.setId("dvar_" + CodegenStringUtil.escapeIdentifier(hi.getSheetName()));
            variable.setTypeRef(new QName("Any"));
            decision.setVariable(variable);
            for (String ri : hi.getRequiredInput()) {
                InformationRequirement ir = new TInformationRequirement();
                DMNElementReference er = new TDMNElementReference();
                er.setHref("#id_" + CodegenStringUtil.escapeIdentifier(ri));
                ir.setRequiredInput(er);
                decision.getInformationRequirement().add(ir);
            }
            for (String ri : hi.getRequiredDecision()) {
                InformationRequirement ir = new TInformationRequirement();
                DMNElementReference er = new TDMNElementReference();
                er.setHref("#d_" + CodegenStringUtil.escapeIdentifier(ri));
                ir.setRequiredDecision(er);
                decision.getInformationRequirement().add(ir);
            }
            DecisionTable dt = new TDecisionTable();
            dt.setOutputLabel(hi.getSheetName());
            dt.setId("ddt_" + CodegenStringUtil.escapeIdentifier(hi.getSheetName()));
            dt.setHitPolicy(HitPolicy.ANY);
            for (String req : hi.getOriginal().subList(0, hi.gethIndex())) {
                InputClause ic = new TInputClause();
                ic.setLabel(req);
                LiteralExpression le = new TLiteralExpression();
                le.setText(req);
                ic.setInputExpression(le);
                dt.getInput().add(ic);
            }
            OutputClause oc = new TOutputClause();
            dt.getOutput().add(oc);
            decision.setExpression(dt);
            definitions.getDrgElement().add(decision);
        }
    }

    private void setDefaultNSContext(Definitions definitions) {
        Map<String, String> nsContext = definitions.getNsContext();
        nsContext.put("feel", KieDMNModelInstrumentedBase.URI_FEEL);
        nsContext.put("dmn", KieDMNModelInstrumentedBase.URI_DMN);
        nsContext.put("dmndi", KieDMNModelInstrumentedBase.URI_DMNDI);
        nsContext.put("di", KieDMNModelInstrumentedBase.URI_DI);
        nsContext.put("dc", KieDMNModelInstrumentedBase.URI_DC);
    }

    private void appendInputData(Definitions definitions, Map<String, DTHeaderInfo> headerInfos) {
        Set<String> usedRI = new LinkedHashSet<>();
        for ( DTHeaderInfo hi : headerInfos.values()) {
            for(String ri : hi.getRequiredInput()) {
                if (!usedRI.contains(ri)) {
                    InputData id = new TInputData();
                    id.setName(ri);
                    id.setId("id_"+CodegenStringUtil.escapeIdentifier(ri));
                    InformationItem variable = new TInformationItem();
                    variable.setName(ri);
                    variable.setId("idvar_"+CodegenStringUtil.escapeIdentifier(ri));
                    variable.setTypeRef(new QName("Any"));
                    id.setVariable(variable);
                    definitions.getDrgElement().add(id);
                }
                usedRI.add(ri);
            }
        }
    }

    private Map<String, DTHeaderInfo> generateDTHeaderInfo(Map<String, List<String>> overview) {
        Map<String, DTHeaderInfo> result = new HashMap<>();
        for (Entry<String, List<String>> kv : overview.entrySet()) {
            String sheetName = kv.getKey();
            List<String> requiredInput = new ArrayList<>();
            List<String> requiredDecision = new ArrayList<>();
            int hIndex = kv.getValue().indexOf(sheetName);
            if (hIndex < 0) {
                throw new XLS2DMNException("There is no result output column in sheet: " + sheetName);
            }
            if (hIndex != kv.getValue().size()) {
                for (int i = hIndex+1; i < kv.getValue().size(); i++) {
                    String afterIndexValue = kv.getValue().get(i);
                    if (!(afterIndexValue == null || afterIndexValue.isEmpty())) {
                        throw new XLS2DMNException("Decision name was not last, on the right I found " + afterIndexValue);
                    }
                }
            }
            for (int i = 0; i < hIndex; i++) {
                String hValue = kv.getValue().get(i);
                if (overview.containsKey(hValue)) {
                    requiredDecision.add(hValue);
                } else {
                    requiredInput.add(hValue);
                }
            }
            DTHeaderInfo info = new DTHeaderInfo(sheetName, kv.getValue(), hIndex, requiredInput, requiredDecision);
            result.put(sheetName, info);
        }
        return result;
    }
    
    public static String removeTrailingExtension(String filename) {
        if (filename.endsWith(".xls") || filename.endsWith(".xlsx") ) {
            return filename.substring(0, filename.lastIndexOf("."));
        }
        return filename;
    }

}
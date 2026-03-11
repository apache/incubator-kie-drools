/*
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
package org.drools.docs.parser;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.drools.docs.model.DecisionModelDoc;
import org.drools.docs.model.DecisionModelDoc.DecisionDoc;
import org.drools.docs.model.DecisionModelDoc.DecisionTableDoc;
import org.drools.docs.model.DecisionModelDoc.InputDataDoc;
import org.drools.docs.model.DecisionModelDoc.ItemDefinitionDoc;
import org.kie.dmn.api.marshalling.DMNMarshaller;
import org.kie.dmn.backend.marshalling.v1x.DMNMarshallerFactory;
import org.kie.dmn.model.api.DRGElement;
import org.kie.dmn.model.api.Decision;
import org.kie.dmn.model.api.DecisionRule;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.api.InformationRequirement;
import org.kie.dmn.model.api.InputClause;
import org.kie.dmn.model.api.InputData;
import org.kie.dmn.model.api.ItemDefinition;
import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.model.api.OutputClause;
import org.kie.dmn.model.api.UnaryTests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses DMN files into the documentation model using the KIE DMN marshaller.
 */
public class DmnDocParser {

    private static final Logger LOG = LoggerFactory.getLogger(DmnDocParser.class);

    public DecisionModelDoc parse(Path dmnFile) throws IOException {
        String content = Files.readString(dmnFile);
        DecisionModelDoc doc = parse(content);
        doc.setSourceFile(dmnFile.getFileName().toString());
        return doc;
    }

    public DecisionModelDoc parse(String dmnXml) {
        try {
            DMNMarshaller marshaller = DMNMarshallerFactory.newDefaultMarshaller();
            Definitions definitions = marshaller.unmarshal(new StringReader(dmnXml));

            if (definitions == null) {
                throw new DocParseException("Failed to parse DMN content");
            }

            return convertDefinitions(definitions);
        } catch (DocParseException e) {
            throw e;
        } catch (Exception e) {
            throw new DocParseException("Error parsing DMN content", e);
        }
    }

    private DecisionModelDoc convertDefinitions(Definitions definitions) {
        DecisionModelDoc doc = new DecisionModelDoc();
        doc.setName(definitions.getName());
        doc.setNamespace(definitions.getNamespace());

        for (ItemDefinition itemDef : definitions.getItemDefinition()) {
            doc.getItemDefinitions().add(convertItemDefinition(itemDef));
        }

        for (DRGElement element : definitions.getDrgElement()) {
            if (element instanceof InputData inputData) {
                doc.getInputs().add(convertInputData(inputData));
            } else if (element instanceof Decision decision) {
                doc.getDecisions().add(convertDecision(decision));
            }
        }

        return doc;
    }

    private InputDataDoc convertInputData(InputData inputData) {
        String typeRef = inputData.getVariable() != null && inputData.getVariable().getTypeRef() != null
                ? inputData.getVariable().getTypeRef().getLocalPart()
                : null;
        return new InputDataDoc(inputData.getName(), typeRef);
    }

    private DecisionDoc convertDecision(Decision decision) {
        DecisionDoc doc = new DecisionDoc();
        doc.setName(decision.getName());
        doc.setQuestion(decision.getQuestion());

        if (decision.getVariable() != null && decision.getVariable().getTypeRef() != null) {
            doc.setOutputTypeRef(decision.getVariable().getTypeRef().getLocalPart());
        }

        for (InformationRequirement req : decision.getInformationRequirement()) {
            if (req.getRequiredDecision() != null) {
                doc.getInformationRequirements().add(extractHref(req.getRequiredDecision().getHref()));
            } else if (req.getRequiredInput() != null) {
                doc.getInformationRequirements().add(extractHref(req.getRequiredInput().getHref()));
            }
        }

        Expression expression = decision.getExpression();
        if (expression instanceof DecisionTable dt) {
            doc.setExpressionType("DecisionTable");
            doc.setDecisionTable(convertDecisionTable(dt));
        } else if (expression instanceof LiteralExpression le) {
            doc.setExpressionType("LiteralExpression");
            doc.setLiteralExpression(le.getText() != null ? le.getText().trim() : "");
        } else if (expression != null) {
            doc.setExpressionType(expression.getClass().getSimpleName());
        }

        return doc;
    }

    private DecisionTableDoc convertDecisionTable(DecisionTable dt) {
        DecisionTableDoc doc = new DecisionTableDoc();

        if (dt.getHitPolicy() != null) {
            doc.setHitPolicy(dt.getHitPolicy().name());
        }

        for (InputClause input : dt.getInput()) {
            String header = input.getInputExpression() != null
                    ? input.getInputExpression().getText()
                    : "?";
            doc.getInputHeaders().add(header);
        }

        for (OutputClause output : dt.getOutput()) {
            doc.getOutputHeaders().add(output.getName() != null ? output.getName() : "Output");
        }

        for (DecisionRule rule : dt.getRule()) {
            List<String> row = new ArrayList<>();
            rule.getInputEntry().forEach(entry -> row.add(unaryTestText(entry)));
            rule.getOutputEntry().forEach(entry -> row.add(entry.getText() != null ? entry.getText().trim() : "-"));
            doc.getRows().add(row);
        }

        return doc;
    }

    private ItemDefinitionDoc convertItemDefinition(ItemDefinition itemDef) {
        ItemDefinitionDoc doc = new ItemDefinitionDoc();
        doc.setName(itemDef.getName());

        if (itemDef.getTypeRef() != null) {
            doc.setTypeRef(itemDef.getTypeRef().getLocalPart());
        }

        if (itemDef.getAllowedValues() != null && itemDef.getAllowedValues().getText() != null) {
            doc.getAllowedValues().add(itemDef.getAllowedValues().getText());
        }

        for (ItemDefinition component : itemDef.getItemComponent()) {
            doc.getComponents().add(convertItemDefinition(component));
        }

        return doc;
    }

    private String unaryTestText(UnaryTests ut) {
        return ut != null && ut.getText() != null ? ut.getText().trim() : "-";
    }

    private String extractHref(String href) {
        if (href == null) return "";
        return href.startsWith("#") ? href.substring(1) : href;
    }
}

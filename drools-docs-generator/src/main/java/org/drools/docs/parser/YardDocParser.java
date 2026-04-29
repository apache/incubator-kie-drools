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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.drools.docs.model.YardDoc;
import org.drools.docs.model.YardDoc.YardElementDoc;
import org.drools.docs.model.YardDoc.YardInputDoc;
import org.kie.yard.api.model.DecisionTable;
import org.kie.yard.api.model.Element;
import org.kie.yard.api.model.InlineRule;
import org.kie.yard.api.model.Input;
import org.kie.yard.api.model.LiteralExpression;
import org.kie.yard.api.model.Rule;
import org.kie.yard.api.model.WhenThenRule;
import org.kie.yard.api.model.YaRD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses YaRD (YAML Rules DSL) files into the documentation model.
 */
public class YardDocParser {

    private static final Logger LOG = LoggerFactory.getLogger(YardDocParser.class);
    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

    public YardDoc parse(Path yamlFile) throws IOException {
        String content = Files.readString(yamlFile);
        YardDoc doc = parse(content);
        doc.setSourceFile(yamlFile.getFileName().toString());
        return doc;
    }

    public YardDoc parse(String yamlContent) {
        try {
            YaRD yard = YAML_MAPPER.readValue(yamlContent, YaRD.class);
            return convertYard(yard);
        } catch (Exception e) {
            throw new DocParseException("Error parsing YaRD YAML content", e);
        }
    }

    private YardDoc convertYard(YaRD yard) {
        YardDoc doc = new YardDoc();
        doc.setName(yard.getName());
        doc.setSpecVersion(yard.getSpecVersion());
        doc.setExpressionLang(yard.getExpressionLang());

        if (yard.getInputs() != null) {
            for (Input input : yard.getInputs()) {
                doc.getInputs().add(new YardInputDoc(input.getName(), input.getType()));
            }
        }

        if (yard.getElements() != null) {
            for (Element element : yard.getElements()) {
                doc.getElements().add(convertElement(element));
            }
        }

        return doc;
    }

    private YardElementDoc convertElement(Element element) {
        YardElementDoc doc = new YardElementDoc();
        doc.setName(element.getName());
        doc.setType(element.getType());

        if (element.getRequirements() != null) {
            doc.getRequirements().addAll(element.getRequirements());
        }

        if (element.getLogic() instanceof DecisionTable dt) {
            doc.setLogicType("DecisionTable");
            if (dt.getHitPolicy() != null) {
                doc.setHitPolicy(dt.getHitPolicy());
            }
            if (dt.getInputs() != null) {
                doc.getInputHeaders().addAll(dt.getInputs());
            }
            if (dt.getOutputComponents() != null) {
                doc.getOutputHeaders().addAll(dt.getOutputComponents());
            }
            if (dt.getRules() != null) {
                for (Rule rule : dt.getRules()) {
                    doc.getRows().add(convertRule(rule));
                }
            }
        } else if (element.getLogic() instanceof LiteralExpression le) {
            doc.setLogicType("LiteralExpression");
            doc.setLiteralExpression(le.getExpression());
        }

        return doc;
    }

    @SuppressWarnings("unchecked")
    private List<String> convertRule(Rule rule) {
        List<String> cells = new ArrayList<>();
        if (rule instanceof WhenThenRule wt) {
            if (wt.getWhen() != null) {
                for (Object v : (List<?>) wt.getWhen()) {
                    cells.add(String.valueOf(v));
                }
            }
            Object then = wt.getThen();
            if (then instanceof List<?> thenList) {
                for (Object v : thenList) {
                    cells.add(String.valueOf(v));
                }
            } else if (then != null) {
                cells.add(String.valueOf(then));
            }
        } else if (rule instanceof InlineRule ir) {
            if (ir.getDef() != null) {
                for (Object v : ir.getDef()) {
                    cells.add(String.valueOf(v));
                }
            }
        }
        return cells;
    }
}

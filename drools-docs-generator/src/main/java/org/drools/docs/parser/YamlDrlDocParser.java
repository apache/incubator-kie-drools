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
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.drools.docs.model.ConditionDoc;
import org.drools.docs.model.GlobalDoc;
import org.drools.docs.model.PackageDoc;
import org.drools.docs.model.RuleDoc;
import org.drools.drlonyaml.model.Base;
import org.drools.drlonyaml.model.DrlPackage;
import org.drools.drlonyaml.model.Exists;
import org.drools.drlonyaml.model.Global;
import org.drools.drlonyaml.model.Import;
import org.drools.drlonyaml.model.Not;
import org.drools.drlonyaml.model.Pattern;
import org.drools.drlonyaml.model.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses DRL-on-YAML (.drl.yaml) files into the documentation model.
 */
public class YamlDrlDocParser {

    private static final Logger LOG = LoggerFactory.getLogger(YamlDrlDocParser.class);
    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

    public PackageDoc parse(Path yamlFile) throws IOException {
        String content = Files.readString(yamlFile);
        PackageDoc doc = parse(content);
        doc.setSourceFile(yamlFile.getFileName().toString());
        return doc;
    }

    public PackageDoc parse(String yamlContent) {
        try {
            DrlPackage drlPackage = YAML_MAPPER.readValue(yamlContent, DrlPackage.class);
            return convertPackage(drlPackage);
        } catch (Exception e) {
            throw new DocParseException("Error parsing YAML DRL content", e);
        }
    }

    private PackageDoc convertPackage(DrlPackage drlPackage) {
        PackageDoc pkg = new PackageDoc();
        pkg.setName(drlPackage.getName());
        pkg.setSourceFormat(PackageDoc.SourceFormat.YAML_DRL);

        if (drlPackage.getImports() != null) {
            for (Import imp : drlPackage.getImports()) {
                pkg.getImports().add(imp.getTarget());
            }
        }
        if (drlPackage.getGlobals() != null) {
            for (Global global : drlPackage.getGlobals()) {
                pkg.getGlobals().add(new GlobalDoc(global.getType(), global.getId()));
            }
        }
        if (drlPackage.getRules() != null) {
            for (Rule rule : drlPackage.getRules()) {
                pkg.getRules().add(convertRule(rule));
            }
        }
        return pkg;
    }

    private RuleDoc convertRule(Rule rule) {
        RuleDoc doc = new RuleDoc();
        doc.setName(rule.getName());

        List<Base> when = rule.getWhen();
        if (when != null) {
            for (Base base : when) {
                doc.getConditions().add(convertCondition(base));
            }
        }

        if (rule.getThen() != null) {
            doc.setConsequence(rule.getThen().toString());
        }

        return doc;
    }

    private ConditionDoc convertCondition(Base base) {
        ConditionDoc cond = new ConditionDoc();

        if (base instanceof Pattern pattern) {
            cond.setType(ConditionDoc.ConditionType.PATTERN);
            cond.setObjectType(pattern.getGiven());
            cond.setBinding(pattern.getAs());
            if (pattern.getHaving() != null) {
                pattern.getHaving().forEach(h -> cond.getConstraints().add(h.toString()));
            }
        } else if (base instanceof Not not) {
            cond.setType(ConditionDoc.ConditionType.NOT);
            if (not.getNot() != null) {
                for (Base child : not.getNot()) {
                    cond.getChildren().add(convertCondition(child));
                }
            }
        } else if (base instanceof Exists exists) {
            cond.setType(ConditionDoc.ConditionType.EXISTS);
            if (exists.getExists() != null) {
                for (Base child : exists.getExists()) {
                    cond.getChildren().add(convertCondition(child));
                }
            }
        } else {
            cond.setType(ConditionDoc.ConditionType.PATTERN);
            cond.setExpression(base.toString());
        }

        return cond;
    }
}

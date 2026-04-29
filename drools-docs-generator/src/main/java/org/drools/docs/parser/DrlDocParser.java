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
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.drools.docs.model.ConditionDoc;
import org.drools.docs.model.FunctionDoc;
import org.drools.docs.model.GlobalDoc;
import org.drools.docs.model.PackageDoc;
import org.drools.docs.model.RuleDoc;
import org.drools.docs.model.TypeDeclarationDoc;
import org.drools.drl.ast.descr.AccumulateDescr;
import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.ConditionalElementDescr;
import org.drools.drl.ast.descr.EvalDescr;
import org.drools.drl.ast.descr.ExistsDescr;
import org.drools.drl.ast.descr.ExprConstraintDescr;
import org.drools.drl.ast.descr.ForallDescr;
import org.drools.drl.ast.descr.FromDescr;
import org.drools.drl.ast.descr.FunctionDescr;
import org.drools.drl.ast.descr.GlobalDescr;
import org.drools.drl.ast.descr.ImportDescr;
import org.drools.drl.ast.descr.NotDescr;
import org.drools.drl.ast.descr.OrDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.drl.ast.descr.TypeDeclarationDescr;
import org.drools.drl.ast.descr.TypeFieldDescr;
import org.drools.drl.parser.DrlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses DRL files into the documentation model using the Drools DRL parser.
 */
public class DrlDocParser {

    private static final Logger LOG = LoggerFactory.getLogger(DrlDocParser.class);

    public PackageDoc parse(Path drlFile) throws IOException {
        String content = Files.readString(drlFile);
        PackageDoc doc = parse(content);
        doc.setSourceFile(drlFile.getFileName().toString());
        return doc;
    }

    public PackageDoc parse(String drlContent) {
        return parse(new StringReader(drlContent));
    }

    public PackageDoc parse(Reader reader) {
        try {
            DrlParser drlParser = new DrlParser();
            PackageDescr pkgDescr = drlParser.parse(reader);

            if (drlParser.hasErrors()) {
                LOG.warn("DRL parse errors: {}", drlParser.getErrors());
            }

            if (pkgDescr == null) {
                throw new DocParseException("Failed to parse DRL content");
            }

            return convertPackage(pkgDescr);
        } catch (DocParseException e) {
            throw e;
        } catch (Exception e) {
            throw new DocParseException("Error parsing DRL content", e);
        }
    }

    private PackageDoc convertPackage(PackageDescr pkgDescr) {
        PackageDoc pkg = new PackageDoc();
        pkg.setName(pkgDescr.getName());
        pkg.setSourceFormat(PackageDoc.SourceFormat.DRL);
        pkg.setDocumentation(pkgDescr.getDocumentation());

        for (ImportDescr imp : pkgDescr.getImports()) {
            pkg.getImports().add(imp.getTarget());
        }
        for (GlobalDescr global : pkgDescr.getGlobals()) {
            pkg.getGlobals().add(new GlobalDoc(global.getType(), global.getIdentifier()));
        }
        for (TypeDeclarationDescr typeDecl : pkgDescr.getTypeDeclarations()) {
            pkg.getTypeDeclarations().add(convertTypeDeclaration(typeDecl));
        }
        for (FunctionDescr func : pkgDescr.getFunctions()) {
            pkg.getFunctions().add(convertFunction(func));
        }
        for (RuleDescr ruleDescr : pkgDescr.getRules()) {
            pkg.getRules().add(convertRule(ruleDescr));
        }
        return pkg;
    }

    private RuleDoc convertRule(RuleDescr ruleDescr) {
        RuleDoc rule = new RuleDoc();
        rule.setName(ruleDescr.getName());
        rule.setDocumentation(ruleDescr.getDocumentation());

        if (ruleDescr.getParentName() != null && !ruleDescr.getParentName().isEmpty()) {
            rule.setParentName(ruleDescr.getParentName());
        }

        for (AttributeDescr attr : ruleDescr.getAttributes().values()) {
            rule.getAttributes().put(attr.getName(), attr.getValue());
        }

        if (ruleDescr.getAnnotations() != null) {
            for (var ann : ruleDescr.getAnnotations()) {
                rule.getAnnotations().put(ann.getName(), ann.getText());
            }
        }

        AndDescr lhs = ruleDescr.getLhs();
        if (lhs != null) {
            for (BaseDescr descr : lhs.getDescrs()) {
                rule.getConditions().add(convertCondition(descr));
            }
        }

        Object consequence = ruleDescr.getConsequence();
        if (consequence != null) {
            rule.setConsequence(consequence.toString().trim());
        }

        return rule;
    }

    private ConditionDoc convertCondition(BaseDescr descr) {
        ConditionDoc cond = new ConditionDoc();

        if (descr instanceof PatternDescr pattern) {
            cond.setType(ConditionDoc.ConditionType.PATTERN);
            cond.setObjectType(pattern.getObjectType());
            cond.setBinding(pattern.getIdentifier());
            if (pattern.getConstraint() != null) {
                for (BaseDescr constraint : pattern.getConstraint().getDescrs()) {
                    cond.getConstraints().add(constraint.getText());
                }
            }
            if (pattern.getSource() instanceof FromDescr fromDescr) {
                ConditionDoc fromCond = new ConditionDoc();
                fromCond.setType(ConditionDoc.ConditionType.FROM);
                fromCond.setExpression(fromDescr.getText());
                cond.getChildren().add(fromCond);
            }
        } else if (descr instanceof AndDescr andDescr) {
            cond.setType(ConditionDoc.ConditionType.AND);
            for (BaseDescr child : andDescr.getDescrs()) {
                cond.getChildren().add(convertCondition(child));
            }
        } else if (descr instanceof OrDescr orDescr) {
            cond.setType(ConditionDoc.ConditionType.OR);
            for (BaseDescr child : orDescr.getDescrs()) {
                cond.getChildren().add(convertCondition(child));
            }
        } else if (descr instanceof NotDescr notDescr) {
            cond.setType(ConditionDoc.ConditionType.NOT);
            for (Object child : notDescr.getDescrs()) {
                cond.getChildren().add(convertCondition((BaseDescr) child));
            }
        } else if (descr instanceof ExistsDescr existsDescr) {
            cond.setType(ConditionDoc.ConditionType.EXISTS);
            for (Object child : existsDescr.getDescrs()) {
                cond.getChildren().add(convertCondition((BaseDescr) child));
            }
        } else if (descr instanceof ForallDescr forallDescr) {
            cond.setType(ConditionDoc.ConditionType.FORALL);
            for (BaseDescr child : forallDescr.getDescrs()) {
                cond.getChildren().add(convertCondition(child));
            }
        } else if (descr instanceof EvalDescr evalDescr) {
            cond.setType(ConditionDoc.ConditionType.EVAL);
            cond.setExpression(evalDescr.getContent().toString());
        } else if (descr instanceof AccumulateDescr accDescr) {
            cond.setType(ConditionDoc.ConditionType.ACCUMULATE);
            cond.setExpression(accDescr.getText());
        } else if (descr instanceof ConditionalElementDescr ceDescr) {
            cond.setType(ConditionDoc.ConditionType.AND);
            for (BaseDescr child : ceDescr.getDescrs()) {
                cond.getChildren().add(convertCondition(child));
            }
        } else {
            cond.setType(ConditionDoc.ConditionType.PATTERN);
            cond.setExpression(descr.getText());
        }

        return cond;
    }

    private TypeDeclarationDoc convertTypeDeclaration(TypeDeclarationDescr typeDecl) {
        TypeDeclarationDoc doc = new TypeDeclarationDoc();
        doc.setName(typeDecl.getTypeName());
        doc.setTrait(typeDecl.isTrait());
        if (typeDecl.getSuperTypeName() != null && !typeDecl.getSuperTypeName().isEmpty()) {
            doc.setSuperType(typeDecl.getSuperTypeName());
        }
        for (TypeFieldDescr field : typeDecl.getFields().values()) {
            doc.getFields().put(field.getFieldName(),
                    field.getPattern() != null ? field.getPattern().getObjectType() : "Object");
        }
        return doc;
    }

    private FunctionDoc convertFunction(FunctionDescr funcDescr) {
        FunctionDoc doc = new FunctionDoc();
        doc.setName(funcDescr.getName());
        doc.setReturnType(funcDescr.getReturnType());
        doc.getParameterTypes().addAll(funcDescr.getParameterTypes());
        doc.getParameterNames().addAll(funcDescr.getParameterNames());
        doc.setBody(funcDescr.getBody());
        return doc;
    }
}

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
package org.drools.docs.renderer;

import java.io.IOException;
import java.io.Writer;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.drools.docs.model.ConditionDoc;
import org.drools.docs.model.DecisionModelDoc;
import org.drools.docs.model.DecisionModelDoc.DecisionDoc;
import org.drools.docs.model.DecisionModelDoc.DecisionTableDoc;
import org.drools.docs.model.DecisionModelDoc.InputDataDoc;
import org.drools.docs.model.DecisionModelDoc.ItemDefinitionDoc;
import org.drools.docs.model.FunctionDoc;
import org.drools.docs.model.GlobalDoc;
import org.drools.docs.model.PackageDoc;
import org.drools.docs.model.RuleDoc;
import org.drools.docs.model.RuleSetDocumentation;
import org.drools.docs.model.TypeDeclarationDoc;
import org.drools.docs.model.YardDoc;
import org.drools.docs.model.YardDoc.YardElementDoc;
import org.drools.docs.model.YardDoc.YardInputDoc;

/**
 * Renders {@link RuleSetDocumentation} as Markdown.
 */
public class MarkdownRenderer implements DocumentRenderer {

    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void render(RuleSetDocumentation documentation, Writer writer) throws IOException {
        StringBuilder sb = new StringBuilder();

        renderHeader(sb, documentation);

        if (!documentation.getPackages().isEmpty()) {
            sb.append("## DRL Rule Packages\n\n");
            for (PackageDoc pkg : documentation.getPackages()) {
                renderPackage(sb, pkg);
            }
        }

        if (!documentation.getDecisionModels().isEmpty()) {
            sb.append("## DMN Decision Models\n\n");
            for (DecisionModelDoc dmn : documentation.getDecisionModels()) {
                renderDecisionModel(sb, dmn);
            }
        }

        if (!documentation.getYardDefinitions().isEmpty()) {
            sb.append("## YaRD Definitions\n\n");
            for (YardDoc yard : documentation.getYardDefinitions()) {
                renderYard(sb, yard);
            }
        }

        renderFooter(sb, documentation);

        writer.write(sb.toString());
    }

    private void renderHeader(StringBuilder sb, RuleSetDocumentation doc) {
        sb.append("# ").append(doc.getTitle() != null ? doc.getTitle() : "Rule Documentation").append("\n\n");
        if (doc.getDescription() != null && !doc.getDescription().isEmpty()) {
            sb.append(doc.getDescription()).append("\n\n");
        }
        if (doc.getGeneratedAt() != null) {
            sb.append("*Generated: ").append(doc.getGeneratedAt().format(DT_FORMAT)).append("*\n\n");
        }
        sb.append("---\n\n");
    }

    private void renderFooter(StringBuilder sb, RuleSetDocumentation doc) {
        sb.append("---\n\n");
        int totalRules = doc.getPackages().stream().mapToInt(p -> p.getRules().size()).sum();
        int totalDecisions = doc.getDecisionModels().stream().mapToInt(d -> d.getDecisions().size()).sum();
        int totalYardElements = doc.getYardDefinitions().stream().mapToInt(y -> y.getElements().size()).sum();

        sb.append("**Summary:** ");
        sb.append(doc.getPackages().size()).append(" package(s), ");
        sb.append(totalRules).append(" rule(s), ");
        sb.append(doc.getDecisionModels().size()).append(" DMN model(s), ");
        sb.append(totalDecisions).append(" decision(s), ");
        sb.append(doc.getYardDefinitions().size()).append(" YaRD definition(s), ");
        sb.append(totalYardElements).append(" YaRD element(s)\n");
    }

    // ---- DRL Package rendering ----

    private void renderPackage(StringBuilder sb, PackageDoc pkg) {
        sb.append("### Package: `").append(pkg.getName()).append("`\n\n");

        if (pkg.getSourceFile() != null) {
            sb.append("**Source:** ").append(pkg.getSourceFile());
            sb.append(" *(").append(pkg.getSourceFormat()).append(")*\n\n");
        }

        if (pkg.getDocumentation() != null && !pkg.getDocumentation().isEmpty()) {
            sb.append("> ").append(pkg.getDocumentation()).append("\n\n");
        }

        if (!pkg.getImports().isEmpty()) {
            sb.append("#### Imports\n\n");
            for (String imp : pkg.getImports()) {
                sb.append("- `").append(imp).append("`\n");
            }
            sb.append("\n");
        }

        if (!pkg.getGlobals().isEmpty()) {
            sb.append("#### Globals\n\n");
            sb.append("| Type | Name |\n|------|------|\n");
            for (GlobalDoc g : pkg.getGlobals()) {
                sb.append("| `").append(g.getType()).append("` | `").append(g.getIdentifier()).append("` |\n");
            }
            sb.append("\n");
        }

        if (!pkg.getTypeDeclarations().isEmpty()) {
            sb.append("#### Declared Types\n\n");
            for (TypeDeclarationDoc typeDecl : pkg.getTypeDeclarations()) {
                renderTypeDeclaration(sb, typeDecl);
            }
        }

        if (!pkg.getFunctions().isEmpty()) {
            sb.append("#### Functions\n\n");
            for (FunctionDoc func : pkg.getFunctions()) {
                renderFunction(sb, func);
            }
        }

        if (!pkg.getRules().isEmpty()) {
            sb.append("#### Rules\n\n");
            for (RuleDoc rule : pkg.getRules()) {
                renderRule(sb, rule);
            }
        }
    }

    private void renderTypeDeclaration(StringBuilder sb, TypeDeclarationDoc typeDecl) {
        sb.append("##### Type: `").append(typeDecl.getName()).append("`");
        if (typeDecl.isTrait()) {
            sb.append(" *(trait)*");
        }
        sb.append("\n\n");

        if (typeDecl.getSuperType() != null) {
            sb.append("Extends: `").append(typeDecl.getSuperType()).append("`\n\n");
        }

        if (!typeDecl.getFields().isEmpty()) {
            sb.append("| Field | Type |\n|-------|------|\n");
            for (Map.Entry<String, String> field : typeDecl.getFields().entrySet()) {
                sb.append("| `").append(field.getKey()).append("` | `").append(field.getValue()).append("` |\n");
            }
            sb.append("\n");
        }
    }

    private void renderFunction(StringBuilder sb, FunctionDoc func) {
        sb.append("##### Function: `").append(func.getReturnType()).append(" ").append(func.getName()).append("(");
        for (int i = 0; i < func.getParameterNames().size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(func.getParameterTypes().get(i)).append(" ").append(func.getParameterNames().get(i));
        }
        sb.append(")`\n\n");
    }

    private void renderRule(StringBuilder sb, RuleDoc rule) {
        sb.append("##### Rule: ").append(rule.getName()).append("\n\n");

        if (rule.getDocumentation() != null && !rule.getDocumentation().isEmpty()) {
            sb.append("> ").append(rule.getDocumentation()).append("\n\n");
        }

        if (rule.getParentName() != null) {
            sb.append("**Extends:** `").append(rule.getParentName()).append("`\n\n");
        }

        if (!rule.getAttributes().isEmpty()) {
            sb.append("**Attributes:**\n\n");
            for (Map.Entry<String, String> attr : rule.getAttributes().entrySet()) {
                sb.append("- `").append(attr.getKey()).append("`: ").append(attr.getValue()).append("\n");
            }
            sb.append("\n");
        }

        if (!rule.getAnnotations().isEmpty()) {
            sb.append("**Annotations:**\n\n");
            for (Map.Entry<String, String> ann : rule.getAnnotations().entrySet()) {
                sb.append("- `@").append(ann.getKey()).append("`: ").append(ann.getValue()).append("\n");
            }
            sb.append("\n");
        }

        if (!rule.getConditions().isEmpty()) {
            sb.append("**When (Conditions):**\n\n");
            for (ConditionDoc cond : rule.getConditions()) {
                renderCondition(sb, cond, 0);
            }
            sb.append("\n");
        }

        if (rule.getConsequence() != null && !rule.getConsequence().isEmpty()) {
            sb.append("**Then (Actions):**\n\n");
            sb.append("```java\n").append(rule.getConsequence()).append("\n```\n\n");
        }
    }

    private void renderCondition(StringBuilder sb, ConditionDoc cond, int depth) {
        String indent = "  ".repeat(depth);

        switch (cond.getType()) {
            case PATTERN:
                sb.append(indent).append("- ");
                if (cond.getBinding() != null && !cond.getBinding().isEmpty()) {
                    sb.append("`").append(cond.getBinding()).append("` : ");
                }
                if (cond.getObjectType() != null) {
                    sb.append("**").append(cond.getObjectType()).append("**");
                } else if (cond.getExpression() != null) {
                    sb.append("`").append(cond.getExpression()).append("`");
                }
                if (!cond.getConstraints().isEmpty()) {
                    sb.append(" where ");
                    sb.append(String.join(", ", cond.getConstraints().stream()
                            .map(c -> "`" + c + "`").toList()));
                }
                sb.append("\n");
                break;

            case AND:
                sb.append(indent).append("- **ALL** of:\n");
                for (ConditionDoc child : cond.getChildren()) {
                    renderCondition(sb, child, depth + 1);
                }
                break;

            case OR:
                sb.append(indent).append("- **ANY** of:\n");
                for (ConditionDoc child : cond.getChildren()) {
                    renderCondition(sb, child, depth + 1);
                }
                break;

            case NOT:
                sb.append(indent).append("- **NOT**:\n");
                for (ConditionDoc child : cond.getChildren()) {
                    renderCondition(sb, child, depth + 1);
                }
                break;

            case EXISTS:
                sb.append(indent).append("- **EXISTS**:\n");
                for (ConditionDoc child : cond.getChildren()) {
                    renderCondition(sb, child, depth + 1);
                }
                break;

            case FORALL:
                sb.append(indent).append("- **FOR ALL**:\n");
                for (ConditionDoc child : cond.getChildren()) {
                    renderCondition(sb, child, depth + 1);
                }
                break;

            case EVAL:
                sb.append(indent).append("- **eval:** `").append(cond.getExpression()).append("`\n");
                break;

            case ACCUMULATE:
                sb.append(indent).append("- **accumulate:** `").append(cond.getExpression()).append("`\n");
                break;

            case FROM:
                sb.append(indent).append("- **from:** `").append(cond.getExpression()).append("`\n");
                break;
        }

        if (cond.getType() == ConditionDoc.ConditionType.PATTERN && !cond.getChildren().isEmpty()) {
            for (ConditionDoc child : cond.getChildren()) {
                renderCondition(sb, child, depth + 1);
            }
        }
    }

    // ---- DMN rendering ----

    private void renderDecisionModel(StringBuilder sb, DecisionModelDoc dmn) {
        sb.append("### DMN Model: ").append(dmn.getName()).append("\n\n");

        if (dmn.getSourceFile() != null) {
            sb.append("**Source:** ").append(dmn.getSourceFile()).append("\n\n");
        }
        if (dmn.getNamespace() != null) {
            sb.append("**Namespace:** `").append(dmn.getNamespace()).append("`\n\n");
        }

        if (!dmn.getItemDefinitions().isEmpty()) {
            sb.append("#### Type Definitions\n\n");
            for (ItemDefinitionDoc itemDef : dmn.getItemDefinitions()) {
                renderItemDefinition(sb, itemDef, 0);
            }
            sb.append("\n");
        }

        if (!dmn.getInputs().isEmpty()) {
            sb.append("#### Input Data\n\n");
            sb.append("| Input | Type |\n|-------|------|\n");
            for (InputDataDoc input : dmn.getInputs()) {
                sb.append("| ").append(input.getName()).append(" | ");
                sb.append(input.getTypeRef() != null ? "`" + input.getTypeRef() + "`" : "-");
                sb.append(" |\n");
            }
            sb.append("\n");
        }

        if (!dmn.getDecisions().isEmpty()) {
            sb.append("#### Decisions\n\n");
            for (DecisionDoc decision : dmn.getDecisions()) {
                renderDecision(sb, decision);
            }
        }
    }

    private void renderItemDefinition(StringBuilder sb, ItemDefinitionDoc itemDef, int depth) {
        String indent = "  ".repeat(depth);
        sb.append(indent).append("- **").append(itemDef.getName()).append("**");
        if (itemDef.getTypeRef() != null) {
            sb.append(" : `").append(itemDef.getTypeRef()).append("`");
        }
        if (!itemDef.getAllowedValues().isEmpty()) {
            sb.append(" - allowed: ").append(String.join(", ", itemDef.getAllowedValues()));
        }
        sb.append("\n");
        for (ItemDefinitionDoc comp : itemDef.getComponents()) {
            renderItemDefinition(sb, comp, depth + 1);
        }
    }

    private void renderDecision(StringBuilder sb, DecisionDoc decision) {
        sb.append("##### Decision: ").append(decision.getName()).append("\n\n");

        if (decision.getQuestion() != null && !decision.getQuestion().isEmpty()) {
            sb.append("**Question:** ").append(decision.getQuestion()).append("\n\n");
        }
        if (decision.getOutputTypeRef() != null) {
            sb.append("**Output Type:** `").append(decision.getOutputTypeRef()).append("`\n\n");
        }
        if (!decision.getInformationRequirements().isEmpty()) {
            sb.append("**Requires:** ");
            sb.append(String.join(", ", decision.getInformationRequirements().stream()
                    .map(r -> "`" + r + "`").toList()));
            sb.append("\n\n");
        }

        if ("LiteralExpression".equals(decision.getExpressionType())) {
            sb.append("**Expression:** `").append(decision.getLiteralExpression()).append("`\n\n");
        } else if ("DecisionTable".equals(decision.getExpressionType()) && decision.getDecisionTable() != null) {
            renderDecisionTable(sb, decision.getDecisionTable());
        } else if (decision.getExpressionType() != null) {
            sb.append("**Expression Type:** ").append(decision.getExpressionType()).append("\n\n");
        }
    }

    private void renderDecisionTable(StringBuilder sb, DecisionTableDoc dt) {
        if (dt.getHitPolicy() != null) {
            sb.append("**Hit Policy:** ").append(dt.getHitPolicy()).append("\n\n");
        }

        List<String> allHeaders = new java.util.ArrayList<>();
        allHeaders.addAll(dt.getInputHeaders());
        allHeaders.addAll(dt.getOutputHeaders());

        if (allHeaders.isEmpty()) return;

        sb.append("| # | ");
        sb.append(String.join(" | ", allHeaders));
        sb.append(" |\n");

        sb.append("|---|");
        for (int i = 0; i < allHeaders.size(); i++) {
            sb.append("---|");
        }
        sb.append("\n");

        int rowNum = 1;
        for (List<String> row : dt.getRows()) {
            sb.append("| ").append(rowNum++).append(" | ");
            sb.append(String.join(" | ", row));
            sb.append(" |\n");
        }
        sb.append("\n");
    }

    // ---- YaRD rendering ----

    private void renderYard(StringBuilder sb, YardDoc yard) {
        sb.append("### YaRD: ").append(yard.getName()).append("\n\n");

        if (yard.getSourceFile() != null) {
            sb.append("**Source:** ").append(yard.getSourceFile()).append("\n\n");
        }
        if (yard.getSpecVersion() != null) {
            sb.append("**Spec Version:** ").append(yard.getSpecVersion()).append("\n\n");
        }
        if (yard.getExpressionLang() != null) {
            sb.append("**Expression Language:** ").append(yard.getExpressionLang()).append("\n\n");
        }

        if (!yard.getInputs().isEmpty()) {
            sb.append("#### Inputs\n\n");
            sb.append("| Input | Type |\n|-------|------|\n");
            for (YardInputDoc input : yard.getInputs()) {
                sb.append("| ").append(input.getName()).append(" | ");
                sb.append(input.getType() != null ? "`" + input.getType() + "`" : "-");
                sb.append(" |\n");
            }
            sb.append("\n");
        }

        if (!yard.getElements().isEmpty()) {
            sb.append("#### Elements\n\n");
            for (YardElementDoc element : yard.getElements()) {
                renderYardElement(sb, element);
            }
        }
    }

    private void renderYardElement(StringBuilder sb, YardElementDoc element) {
        sb.append("##### Element: ").append(element.getName()).append("\n\n");

        if (element.getType() != null) {
            sb.append("**Type:** `").append(element.getType()).append("`\n\n");
        }
        if (!element.getRequirements().isEmpty()) {
            sb.append("**Requires:** ");
            sb.append(String.join(", ", element.getRequirements().stream()
                    .map(r -> "`" + r + "`").toList()));
            sb.append("\n\n");
        }

        if ("LiteralExpression".equals(element.getLogicType())) {
            sb.append("**Expression:** `").append(element.getLiteralExpression()).append("`\n\n");
        } else if ("DecisionTable".equals(element.getLogicType())) {
            if (element.getHitPolicy() != null) {
                sb.append("**Hit Policy:** ").append(element.getHitPolicy()).append("\n\n");
            }

            List<String> allHeaders = new java.util.ArrayList<>();
            allHeaders.addAll(element.getInputHeaders());
            allHeaders.addAll(element.getOutputHeaders());

            if (!allHeaders.isEmpty()) {
                sb.append("| # | ");
                sb.append(String.join(" | ", allHeaders));
                sb.append(" |\n");
                sb.append("|---|");
                for (int i = 0; i < allHeaders.size(); i++) {
                    sb.append("---|");
                }
                sb.append("\n");
                int rowNum = 1;
                for (List<String> row : element.getRows()) {
                    sb.append("| ").append(rowNum++).append(" | ");
                    sb.append(String.join(" | ", row));
                    sb.append(" |\n");
                }
                sb.append("\n");
            } else if (!element.getRows().isEmpty()) {
                sb.append("**Decision Table Rows:**\n\n");
                int rowNum = 1;
                for (List<String> row : element.getRows()) {
                    sb.append(rowNum++).append(". ").append(String.join(", ", row)).append("\n");
                }
                sb.append("\n");
            }
        }
    }
}

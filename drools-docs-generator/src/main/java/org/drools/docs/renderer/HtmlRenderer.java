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
 * Renders {@link RuleSetDocumentation} as a styled HTML page.
 */
public class HtmlRenderer implements DocumentRenderer {

    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void render(RuleSetDocumentation documentation, Writer writer) throws IOException {
        StringBuilder sb = new StringBuilder();

        renderHtmlHead(sb, documentation);
        renderNavigation(sb, documentation);

        sb.append("<main>\n");
        renderHeader(sb, documentation);

        if (!documentation.getPackages().isEmpty()) {
            sb.append("<section id=\"drl-packages\">\n");
            sb.append("<h2>DRL Rule Packages</h2>\n");
            for (PackageDoc pkg : documentation.getPackages()) {
                renderPackage(sb, pkg);
            }
            sb.append("</section>\n");
        }

        if (!documentation.getDecisionModels().isEmpty()) {
            sb.append("<section id=\"dmn-models\">\n");
            sb.append("<h2>DMN Decision Models</h2>\n");
            for (DecisionModelDoc dmn : documentation.getDecisionModels()) {
                renderDecisionModel(sb, dmn);
            }
            sb.append("</section>\n");
        }

        if (!documentation.getYardDefinitions().isEmpty()) {
            sb.append("<section id=\"yard-definitions\">\n");
            sb.append("<h2>YaRD Definitions</h2>\n");
            for (YardDoc yard : documentation.getYardDefinitions()) {
                renderYard(sb, yard);
            }
            sb.append("</section>\n");
        }

        renderSummaryFooter(sb, documentation);

        sb.append("</main>\n</body>\n</html>\n");
        writer.write(sb.toString());
    }

    private void renderHtmlHead(StringBuilder sb, RuleSetDocumentation doc) {
        String title = esc(doc.getTitle() != null ? doc.getTitle() : "Rule Documentation");
        sb.append("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n");
        sb.append("<meta charset=\"UTF-8\">\n");
        sb.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        sb.append("<title>").append(title).append("</title>\n");
        sb.append("<style>\n").append(CSS).append("\n</style>\n");
        sb.append("</head>\n<body>\n");
    }

    private void renderNavigation(StringBuilder sb, RuleSetDocumentation doc) {
        sb.append("<nav>\n<ul>\n");
        if (!doc.getPackages().isEmpty()) {
            sb.append("<li><a href=\"#drl-packages\">DRL Packages</a>\n<ul>\n");
            for (PackageDoc pkg : doc.getPackages()) {
                String id = "pkg-" + slugify(pkg.getName());
                sb.append("<li><a href=\"#").append(id).append("\">").append(esc(pkg.getName())).append("</a></li>\n");
            }
            sb.append("</ul>\n</li>\n");
        }
        if (!doc.getDecisionModels().isEmpty()) {
            sb.append("<li><a href=\"#dmn-models\">DMN Models</a>\n<ul>\n");
            for (DecisionModelDoc dmn : doc.getDecisionModels()) {
                String id = "dmn-" + slugify(dmn.getName());
                sb.append("<li><a href=\"#").append(id).append("\">").append(esc(dmn.getName())).append("</a></li>\n");
            }
            sb.append("</ul>\n</li>\n");
        }
        if (!doc.getYardDefinitions().isEmpty()) {
            sb.append("<li><a href=\"#yard-definitions\">YaRD</a>\n<ul>\n");
            for (YardDoc yard : doc.getYardDefinitions()) {
                String id = "yard-" + slugify(yard.getName());
                sb.append("<li><a href=\"#").append(id).append("\">").append(esc(yard.getName())).append("</a></li>\n");
            }
            sb.append("</ul>\n</li>\n");
        }
        sb.append("</ul>\n</nav>\n");
    }

    private void renderHeader(StringBuilder sb, RuleSetDocumentation doc) {
        sb.append("<header>\n");
        sb.append("<h1>").append(esc(doc.getTitle() != null ? doc.getTitle() : "Rule Documentation")).append("</h1>\n");
        if (doc.getDescription() != null && !doc.getDescription().isEmpty()) {
            sb.append("<p class=\"description\">").append(esc(doc.getDescription())).append("</p>\n");
        }
        if (doc.getGeneratedAt() != null) {
            sb.append("<p class=\"generated\">Generated: ").append(doc.getGeneratedAt().format(DT_FORMAT)).append("</p>\n");
        }
        sb.append("</header>\n");
    }

    // ---- DRL Package ----

    private void renderPackage(StringBuilder sb, PackageDoc pkg) {
        String id = "pkg-" + slugify(pkg.getName());
        sb.append("<article class=\"package\" id=\"").append(id).append("\">\n");
        sb.append("<h3>Package: <code>").append(esc(pkg.getName())).append("</code></h3>\n");

        if (pkg.getSourceFile() != null) {
            sb.append("<p class=\"source\">Source: ").append(esc(pkg.getSourceFile()));
            sb.append(" <em>(").append(pkg.getSourceFormat()).append(")</em></p>\n");
        }
        if (pkg.getDocumentation() != null && !pkg.getDocumentation().isEmpty()) {
            sb.append("<blockquote>").append(esc(pkg.getDocumentation())).append("</blockquote>\n");
        }

        if (!pkg.getImports().isEmpty()) {
            sb.append("<details open><summary>Imports (").append(pkg.getImports().size()).append(")</summary>\n<ul>\n");
            for (String imp : pkg.getImports()) {
                sb.append("<li><code>").append(esc(imp)).append("</code></li>\n");
            }
            sb.append("</ul>\n</details>\n");
        }

        if (!pkg.getGlobals().isEmpty()) {
            sb.append("<details open><summary>Globals</summary>\n");
            sb.append("<table><thead><tr><th>Type</th><th>Name</th></tr></thead><tbody>\n");
            for (GlobalDoc g : pkg.getGlobals()) {
                sb.append("<tr><td><code>").append(esc(g.getType())).append("</code></td>");
                sb.append("<td><code>").append(esc(g.getIdentifier())).append("</code></td></tr>\n");
            }
            sb.append("</tbody></table>\n</details>\n");
        }

        if (!pkg.getTypeDeclarations().isEmpty()) {
            sb.append("<details open><summary>Declared Types</summary>\n");
            for (TypeDeclarationDoc t : pkg.getTypeDeclarations()) {
                renderTypeDeclarationHtml(sb, t);
            }
            sb.append("</details>\n");
        }

        if (!pkg.getFunctions().isEmpty()) {
            sb.append("<details open><summary>Functions</summary>\n");
            for (FunctionDoc f : pkg.getFunctions()) {
                renderFunctionHtml(sb, f);
            }
            sb.append("</details>\n");
        }

        if (!pkg.getRules().isEmpty()) {
            sb.append("<details open><summary>Rules (").append(pkg.getRules().size()).append(")</summary>\n");
            for (RuleDoc rule : pkg.getRules()) {
                renderRuleHtml(sb, rule);
            }
            sb.append("</details>\n");
        }

        sb.append("</article>\n");
    }

    private void renderTypeDeclarationHtml(StringBuilder sb, TypeDeclarationDoc t) {
        sb.append("<div class=\"type-decl\"><h5>Type: <code>").append(esc(t.getName())).append("</code>");
        if (t.isTrait()) sb.append(" <em>(trait)</em>");
        sb.append("</h5>\n");
        if (t.getSuperType() != null) {
            sb.append("<p>Extends: <code>").append(esc(t.getSuperType())).append("</code></p>\n");
        }
        if (!t.getFields().isEmpty()) {
            sb.append("<table><thead><tr><th>Field</th><th>Type</th></tr></thead><tbody>\n");
            for (Map.Entry<String, String> f : t.getFields().entrySet()) {
                sb.append("<tr><td><code>").append(esc(f.getKey())).append("</code></td>");
                sb.append("<td><code>").append(esc(f.getValue())).append("</code></td></tr>\n");
            }
            sb.append("</tbody></table>\n");
        }
        sb.append("</div>\n");
    }

    private void renderFunctionHtml(StringBuilder sb, FunctionDoc f) {
        sb.append("<div class=\"function\"><h5>Function: <code>");
        sb.append(esc(f.getReturnType())).append(" ").append(esc(f.getName())).append("(");
        for (int i = 0; i < f.getParameterNames().size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(esc(f.getParameterTypes().get(i))).append(" ").append(esc(f.getParameterNames().get(i)));
        }
        sb.append(")</code></h5></div>\n");
    }

    private void renderRuleHtml(StringBuilder sb, RuleDoc rule) {
        sb.append("<div class=\"rule\">\n");
        sb.append("<h5>").append(esc(rule.getName())).append("</h5>\n");

        if (rule.getDocumentation() != null && !rule.getDocumentation().isEmpty()) {
            sb.append("<blockquote>").append(esc(rule.getDocumentation())).append("</blockquote>\n");
        }

        if (!rule.getAttributes().isEmpty()) {
            sb.append("<div class=\"attrs\"><strong>Attributes:</strong> ");
            rule.getAttributes().forEach((k, v) ->
                    sb.append("<span class=\"badge\">").append(esc(k)).append("=").append(esc(v)).append("</span> "));
            sb.append("</div>\n");
        }

        if (!rule.getConditions().isEmpty()) {
            sb.append("<div class=\"when\"><strong>When:</strong>\n<ul>\n");
            for (ConditionDoc c : rule.getConditions()) {
                renderConditionHtml(sb, c);
            }
            sb.append("</ul>\n</div>\n");
        }

        if (rule.getConsequence() != null && !rule.getConsequence().isEmpty()) {
            sb.append("<div class=\"then\"><strong>Then:</strong>\n");
            sb.append("<pre><code>").append(esc(rule.getConsequence())).append("</code></pre>\n</div>\n");
        }

        sb.append("</div>\n");
    }

    private void renderConditionHtml(StringBuilder sb, ConditionDoc cond) {
        switch (cond.getType()) {
            case PATTERN:
                sb.append("<li>");
                if (cond.getBinding() != null && !cond.getBinding().isEmpty()) {
                    sb.append("<code>").append(esc(cond.getBinding())).append("</code> : ");
                }
                if (cond.getObjectType() != null) {
                    sb.append("<strong>").append(esc(cond.getObjectType())).append("</strong>");
                } else if (cond.getExpression() != null) {
                    sb.append("<code>").append(esc(cond.getExpression())).append("</code>");
                }
                if (!cond.getConstraints().isEmpty()) {
                    sb.append(" where ");
                    for (int i = 0; i < cond.getConstraints().size(); i++) {
                        if (i > 0) sb.append(", ");
                        sb.append("<code>").append(esc(cond.getConstraints().get(i))).append("</code>");
                    }
                }
                if (!cond.getChildren().isEmpty()) {
                    sb.append("\n<ul>\n");
                    for (ConditionDoc child : cond.getChildren()) {
                        renderConditionHtml(sb, child);
                    }
                    sb.append("</ul>\n");
                }
                sb.append("</li>\n");
                break;

            case AND:
                sb.append("<li><strong>ALL</strong> of:\n<ul>\n");
                for (ConditionDoc child : cond.getChildren()) { renderConditionHtml(sb, child); }
                sb.append("</ul>\n</li>\n");
                break;

            case OR:
                sb.append("<li><strong>ANY</strong> of:\n<ul>\n");
                for (ConditionDoc child : cond.getChildren()) { renderConditionHtml(sb, child); }
                sb.append("</ul>\n</li>\n");
                break;

            case NOT:
                sb.append("<li><strong>NOT</strong>:\n<ul>\n");
                for (ConditionDoc child : cond.getChildren()) { renderConditionHtml(sb, child); }
                sb.append("</ul>\n</li>\n");
                break;

            case EXISTS:
                sb.append("<li><strong>EXISTS</strong>:\n<ul>\n");
                for (ConditionDoc child : cond.getChildren()) { renderConditionHtml(sb, child); }
                sb.append("</ul>\n</li>\n");
                break;

            case EVAL:
                sb.append("<li><strong>eval:</strong> <code>").append(esc(cond.getExpression())).append("</code></li>\n");
                break;

            case ACCUMULATE:
                sb.append("<li><strong>accumulate:</strong> <code>").append(esc(cond.getExpression())).append("</code></li>\n");
                break;

            default:
                sb.append("<li><code>").append(esc(cond.getExpression())).append("</code></li>\n");
        }
    }

    // ---- DMN rendering ----

    private void renderDecisionModel(StringBuilder sb, DecisionModelDoc dmn) {
        String id = "dmn-" + slugify(dmn.getName());
        sb.append("<article class=\"dmn-model\" id=\"").append(id).append("\">\n");
        sb.append("<h3>DMN Model: ").append(esc(dmn.getName())).append("</h3>\n");

        if (dmn.getSourceFile() != null) {
            sb.append("<p class=\"source\">Source: ").append(esc(dmn.getSourceFile())).append("</p>\n");
        }
        if (dmn.getNamespace() != null) {
            sb.append("<p>Namespace: <code>").append(esc(dmn.getNamespace())).append("</code></p>\n");
        }

        if (!dmn.getItemDefinitions().isEmpty()) {
            sb.append("<details open><summary>Type Definitions</summary>\n<ul>\n");
            for (ItemDefinitionDoc item : dmn.getItemDefinitions()) {
                renderItemDefinitionHtml(sb, item);
            }
            sb.append("</ul>\n</details>\n");
        }

        if (!dmn.getInputs().isEmpty()) {
            sb.append("<details open><summary>Input Data</summary>\n");
            sb.append("<table><thead><tr><th>Input</th><th>Type</th></tr></thead><tbody>\n");
            for (InputDataDoc input : dmn.getInputs()) {
                sb.append("<tr><td>").append(esc(input.getName())).append("</td>");
                sb.append("<td>").append(input.getTypeRef() != null ? "<code>" + esc(input.getTypeRef()) + "</code>" : "-").append("</td></tr>\n");
            }
            sb.append("</tbody></table>\n</details>\n");
        }

        if (!dmn.getDecisions().isEmpty()) {
            sb.append("<details open><summary>Decisions (").append(dmn.getDecisions().size()).append(")</summary>\n");
            for (DecisionDoc d : dmn.getDecisions()) {
                renderDecisionHtml(sb, d);
            }
            sb.append("</details>\n");
        }

        sb.append("</article>\n");
    }

    private void renderItemDefinitionHtml(StringBuilder sb, ItemDefinitionDoc item) {
        sb.append("<li><strong>").append(esc(item.getName())).append("</strong>");
        if (item.getTypeRef() != null) {
            sb.append(" : <code>").append(esc(item.getTypeRef())).append("</code>");
        }
        if (!item.getAllowedValues().isEmpty()) {
            sb.append(" - allowed: ").append(esc(String.join(", ", item.getAllowedValues())));
        }
        if (!item.getComponents().isEmpty()) {
            sb.append("\n<ul>\n");
            for (ItemDefinitionDoc comp : item.getComponents()) {
                renderItemDefinitionHtml(sb, comp);
            }
            sb.append("</ul>\n");
        }
        sb.append("</li>\n");
    }

    private void renderDecisionHtml(StringBuilder sb, DecisionDoc d) {
        sb.append("<div class=\"decision\">\n");
        sb.append("<h5>").append(esc(d.getName())).append("</h5>\n");

        if (d.getQuestion() != null && !d.getQuestion().isEmpty()) {
            sb.append("<p><strong>Question:</strong> ").append(esc(d.getQuestion())).append("</p>\n");
        }
        if (d.getOutputTypeRef() != null) {
            sb.append("<p><strong>Output Type:</strong> <code>").append(esc(d.getOutputTypeRef())).append("</code></p>\n");
        }
        if (!d.getInformationRequirements().isEmpty()) {
            sb.append("<p><strong>Requires:</strong> ");
            d.getInformationRequirements().forEach(r ->
                    sb.append("<code>").append(esc(r)).append("</code> "));
            sb.append("</p>\n");
        }

        if ("LiteralExpression".equals(d.getExpressionType())) {
            sb.append("<p><strong>Expression:</strong> <code>").append(esc(d.getLiteralExpression())).append("</code></p>\n");
        } else if ("DecisionTable".equals(d.getExpressionType()) && d.getDecisionTable() != null) {
            renderDecisionTableHtml(sb, d.getDecisionTable());
        }

        sb.append("</div>\n");
    }

    private void renderDecisionTableHtml(StringBuilder sb, DecisionTableDoc dt) {
        if (dt.getHitPolicy() != null) {
            sb.append("<p><strong>Hit Policy:</strong> ").append(esc(dt.getHitPolicy())).append("</p>\n");
        }

        List<String> allHeaders = new java.util.ArrayList<>(dt.getInputHeaders());
        allHeaders.addAll(dt.getOutputHeaders());

        if (allHeaders.isEmpty()) return;

        sb.append("<table class=\"dtable\"><thead><tr><th>#</th>");
        for (String h : allHeaders) {
            sb.append("<th>").append(esc(h)).append("</th>");
        }
        sb.append("</tr></thead><tbody>\n");

        int rowNum = 1;
        for (List<String> row : dt.getRows()) {
            sb.append("<tr><td>").append(rowNum++).append("</td>");
            for (String cell : row) {
                sb.append("<td>").append(esc(cell)).append("</td>");
            }
            sb.append("</tr>\n");
        }
        sb.append("</tbody></table>\n");
    }

    // ---- YaRD rendering ----

    private void renderYard(StringBuilder sb, YardDoc yard) {
        String id = "yard-" + slugify(yard.getName());
        sb.append("<article class=\"yard\" id=\"").append(id).append("\">\n");
        sb.append("<h3>YaRD: ").append(esc(yard.getName())).append("</h3>\n");

        if (yard.getSourceFile() != null) {
            sb.append("<p class=\"source\">Source: ").append(esc(yard.getSourceFile())).append("</p>\n");
        }

        if (!yard.getInputs().isEmpty()) {
            sb.append("<details open><summary>Inputs</summary>\n");
            sb.append("<table><thead><tr><th>Input</th><th>Type</th></tr></thead><tbody>\n");
            for (YardInputDoc input : yard.getInputs()) {
                sb.append("<tr><td>").append(esc(input.getName())).append("</td>");
                sb.append("<td>").append(input.getType() != null ? "<code>" + esc(input.getType()) + "</code>" : "-").append("</td></tr>\n");
            }
            sb.append("</tbody></table>\n</details>\n");
        }

        if (!yard.getElements().isEmpty()) {
            sb.append("<details open><summary>Elements (").append(yard.getElements().size()).append(")</summary>\n");
            for (YardElementDoc e : yard.getElements()) {
                renderYardElementHtml(sb, e);
            }
            sb.append("</details>\n");
        }

        sb.append("</article>\n");
    }

    private void renderYardElementHtml(StringBuilder sb, YardElementDoc e) {
        sb.append("<div class=\"yard-element\">\n");
        sb.append("<h5>").append(esc(e.getName())).append("</h5>\n");

        if (e.getType() != null) {
            sb.append("<p><strong>Type:</strong> <code>").append(esc(e.getType())).append("</code></p>\n");
        }

        if ("LiteralExpression".equals(e.getLogicType())) {
            sb.append("<p><strong>Expression:</strong> <code>").append(esc(e.getLiteralExpression())).append("</code></p>\n");
        } else if ("DecisionTable".equals(e.getLogicType())) {
            if (e.getHitPolicy() != null) {
                sb.append("<p><strong>Hit Policy:</strong> ").append(esc(e.getHitPolicy())).append("</p>\n");
            }

            List<String> allHeaders = new java.util.ArrayList<>(e.getInputHeaders());
            allHeaders.addAll(e.getOutputHeaders());

            if (!allHeaders.isEmpty() || !e.getRows().isEmpty()) {
                sb.append("<table class=\"dtable\"><thead><tr><th>#</th>");
                for (String h : allHeaders) {
                    sb.append("<th>").append(esc(h)).append("</th>");
                }
                sb.append("</tr></thead><tbody>\n");
                int rowNum = 1;
                for (List<String> row : e.getRows()) {
                    sb.append("<tr><td>").append(rowNum++).append("</td>");
                    for (String cell : row) {
                        sb.append("<td>").append(esc(cell)).append("</td>");
                    }
                    sb.append("</tr>\n");
                }
                sb.append("</tbody></table>\n");
            }
        }

        sb.append("</div>\n");
    }

    private void renderSummaryFooter(StringBuilder sb, RuleSetDocumentation doc) {
        int totalRules = doc.getPackages().stream().mapToInt(p -> p.getRules().size()).sum();
        int totalDecisions = doc.getDecisionModels().stream().mapToInt(d -> d.getDecisions().size()).sum();
        int totalYardElements = doc.getYardDefinitions().stream().mapToInt(y -> y.getElements().size()).sum();

        sb.append("<footer>\n<p><strong>Summary:</strong> ");
        sb.append(doc.getPackages().size()).append(" package(s), ");
        sb.append(totalRules).append(" rule(s), ");
        sb.append(doc.getDecisionModels().size()).append(" DMN model(s), ");
        sb.append(totalDecisions).append(" decision(s), ");
        sb.append(doc.getYardDefinitions().size()).append(" YaRD definition(s), ");
        sb.append(totalYardElements).append(" element(s)");
        sb.append("</p>\n</footer>\n");
    }

    private static String esc(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private static String slugify(String text) {
        if (text == null) return "unnamed";
        return text.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }

    private static final String CSS = """
            :root { --primary: #1a56db; --bg: #f8fafc; --card: #fff; --border: #e2e8f0; --text: #1e293b; --muted: #64748b; }
            * { box-sizing: border-box; margin: 0; padding: 0; }
            body { font-family: system-ui, -apple-system, sans-serif; color: var(--text); background: var(--bg); line-height: 1.6; display: flex; }
            nav { position: fixed; top: 0; left: 0; width: 260px; height: 100vh; overflow-y: auto; background: var(--card); border-right: 1px solid var(--border); padding: 1.5rem 1rem; font-size: 0.875rem; }
            nav ul { list-style: none; }
            nav > ul > li { margin-bottom: 0.75rem; }
            nav ul ul { margin-left: 1rem; margin-top: 0.25rem; }
            nav a { color: var(--primary); text-decoration: none; }
            nav a:hover { text-decoration: underline; }
            main { margin-left: 260px; max-width: 960px; padding: 2rem 2.5rem; width: 100%; }
            header { margin-bottom: 2rem; border-bottom: 2px solid var(--primary); padding-bottom: 1rem; }
            h1 { font-size: 1.75rem; margin-bottom: 0.5rem; }
            h2 { font-size: 1.4rem; margin: 2rem 0 1rem; color: var(--primary); border-bottom: 1px solid var(--border); padding-bottom: 0.4rem; }
            h3 { font-size: 1.15rem; margin-bottom: 0.75rem; }
            h5 { font-size: 1rem; margin-bottom: 0.5rem; }
            .description { color: var(--muted); }
            .generated { font-size: 0.85rem; color: var(--muted); }
            .source { font-size: 0.85rem; color: var(--muted); margin-bottom: 0.5rem; }
            article { background: var(--card); border: 1px solid var(--border); border-radius: 8px; padding: 1.25rem; margin-bottom: 1.5rem; }
            details { margin: 0.75rem 0; }
            summary { cursor: pointer; font-weight: 600; padding: 0.25rem 0; }
            table { width: 100%; border-collapse: collapse; margin: 0.75rem 0; font-size: 0.9rem; }
            th, td { border: 1px solid var(--border); padding: 0.5rem 0.75rem; text-align: left; }
            th { background: #f1f5f9; font-weight: 600; }
            .dtable th { background: var(--primary); color: #fff; }
            code { background: #f1f5f9; padding: 0.15rem 0.35rem; border-radius: 3px; font-size: 0.9em; }
            pre { background: #f1f5f9; padding: 1rem; border-radius: 6px; overflow-x: auto; margin: 0.5rem 0; }
            pre code { background: none; padding: 0; }
            blockquote { border-left: 3px solid var(--primary); padding: 0.5rem 1rem; margin: 0.5rem 0; color: var(--muted); background: #f0f4ff; border-radius: 0 4px 4px 0; }
            .rule, .decision, .type-decl, .function, .yard-element { border: 1px solid var(--border); border-radius: 6px; padding: 1rem; margin: 0.75rem 0; background: var(--bg); }
            .attrs { margin: 0.5rem 0; }
            .badge { display: inline-block; background: #dbeafe; color: var(--primary); padding: 0.1rem 0.5rem; border-radius: 4px; font-size: 0.8rem; margin-right: 0.25rem; }
            .when, .then { margin: 0.5rem 0; }
            .when ul, .when ul ul { margin-left: 1.25rem; }
            footer { margin-top: 2rem; padding-top: 1rem; border-top: 1px solid var(--border); font-size: 0.85rem; color: var(--muted); }
            @media (max-width: 768px) { nav { display: none; } main { margin-left: 0; padding: 1rem; } }
            """;
}

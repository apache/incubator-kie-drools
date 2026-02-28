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
package org.drools.lsp.hover;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.FunctionDescr;
import org.drools.drl.ast.descr.GlobalDescr;
import org.drools.drl.ast.descr.ImportDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.drl.ast.descr.TypeDeclarationDescr;
import org.drools.drl.ast.descr.TypeFieldDescr;
import org.drools.lsp.model.DrlDocumentModel;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;
import org.eclipse.lsp4j.Position;

public class DrlHoverProvider {

    private static final Map<String, String> KEYWORD_DOCS = Map.ofEntries(
            Map.entry("package", "**package** - Declares the namespace for the DRL file. All rules in the file belong to this package."),
            Map.entry("import", "**import** - Imports a Java class or static method so it can be used in rule conditions and actions."),
            Map.entry("global", "**global** - Declares a global variable that is shared across all rules in the session. Must be set on the KieSession before firing."),
            Map.entry("rule", "**rule** - Defines a business rule. Consists of attributes, conditions (when), and actions (then)."),
            Map.entry("query", "**query** - Defines a named query for retrieving facts from working memory. Queries can accept parameters."),
            Map.entry("declare", "**declare** - Declares a new fact type, trait, or entry point. Declared types can be used directly in rules."),
            Map.entry("function", "**function** - Declares a helper function that can be called from rule actions. Supports Java or MVEL dialect."),
            Map.entry("when", "**when** - Begins the condition (LHS) section of a rule. Contains patterns that match against facts in working memory."),
            Map.entry("then", "**then** - Begins the action (RHS) section of a rule. Contains Java/MVEL code executed when conditions are met."),
            Map.entry("end", "**end** - Terminates a `rule`, `query`, or `declare` block."),
            Map.entry("not", "**not** - Negated existential CE. Matches when no fact satisfies the enclosed pattern."),
            Map.entry("exists", "**exists** - Existential CE. Matches when at least one fact satisfies the enclosed pattern (fires only once)."),
            Map.entry("forall", "**forall** - Universal quantifier CE. Matches when all facts matching the first pattern also match the remaining patterns."),
            Map.entry("accumulate", "**accumulate** - Iterates over matching facts and computes aggregate values (sum, count, avg, min, max, etc.)."),
            Map.entry("from", "**from** - Specifies a data source for a pattern, allowing retrieval from collections, methods, or entry points."),
            Map.entry("collect", "**collect** - Collects all matching facts into a result collection (List, Set, etc.)."),
            Map.entry("eval", "**eval** - Evaluates an arbitrary boolean expression. Use sparingly; prefer pattern constraints."),
            Map.entry("salience", "**salience** - Sets rule priority. Higher values fire first. Can be a constant or dynamic expression."),
            Map.entry("no-loop", "**no-loop** - Prevents rule re-activation by changes made in its own consequence."),
            Map.entry("lock-on-active", "**lock-on-active** - Prevents rule re-activation when in an active ruleflow or agenda group."),
            Map.entry("dialect", "**dialect** - Sets the language for evaluating expressions: `\"java\"` or `\"mvel\"`."),
            Map.entry("timer", "**timer** - Schedules delayed or periodic rule execution. Supports `int:` (interval) and `cron:` expressions."),
            Map.entry("calendars", "**calendars** - Specifies Quartz calendar names that control when the rule can fire.")
    );

    public Hover getHover(DrlDocumentModel model, Position position) {
        String word = model.getWordAt(position.getLine(), position.getCharacter());
        if (word.isEmpty()) {
            return null;
        }

        String content = null;
        PackageDescr pkg = model.getPackageDescr();

        if (KEYWORD_DOCS.containsKey(word)) {
            content = KEYWORD_DOCS.get(word);
        } else if (pkg != null) {
            content = tryRuleHover(pkg, word);
            if (content == null) {
                content = tryTypeHover(pkg, word);
            }
            if (content == null) {
                content = tryGlobalHover(pkg, word);
            }
            if (content == null) {
                content = tryFunctionHover(pkg, word);
            }
            if (content == null) {
                content = tryImportHover(pkg, word);
            }
        }

        if (content == null) {
            return null;
        }

        MarkupContent markup = new MarkupContent();
        markup.setKind(MarkupKind.MARKDOWN);
        markup.setValue(content);
        return new Hover(markup);
    }

    private String tryRuleHover(PackageDescr pkg, String word) {
        for (RuleDescr rule : pkg.getRules()) {
            if (word.equals(rule.getName())) {
                return buildRuleHover(rule);
            }
        }
        return null;
    }

    private String buildRuleHover(RuleDescr rule) {
        StringBuilder sb = new StringBuilder();
        sb.append("### Rule: `").append(rule.getName()).append("`\n\n");

        if (rule.getParentName() != null && !rule.getParentName().isEmpty()) {
            sb.append("**Extends**: `").append(rule.getParentName()).append("`\n\n");
        }

        Map<String, AttributeDescr> attrs = rule.getAttributes();
        if (attrs != null && !attrs.isEmpty()) {
            sb.append("**Attributes**:\n");
            for (Map.Entry<String, AttributeDescr> entry : attrs.entrySet()) {
                sb.append("- `").append(entry.getKey()).append("`: `").append(entry.getValue().getValue()).append("`\n");
            }
            sb.append("\n");
        }

        if (rule.getLhs() != null && !rule.getLhs().getDescrs().isEmpty()) {
            sb.append("**Conditions**: ").append(rule.getLhs().getDescrs().size()).append(" pattern(s)\n\n");
        }

        return sb.toString();
    }

    private String tryTypeHover(PackageDescr pkg, String word) {
        for (TypeDeclarationDescr typeDecl : pkg.getTypeDeclarations()) {
            if (word.equals(typeDecl.getTypeName())) {
                return buildTypeHover(typeDecl);
            }
        }
        return null;
    }

    private String buildTypeHover(TypeDeclarationDescr typeDecl) {
        StringBuilder sb = new StringBuilder();
        sb.append("### Type: `").append(typeDecl.getTypeName()).append("`\n\n");

        if (typeDecl.getSuperTypeName() != null && !typeDecl.getSuperTypeName().isEmpty()) {
            sb.append("**Extends**: `").append(typeDecl.getSuperTypeName()).append("`\n\n");
        }

        Map<String, TypeFieldDescr> fields = typeDecl.getFields();
        if (fields != null && !fields.isEmpty()) {
            sb.append("**Fields**:\n");
            sb.append("```\n");
            for (Map.Entry<String, TypeFieldDescr> entry : fields.entrySet()) {
                TypeFieldDescr field = entry.getValue();
                sb.append(field.getFieldName());
                if (field.getPattern() != null) {
                    sb.append(" : ").append(field.getPattern().getObjectType());
                }
                if (field.getInitExpr() != null && !field.getInitExpr().isEmpty()) {
                    sb.append(" = ").append(field.getInitExpr());
                }
                sb.append("\n");
            }
            sb.append("```\n");
        }

        return sb.toString();
    }

    private String tryGlobalHover(PackageDescr pkg, String word) {
        for (GlobalDescr global : pkg.getGlobals()) {
            if (word.equals(global.getIdentifier())) {
                return "### Global: `" + global.getIdentifier() + "`\n\n**Type**: `" + global.getType() + "`\n";
            }
        }
        return null;
    }

    private String tryFunctionHover(PackageDescr pkg, String word) {
        for (FunctionDescr func : pkg.getFunctions()) {
            if (word.equals(func.getName())) {
                return buildFunctionHover(func);
            }
        }
        return null;
    }

    private String buildFunctionHover(FunctionDescr func) {
        StringBuilder sb = new StringBuilder();
        sb.append("### Function: `").append(func.getName()).append("`\n\n");
        sb.append("```java\n");
        sb.append(func.getReturnType()).append(" ").append(func.getName()).append("(");

        List<String> types = func.getParameterTypes();
        List<String> names = func.getParameterNames();
        String params = IntStream.range(0, types.size())
                .mapToObj(i -> types.get(i) + " " + names.get(i))
                .collect(Collectors.joining(", "));
        sb.append(params);

        sb.append(")\n```\n");
        return sb.toString();
    }

    private String tryImportHover(PackageDescr pkg, String word) {
        String wordSimple = word.contains(".") ? word.substring(word.lastIndexOf('.') + 1) : word;
        for (ImportDescr imp : pkg.getImports()) {
            String target = imp.getTarget();
            String simpleName = target.contains(".") ? target.substring(target.lastIndexOf('.') + 1) : target;
            if (word.equals(target) || word.equals(simpleName) || wordSimple.equals(simpleName)) {
                return "### Import\n\n**Fully qualified**: `" + target + "`\n";
            }
        }
        return null;
    }
}

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
package org.drools.lsp.completion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.drools.drl.ast.descr.GlobalDescr;
import org.drools.drl.ast.descr.ImportDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.TypeDeclarationDescr;
import org.drools.drl.parser.antlr4.DRL10Parser;
import org.drools.drl.parser.antlr4.DRL10ParserHelper;
import org.drools.lsp.model.DrlDocumentModel;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;
import org.eclipse.lsp4j.Position;

public class DrlCompletionProvider {

    private static final Map<String, String> KEYWORD_DOCS = Map.ofEntries(
            Map.entry("package", "Declares the package namespace for the DRL file"),
            Map.entry("import", "Imports a Java class or static method for use in rules"),
            Map.entry("global", "Declares a global variable accessible across rules"),
            Map.entry("rule", "Defines a business rule with conditions and actions"),
            Map.entry("query", "Defines a named query for retrieving facts"),
            Map.entry("declare", "Declares a new fact type or trait"),
            Map.entry("function", "Declares a helper function usable in rules"),
            Map.entry("when", "Begins the condition (LHS) section of a rule"),
            Map.entry("then", "Begins the action (RHS) section of a rule"),
            Map.entry("end", "Ends a rule, query, or declare block"),
            Map.entry("not", "Negated existential conditional element"),
            Map.entry("exists", "Existential conditional element"),
            Map.entry("forall", "Universal quantifier conditional element"),
            Map.entry("accumulate", "Accumulate conditional element for aggregation"),
            Map.entry("from", "Specifies a data source for pattern matching"),
            Map.entry("collect", "Collects matching facts into a collection"),
            Map.entry("eval", "Evaluates a boolean expression"),
            Map.entry("over", "Sliding window specification"),
            Map.entry("and", "Logical AND connector"),
            Map.entry("or", "Logical OR connector"),
            Map.entry("salience", "Rule priority (higher fires first)"),
            Map.entry("enabled", "Whether the rule is active (true/false)"),
            Map.entry("no-loop", "Prevents rule re-activation by its own consequences"),
            Map.entry("lock-on-active", "Prevents rule re-activation within same ruleflow group"),
            Map.entry("auto-focus", "Automatically gives focus to the rule's agenda group"),
            Map.entry("agenda-group", "Assigns the rule to a named agenda group"),
            Map.entry("activation-group", "Mutex group - only one rule fires"),
            Map.entry("ruleflow-group", "Associates the rule with a ruleflow group"),
            Map.entry("date-effective", "Rule activates after this date"),
            Map.entry("date-expires", "Rule deactivates after this date"),
            Map.entry("dialect", "Sets the language dialect (java or mvel)"),
            Map.entry("duration", "Delay before rule action fires (ms)"),
            Map.entry("timer", "Timer specification for delayed/periodic execution"),
            Map.entry("calendars", "Quartz calendar names controlling when rule can fire")
    );

    private static final Set<String> TOP_LEVEL_KEYWORDS = Set.of(
            "package", "import", "global", "rule", "query", "declare", "function"
    );

    private static final Set<String> RULE_ATTRIBUTE_KEYWORDS = Set.of(
            "salience", "enabled", "no-loop", "lock-on-active", "auto-focus",
            "agenda-group", "activation-group", "ruleflow-group",
            "date-effective", "date-expires", "dialect", "duration", "timer", "calendars"
    );

    private static final Set<String> LHS_KEYWORDS = Set.of(
            "not", "exists", "forall", "accumulate", "acc", "from", "collect", "eval", "over", "and", "or"
    );

    private static final Set<String> RHS_KEYWORDS = Set.of(
            "insert", "insertLogical", "update", "delete", "retract", "modify", "drools"
    );

    public List<CompletionItem> getCompletions(DrlDocumentModel model, Position position) {
        List<CompletionItem> items = new ArrayList<>();

        CompletionContext ctx = determineContext(model, position);

        switch (ctx) {
            case TOP_LEVEL:
                addKeywordCompletions(items, TOP_LEVEL_KEYWORDS);
                addSnippets(items);
                break;
            case RULE_HEADER:
                addKeywordCompletions(items, RULE_ATTRIBUTE_KEYWORDS);
                items.add(keywordItem("when"));
                break;
            case LHS:
                addKeywordCompletions(items, LHS_KEYWORDS);
                addTypeCompletions(items, model);
                items.add(keywordItem("then"));
                break;
            case RHS:
                addKeywordCompletions(items, RHS_KEYWORDS);
                addGlobalCompletions(items, model);
                items.add(keywordItem("end"));
                break;
            default:
                addKeywordCompletions(items, TOP_LEVEL_KEYWORDS);
                addKeywordCompletions(items, LHS_KEYWORDS);
                addKeywordCompletions(items, RULE_ATTRIBUTE_KEYWORDS);
                addTypeCompletions(items, model);
                addSnippets(items);
                break;
        }

        return items;
    }

    private CompletionContext determineContext(DrlDocumentModel model, Position position) {
        String[] lines = model.getLines();
        int targetLine = position.getLine();

        boolean inRule = false;
        boolean afterWhen = false;
        boolean afterThen = false;

        for (int i = 0; i <= targetLine && i < lines.length; i++) {
            String trimmed = lines[i].trim();
            if (trimmed.startsWith("rule ") || trimmed.startsWith("rule\"")) {
                inRule = true;
                afterWhen = false;
                afterThen = false;
            } else if (trimmed.equals("when") || trimmed.startsWith("when ")) {
                if (inRule) {
                    afterWhen = true;
                }
            } else if (trimmed.equals("then") || trimmed.startsWith("then ")) {
                if (inRule) {
                    afterThen = true;
                }
            } else if (trimmed.equals("end") || trimmed.startsWith("end ") || trimmed.startsWith("end;")) {
                inRule = false;
                afterWhen = false;
                afterThen = false;
            }
        }

        if (afterThen) {
            return CompletionContext.RHS;
        } else if (afterWhen) {
            return CompletionContext.LHS;
        } else if (inRule) {
            return CompletionContext.RULE_HEADER;
        } else {
            return CompletionContext.TOP_LEVEL;
        }
    }

    private void addKeywordCompletions(List<CompletionItem> items, Set<String> keywords) {
        for (String keyword : keywords) {
            items.add(keywordItem(keyword));
        }
    }

    private CompletionItem keywordItem(String keyword) {
        CompletionItem item = new CompletionItem(keyword);
        item.setKind(CompletionItemKind.Keyword);
        item.setDetail(KEYWORD_DOCS.getOrDefault(keyword, "DRL keyword"));
        item.setInsertText(keyword);
        return item;
    }

    private void addTypeCompletions(List<CompletionItem> items, DrlDocumentModel model) {
        PackageDescr pkg = model.getPackageDescr();
        if (pkg == null) {
            return;
        }

        for (ImportDescr imp : pkg.getImports()) {
            String target = imp.getTarget();
            String simpleName = target.contains(".") ? target.substring(target.lastIndexOf('.') + 1) : target;
            if (!"*".equals(simpleName)) {
                CompletionItem item = new CompletionItem(simpleName);
                item.setKind(CompletionItemKind.Class);
                item.setDetail(target);
                item.setInsertText(simpleName);
                items.add(item);
            }
        }

        for (TypeDeclarationDescr typeDecl : pkg.getTypeDeclarations()) {
            String typeName = typeDecl.getTypeName();
            if (typeName != null) {
                CompletionItem item = new CompletionItem(typeName);
                item.setKind(CompletionItemKind.Struct);
                item.setDetail("Declared type");
                item.setInsertText(typeName);
                items.add(item);
            }
        }
    }

    private void addGlobalCompletions(List<CompletionItem> items, DrlDocumentModel model) {
        PackageDescr pkg = model.getPackageDescr();
        if (pkg == null) {
            return;
        }
        for (GlobalDescr global : pkg.getGlobals()) {
            CompletionItem item = new CompletionItem(global.getIdentifier());
            item.setKind(CompletionItemKind.Variable);
            item.setDetail(global.getType() + " (global)");
            item.setInsertText(global.getIdentifier());
            items.add(item);
        }
    }

    private void addSnippets(List<CompletionItem> items) {
        items.add(snippet("rule (template)", "rule",
                "rule \"${1:RuleName}\"\n    when\n        ${2:// conditions}\n    then\n        ${3:// actions}\nend",
                "Complete rule template"));

        items.add(snippet("query (template)", "query",
                "query \"${1:QueryName}\" (${2:Type} ${3:var})\n    ${4:// conditions}\nend",
                "Complete query template"));

        items.add(snippet("declare (template)", "declare",
                "declare ${1:TypeName}\n    ${2:fieldName} : ${3:Type}\nend",
                "Complete type declaration template"));

        items.add(snippet("function (template)", "function",
                "function ${1:void} ${2:functionName}(${3:}) {\n    ${4:// body}\n}",
                "Complete function template"));
    }

    private CompletionItem snippet(String label, String filterText, String insertText, String detail) {
        CompletionItem item = new CompletionItem(label);
        item.setKind(CompletionItemKind.Snippet);
        item.setInsertTextFormat(InsertTextFormat.Snippet);
        item.setFilterText(filterText);
        item.setInsertText(insertText);
        item.setDetail(detail);
        return item;
    }

    private enum CompletionContext {
        TOP_LEVEL,
        RULE_HEADER,
        LHS,
        RHS,
        UNKNOWN
    }
}

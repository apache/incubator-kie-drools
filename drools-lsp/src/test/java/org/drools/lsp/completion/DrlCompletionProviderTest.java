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

import java.util.List;

import org.drools.lsp.model.DrlDocumentModel;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.Position;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DrlCompletionProviderTest {

    private final DrlCompletionProvider provider = new DrlCompletionProvider();

    @Test
    void topLevelShouldOfferPackageAndRuleKeywords() {
        String drl = "package org.test;\n";
        DrlDocumentModel model = new DrlDocumentModel("file:///test.drl", drl);

        List<CompletionItem> items = provider.getCompletions(model, new Position(1, 0));
        List<String> labels = items.stream().map(CompletionItem::getLabel).toList();

        assertThat(labels).contains("rule", "import", "global", "declare", "function", "query");
    }

    @Test
    void insideRuleHeaderShouldOfferAttributes() {
        String drl = "package org.test;\nrule \"R1\"\n    \n    when\n    then\nend";
        DrlDocumentModel model = new DrlDocumentModel("file:///test.drl", drl);

        List<CompletionItem> items = provider.getCompletions(model, new Position(2, 4));
        List<String> labels = items.stream().map(CompletionItem::getLabel).toList();

        assertThat(labels).contains("salience", "no-loop", "when");
    }

    @Test
    void insideLhsShouldOfferPatternKeywords() {
        String drl = "package org.test;\nrule \"R1\"\n    when\n        \n    then\nend";
        DrlDocumentModel model = new DrlDocumentModel("file:///test.drl", drl);

        List<CompletionItem> items = provider.getCompletions(model, new Position(3, 8));
        List<String> labels = items.stream().map(CompletionItem::getLabel).toList();

        assertThat(labels).contains("not", "exists", "forall", "accumulate", "then");
    }

    @Test
    void insideRhsShouldOfferActionKeywords() {
        String drl = "package org.test;\nrule \"R1\"\n    when\n    then\n        \nend";
        DrlDocumentModel model = new DrlDocumentModel("file:///test.drl", drl);

        List<CompletionItem> items = provider.getCompletions(model, new Position(4, 8));
        List<String> labels = items.stream().map(CompletionItem::getLabel).toList();

        assertThat(labels).contains("insert", "update", "delete", "modify", "end");
    }

    @Test
    void shouldOfferImportedTypesInLhs() {
        String drl = "package org.test;\nimport org.example.Person;\nrule \"R1\"\n    when\n        \n    then\nend";
        DrlDocumentModel model = new DrlDocumentModel("file:///test.drl", drl);

        List<CompletionItem> items = provider.getCompletions(model, new Position(4, 8));
        List<String> labels = items.stream().map(CompletionItem::getLabel).toList();

        assertThat(labels).contains("Person");
    }

    @Test
    void shouldOfferSnippetTemplatesAtTopLevel() {
        String drl = "package org.test;\n";
        DrlDocumentModel model = new DrlDocumentModel("file:///test.drl", drl);

        List<CompletionItem> items = provider.getCompletions(model, new Position(1, 0));
        List<CompletionItem> snippets = items.stream()
                .filter(i -> i.getKind() == CompletionItemKind.Snippet)
                .toList();

        assertThat(snippets).isNotEmpty();
        assertThat(snippets.stream().map(CompletionItem::getLabel))
                .anyMatch(l -> l.contains("rule"));
    }

    @Test
    void shouldOfferGlobalsInRhs() {
        String drl = "package org.test;\nglobal java.util.List results;\nrule \"R1\"\n    when\n    then\n        \nend";
        DrlDocumentModel model = new DrlDocumentModel("file:///test.drl", drl);

        List<CompletionItem> items = provider.getCompletions(model, new Position(5, 8));
        List<String> labels = items.stream().map(CompletionItem::getLabel).toList();

        assertThat(labels).contains("results");
    }
}

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
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.drools.docs.model.PackageDoc;
import org.drools.docs.model.RuleSetDocumentation;
import org.drools.docs.model.YardDoc;
import org.drools.docs.parser.DrlDocParser;
import org.drools.docs.parser.YardDocParser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownRendererTest {

    @Test
    void shouldRenderDrlPackageAsMarkdown() throws IOException {
        RuleSetDocumentation doc = createDocWithDrl();

        String md = new MarkdownRenderer().render(doc);

        assertThat(md).contains("# Loan Rules");
        assertThat(md).contains("## DRL Rule Packages");
        assertThat(md).contains("### Package: `org.example.loans`");
        assertThat(md).contains("#### Rules");
        assertThat(md).contains("##### Rule: Eligible Age Check");
        assertThat(md).contains("**When (Conditions):**");
        assertThat(md).contains("**Then (Actions):**");
        assertThat(md).contains("#### Imports");
        assertThat(md).contains("#### Globals");
        assertThat(md).contains("#### Declared Types");
    }

    @Test
    void shouldRenderYardAsMarkdown() throws IOException {
        RuleSetDocumentation doc = createDocWithYard();

        String md = new MarkdownRenderer().render(doc);

        assertThat(md).contains("## YaRD Definitions");
        assertThat(md).contains("### YaRD: Insurance Base Price");
        assertThat(md).contains("#### Inputs");
        assertThat(md).contains("#### Elements");
        assertThat(md).contains("##### Element: Base price");
        assertThat(md).contains("**Expression:** `Math.max");
    }

    @Test
    void shouldRenderSummaryFooter() throws IOException {
        RuleSetDocumentation doc = createDocWithDrl();

        String md = new MarkdownRenderer().render(doc);

        assertThat(md).contains("**Summary:**");
        assertThat(md).contains("1 package(s)");
        assertThat(md).contains("3 rule(s)");
    }

    private RuleSetDocumentation createDocWithDrl() throws IOException {
        RuleSetDocumentation doc = new RuleSetDocumentation();
        doc.setTitle("Loan Rules");
        doc.setGeneratedAt(LocalDateTime.of(2026, 3, 1, 12, 0));

        InputStream is = getClass().getClassLoader().getResourceAsStream("sample-rules.drl");
        String drlContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        PackageDoc pkg = new DrlDocParser().parse(drlContent);
        doc.getPackages().add(pkg);

        return doc;
    }

    private RuleSetDocumentation createDocWithYard() throws IOException {
        RuleSetDocumentation doc = new RuleSetDocumentation();
        doc.setTitle("Insurance Rules");
        doc.setGeneratedAt(LocalDateTime.of(2026, 3, 1, 12, 0));

        InputStream is = getClass().getClassLoader().getResourceAsStream("sample-yard.yml");
        String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        YardDoc yard = new YardDocParser().parse(content);
        doc.getYardDefinitions().add(yard);

        return doc;
    }
}

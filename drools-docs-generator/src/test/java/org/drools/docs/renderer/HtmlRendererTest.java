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
import org.drools.docs.parser.DrlDocParser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HtmlRendererTest {

    @Test
    void shouldRenderValidHtml() throws IOException {
        RuleSetDocumentation doc = createDocWithDrl();

        String html = new HtmlRenderer().render(doc);

        assertThat(html).contains("<!DOCTYPE html>");
        assertThat(html).contains("<html lang=\"en\">");
        assertThat(html).contains("</html>");
        assertThat(html).contains("<title>Loan Rules</title>");
    }

    @Test
    void shouldRenderNavigation() throws IOException {
        RuleSetDocumentation doc = createDocWithDrl();

        String html = new HtmlRenderer().render(doc);

        assertThat(html).contains("<nav>");
        assertThat(html).contains("DRL Packages");
        assertThat(html).contains("href=\"#pkg-org-example-loans\"");
    }

    @Test
    void shouldRenderRulesSection() throws IOException {
        RuleSetDocumentation doc = createDocWithDrl();

        String html = new HtmlRenderer().render(doc);

        assertThat(html).contains("Eligible Age Check");
        assertThat(html).contains("Credit Score Approval");
        assertThat(html).contains("High Risk Rejection");
        assertThat(html).contains("<strong>When:</strong>");
        assertThat(html).contains("<strong>Then:</strong>");
    }

    @Test
    void shouldRenderStyledOutput() throws IOException {
        RuleSetDocumentation doc = createDocWithDrl();

        String html = new HtmlRenderer().render(doc);

        assertThat(html).contains("<style>");
        assertThat(html).contains("--primary:");
        assertThat(html).contains(".rule");
    }

    @Test
    void shouldEscapeHtmlEntities() throws IOException {
        RuleSetDocumentation doc = new RuleSetDocumentation();
        doc.setTitle("Rules with <special> & \"chars\"");
        doc.setGeneratedAt(LocalDateTime.of(2026, 3, 1, 12, 0));

        String html = new HtmlRenderer().render(doc);

        assertThat(html).contains("&lt;special&gt;");
        assertThat(html).contains("&amp;");
        assertThat(html).contains("&quot;chars&quot;");
        assertThat(html).doesNotContain("<special>");
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
}

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

import org.drools.lsp.model.DrlDocumentModel;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.MarkupKind;
import org.eclipse.lsp4j.Position;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DrlHoverProviderTest {

    private final DrlHoverProvider provider = new DrlHoverProvider();

    @Test
    void shouldReturnKeywordDocumentation() {
        String drl = "package org.test;\nrule \"R1\" when then end";
        DrlDocumentModel model = new DrlDocumentModel("file:///test.drl", drl);

        Hover hover = provider.getHover(model, new Position(0, 0));
        assertThat(hover).isNotNull();
        assertThat(hover.getContents().getRight().getKind()).isEqualTo(MarkupKind.MARKDOWN);
        assertThat(hover.getContents().getRight().getValue()).contains("package");
    }

    @Test
    void shouldReturnRuleHoverInfo() {
        String drl = "package org.test;\nrule \"CheckAge\"\n    salience 10\n    when\n    then\nend";
        DrlDocumentModel model = new DrlDocumentModel("file:///test.drl", drl);

        Hover hover = provider.getHover(model, new Position(1, 8));
        assertThat(hover).isNotNull();
        assertThat(hover.getContents().getRight().getValue()).contains("CheckAge");
    }

    @Test
    void shouldReturnGlobalHoverInfo() {
        String drl = "package org.test;\nglobal java.util.List results;";
        DrlDocumentModel model = new DrlDocumentModel("file:///test.drl", drl);

        Hover hover = provider.getHover(model, new Position(1, 27));
        assertThat(hover).isNotNull();
        assertThat(hover.getContents().getRight().getValue()).contains("results");
        assertThat(hover.getContents().getRight().getValue()).contains("java.util.List");
    }

    @Test
    void shouldReturnTypeDeclarationHoverInfo() {
        String drl = "package org.test;\ndeclare Applicant\n    name : String\n    age  : int\nend";
        DrlDocumentModel model = new DrlDocumentModel("file:///test.drl", drl);

        Hover hover = provider.getHover(model, new Position(1, 10));
        assertThat(hover).isNotNull();
        assertThat(hover.getContents().getRight().getValue()).contains("Applicant");
    }

    @Test
    void shouldReturnFunctionHoverInfo() {
        String drl = "package org.test;\nfunction boolean isAdult(int age) {\n    return age >= 18;\n}";
        DrlDocumentModel model = new DrlDocumentModel("file:///test.drl", drl);

        Hover hover = provider.getHover(model, new Position(1, 20));
        assertThat(hover).isNotNull();
        assertThat(hover.getContents().getRight().getValue()).contains("isAdult");
        assertThat(hover.getContents().getRight().getValue()).contains("boolean");
    }

    @Test
    void shouldReturnImportHoverInfo() {
        String drl = "package org.test;\nimport org.example.model.Person;";
        DrlDocumentModel model = new DrlDocumentModel("file:///test.drl", drl);

        // "Person" starts at column 25 on line 1
        Hover hover = provider.getHover(model, new Position(1, 26));
        assertThat(hover).isNotNull();
        assertThat(hover.getContents().getRight().getValue()).contains("org.example.model.Person");
    }

    @Test
    void shouldReturnNullForEmptyPosition() {
        String drl = "package org.test;\n\nrule \"R1\" when then end";
        DrlDocumentModel model = new DrlDocumentModel("file:///test.drl", drl);

        Hover hover = provider.getHover(model, new Position(1, 0));
        assertThat(hover).isNull();
    }
}

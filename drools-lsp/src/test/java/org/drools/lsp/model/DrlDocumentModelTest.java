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
package org.drools.lsp.model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DrlDocumentModelTest {

    @Test
    void validDrlShouldParseWithoutErrors() {
        String drl = loadResource("sample.drl");
        DrlDocumentModel model = new DrlDocumentModel("file:///test/sample.drl", drl);

        assertThat(model.hasErrors()).isFalse();
        assertThat(model.getPackageDescr()).isNotNull();
        assertThat(model.getPackageDescr().getName()).isEqualTo("org.example.rules");
        assertThat(model.getParseTree()).isNotNull();
    }

    @Test
    void invalidDrlShouldProduceErrors() {
        String drl = loadResource("invalid.drl");
        DrlDocumentModel model = new DrlDocumentModel("file:///test/invalid.drl", drl);

        assertThat(model.hasErrors()).isTrue();
        assertThat(model.getErrors()).isNotEmpty();
    }

    @Test
    void updateShouldReparse() {
        String drl = "package org.test;\nrule \"R1\" when then end";
        DrlDocumentModel model = new DrlDocumentModel("file:///test.drl", drl);

        assertThat(model.hasErrors()).isFalse();
        assertThat(model.getPackageDescr().getRules()).hasSize(1);

        model.update("package org.test;\nrule \"R1\" when then end\nrule \"R2\" when then end");
        assertThat(model.getPackageDescr().getRules()).hasSize(2);
    }

    @Test
    void getWordAtShouldReturnCorrectWord() {
        String drl = "package org.example;\nglobal java.util.List results;";
        DrlDocumentModel model = new DrlDocumentModel("file:///test.drl", drl);

        assertThat(model.getWordAt(0, 0)).isEqualTo("package");
        assertThat(model.getWordAt(0, 8)).isEqualTo("org.example");
        assertThat(model.getWordAt(1, 7)).isEqualTo("java.util.List");
        assertThat(model.getWordAt(1, 22)).isEqualTo("results");
    }

    @Test
    void packageDescrShouldContainImports() {
        String drl = loadResource("sample.drl");
        DrlDocumentModel model = new DrlDocumentModel("file:///test/sample.drl", drl);

        assertThat(model.getPackageDescr().getImports()).hasSize(2);
        assertThat(model.getPackageDescr().getImports().get(0).getTarget()).isEqualTo("org.example.model.Person");
    }

    @Test
    void packageDescrShouldContainGlobals() {
        String drl = loadResource("sample.drl");
        DrlDocumentModel model = new DrlDocumentModel("file:///test/sample.drl", drl);

        assertThat(model.getPackageDescr().getGlobals()).hasSize(1);
        assertThat(model.getPackageDescr().getGlobals().get(0).getIdentifier()).isEqualTo("results");
        assertThat(model.getPackageDescr().getGlobals().get(0).getType()).isEqualTo("java.util.List");
    }

    private String loadResource(String name) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(name)) {
            if (is == null) {
                throw new RuntimeException("Resource not found: " + name);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

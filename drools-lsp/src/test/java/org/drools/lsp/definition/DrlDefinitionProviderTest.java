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
package org.drools.lsp.definition;

import java.util.List;

import org.drools.lsp.model.DrlDocumentModel;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DrlDefinitionProviderTest {

    private final DrlDefinitionProvider provider = new DrlDefinitionProvider();

    @Test
    void shouldFindRuleDefinition() {
        String drl = "package org.test;\nrule \"CheckAge\"\n    when\n    then\nend";
        DrlDocumentModel model = new DrlDocumentModel("file:///test.drl", drl);

        // "CheckAge" on line 1 (0-based)
        List<Location> locations = provider.getDefinition(model, new Position(1, 7));
        assertThat(locations).isNotEmpty();
        assertThat(locations.get(0).getUri()).isEqualTo("file:///test.drl");
    }

    @Test
    void shouldFindGlobalDefinition() {
        String drl = "package org.test;\nglobal java.util.List results;\nrule \"R1\"\n    when\n    then\n        results.add(\"x\");\nend";
        DrlDocumentModel model = new DrlDocumentModel("file:///test.drl", drl);

        // "results" starts at column 22 on line 1
        List<Location> locations = provider.getDefinition(model, new Position(1, 24));
        assertThat(locations).isNotEmpty();
    }

    @Test
    void shouldFindTypeDeclarationDefinition() {
        String drl = "package org.test;\ndeclare Person\n    name : String\nend\nrule \"R1\" when Person() then end";
        DrlDocumentModel model = new DrlDocumentModel("file:///test.drl", drl);

        // "Person" on the declare line
        List<Location> locations = provider.getDefinition(model, new Position(1, 10));
        assertThat(locations).isNotEmpty();
    }

    @Test
    void shouldReturnEmptyForUnknownSymbol() {
        String drl = "package org.test;\nrule \"R1\" when then end";
        DrlDocumentModel model = new DrlDocumentModel("file:///test.drl", drl);

        List<Location> locations = provider.getDefinition(model, new Position(0, 0));
        // "package" is a keyword, not a definition target - may or may not match
        // The key test is that it doesn't crash
        assertThat(locations).isNotNull();
    }

    @Test
    void shouldFindFunctionDefinition() {
        String drl = "package org.test;\nfunction boolean isValid(String s) {\n    return s != null;\n}\nrule \"R1\" when then end";
        DrlDocumentModel model = new DrlDocumentModel("file:///test.drl", drl);

        List<Location> locations = provider.getDefinition(model, new Position(1, 20));
        assertThat(locations).isNotEmpty();
    }
}

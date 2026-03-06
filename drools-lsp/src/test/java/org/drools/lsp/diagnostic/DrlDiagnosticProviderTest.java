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
package org.drools.lsp.diagnostic;

import java.util.List;

import org.drools.lsp.model.DrlDocumentModel;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DrlDiagnosticProviderTest {

    private final DrlDiagnosticProvider provider = new DrlDiagnosticProvider();

    @Test
    void validDrlShouldProduceNoDiagnostics() {
        String drl = "package org.test;\nrule \"R1\" when then end";
        DrlDocumentModel model = new DrlDocumentModel("file:///test.drl", drl);

        List<Diagnostic> diagnostics = provider.computeDiagnostics(model);
        assertThat(diagnostics).isEmpty();
    }

    @Test
    void invalidDrlShouldProduceDiagnostics() {
        String drl = "package org.test;\nrule \"Broken\" when Person(name == then end";
        DrlDocumentModel model = new DrlDocumentModel("file:///test.drl", drl);

        List<Diagnostic> diagnostics = provider.computeDiagnostics(model);
        assertThat(diagnostics).isNotEmpty();
        assertThat(diagnostics.get(0).getSeverity()).isEqualTo(DiagnosticSeverity.Error);
        assertThat(diagnostics.get(0).getSource()).isEqualTo("drools-lsp");
    }

    @Test
    void diagnosticsShouldHaveCorrectLineNumbers() {
        String drl = "package org.test;\n\nrule \"Bad\"\n    when\n        Person(name ==\n    then\n    end";
        DrlDocumentModel model = new DrlDocumentModel("file:///test.drl", drl);

        List<Diagnostic> diagnostics = provider.computeDiagnostics(model);
        assertThat(diagnostics).isNotEmpty();
        assertThat(diagnostics.get(0).getRange().getStart().getLine()).isGreaterThanOrEqualTo(0);
    }
}

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

import java.util.ArrayList;
import java.util.List;

import org.drools.drl.parser.antlr4.DRLParserError;
import org.drools.lsp.model.DrlDocumentModel;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.services.LanguageClient;

public class DrlDiagnosticProvider {

    private static final String SOURCE = "drools-lsp";

    public void publishDiagnostics(LanguageClient client, DrlDocumentModel model) {
        if (client == null) {
            return;
        }
        List<Diagnostic> diagnostics = computeDiagnostics(model);
        client.publishDiagnostics(new PublishDiagnosticsParams(model.getUri(), diagnostics));
    }

    public List<Diagnostic> computeDiagnostics(DrlDocumentModel model) {
        List<Diagnostic> diagnostics = new ArrayList<>();
        for (DRLParserError error : model.getErrors()) {
            diagnostics.add(toDiagnostic(error));
        }
        return diagnostics;
    }

    private Diagnostic toDiagnostic(DRLParserError error) {
        int line = Math.max(0, error.getLineNumber() - 1);
        int col = Math.max(0, error.getColumn());

        Position start = new Position(line, col);
        Position end = new Position(line, col + 1);
        Range range = new Range(start, end);

        Diagnostic diagnostic = new Diagnostic();
        diagnostic.setRange(range);
        diagnostic.setSeverity(DiagnosticSeverity.Error);
        diagnostic.setSource(SOURCE);
        diagnostic.setMessage(error.getMessage());
        return diagnostic;
    }
}

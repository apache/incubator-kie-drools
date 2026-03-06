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
package org.drools.lsp.server;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.lsp.completion.DrlCompletionProvider;
import org.drools.lsp.definition.DrlDefinitionProvider;
import org.drools.lsp.diagnostic.DrlDiagnosticProvider;
import org.drools.lsp.hover.DrlHoverProvider;
import org.drools.lsp.model.DrlDocumentModel;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.DefinitionParams;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DrlTextDocumentService implements TextDocumentService {

    private static final Logger LOG = LoggerFactory.getLogger(DrlTextDocumentService.class);

    private final DrlLanguageServer server;
    private final Map<String, DrlDocumentModel> documents = new ConcurrentHashMap<>();
    private final DrlCompletionProvider completionProvider = new DrlCompletionProvider();
    private final DrlDiagnosticProvider diagnosticProvider = new DrlDiagnosticProvider();
    private final DrlDefinitionProvider definitionProvider = new DrlDefinitionProvider();
    private final DrlHoverProvider hoverProvider = new DrlHoverProvider();
    private LanguageClient client;

    public DrlTextDocumentService(DrlLanguageServer server) {
        this.server = server;
    }

    public void connect(LanguageClient client) {
        this.client = client;
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        String text = params.getTextDocument().getText();
        LOG.debug("Document opened: {}", uri);

        DrlDocumentModel model = new DrlDocumentModel(uri, text);
        documents.put(uri, model);
        diagnosticProvider.publishDiagnostics(client, model);
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        String text = params.getContentChanges().get(0).getText();
        LOG.debug("Document changed: {}", uri);

        DrlDocumentModel model = documents.get(uri);
        if (model != null) {
            model.update(text);
        } else {
            model = new DrlDocumentModel(uri, text);
            documents.put(uri, model);
        }
        diagnosticProvider.publishDiagnostics(client, model);
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        LOG.debug("Document closed: {}", uri);
        documents.remove(uri);
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        LOG.debug("Document saved: {}", params.getTextDocument().getUri());
    }

    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams params) {
        String uri = params.getTextDocument().getUri();
        DrlDocumentModel model = documents.get(uri);
        if (model == null) {
            return CompletableFuture.completedFuture(Either.forLeft(List.of()));
        }
        List<CompletionItem> items = completionProvider.getCompletions(model, params.getPosition());
        return CompletableFuture.completedFuture(Either.forLeft(items));
    }

    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(DefinitionParams params) {
        String uri = params.getTextDocument().getUri();
        DrlDocumentModel model = documents.get(uri);
        if (model == null) {
            return CompletableFuture.completedFuture(Either.forLeft(List.of()));
        }
        List<Location> locations = definitionProvider.getDefinition(model, params.getPosition());
        return CompletableFuture.completedFuture(Either.forLeft(locations));
    }

    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        String uri = params.getTextDocument().getUri();
        DrlDocumentModel model = documents.get(uri);
        if (model == null) {
            return CompletableFuture.completedFuture(null);
        }
        Hover hover = hoverProvider.getHover(model, params.getPosition());
        return CompletableFuture.completedFuture(hover);
    }

    public DrlDocumentModel getDocument(String uri) {
        return documents.get(uri);
    }
}

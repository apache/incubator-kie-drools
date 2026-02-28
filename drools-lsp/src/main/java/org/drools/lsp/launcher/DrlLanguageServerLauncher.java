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
package org.drools.lsp.launcher;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Future;

import org.drools.lsp.server.DrlLanguageServer;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DrlLanguageServerLauncher {

    private static final Logger LOG = LoggerFactory.getLogger(DrlLanguageServerLauncher.class);

    public static void main(String[] args) {
        launch(System.in, System.out);
    }

    public static DrlLanguageServer launch(InputStream in, OutputStream out) {
        DrlLanguageServer server = new DrlLanguageServer();
        Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(server, in, out);
        LanguageClient client = launcher.getRemoteProxy();
        server.connect(client);
        Future<?> listening = launcher.startListening();
        LOG.info("DRL Language Server started");
        return server;
    }
}

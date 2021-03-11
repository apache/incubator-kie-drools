/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.codegen.openapi.client;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import static java.util.Objects.requireNonNull;

/**
 * Extension to serve OpenAPI spec files under /openapi/specs/__files.
 * Every file in this directory will be served by the web server.
 */
public class OpenApiSpecMockServer implements BeforeEachCallback,
        AfterEachCallback {

    private static final String SPEC_FILES_PATH = "/specs";
    private static final int PORT = 8989;
    private WireMockServer specServer;

    @Override
    public void afterEach(ExtensionContext context) {
        if (specServer != null) {
            specServer.stop();
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        specServer = new WireMockServer(
                WireMockConfiguration.options()
                        .usingFilesUnderDirectory(requireNonNull(getClass().getResource(SPEC_FILES_PATH)).getPath())
                        .port(PORT));
        specServer.start();
    }
}

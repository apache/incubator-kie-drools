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
package org.kie.kogito.codegen.openapi.client.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.openapi.client.OpenApiSpecDescriptor;
import org.kie.kogito.codegen.openapi.client.OpenApiUtils;

/**
 * Resolves the schema "http|https:" in a given OpenApi operation definition.
 * For example: "http://myservices.com/swagger.json"
 */
public class HTTPResolver extends AbstractPathResolver {

    private static final String ACCEPT_HEADERS = "application/json,application/yaml,application/yml,application/text,text/*,*/*";

    protected HTTPResolver(final KogitoBuildContext context) {
        super(context);
    }

    @Override
    public String resolve(OpenApiSpecDescriptor resource) {
        OpenApiUtils.requireValidSpecURI(resource);
        try {
            final URL openAPISpecFileURL = resource.getURI().toURL();
            final HttpURLConnection conn = (HttpURLConnection) openAPISpecFileURL.openConnection();
            conn.setRequestProperty("Accept", ACCEPT_HEADERS);
            final int respCode = conn.getResponseCode();
            if (respCode == HttpURLConnection.HTTP_OK) {
                try (InputStream is = conn.getInputStream()) {
                    return this.saveFileToTempLocation(resource, is);
                }
            } else {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                    final StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    throw new IllegalArgumentException(String.format(
                            "Failed to fetch remote OpenAPI spec file: %s. Status code is %d and response: \n %s",
                            resource.getURI().toString(), respCode, response));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to resolve remote file: " + resource.getURI().toString(), e);
        }
    }
}

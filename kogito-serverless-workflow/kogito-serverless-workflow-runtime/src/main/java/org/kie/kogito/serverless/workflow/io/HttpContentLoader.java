/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Optional;

class HttpContentLoader extends FallbackContentLoader {

    private URI uri;

    public HttpContentLoader(URI uri, Optional<URIContentLoader> fallback) {
        super(fallback);
        this.uri = uri;
    }

    @Override
    protected byte[] internalToBytes() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        // some http servers required specific accept header (*/* is specified for those we do not care about accept) 
        conn.setRequestProperty("Accept", "application/json,application/yaml,application/yml,application/text,text/*,*/*");
        int code = conn.getResponseCode();
        if (code == HttpURLConnection.HTTP_OK) {
            try (InputStream is = conn.getInputStream()) {
                return is.readAllBytes();
            }
        } else {
            try (InputStream is = conn.getErrorStream()) {
                throw new IllegalArgumentException(String.format(
                        "Failed to fetch remote file: %s. Status code is %d and response: %n %s", uri.toString(), code, new String(is.readAllBytes())));
            }
        }
    }
}

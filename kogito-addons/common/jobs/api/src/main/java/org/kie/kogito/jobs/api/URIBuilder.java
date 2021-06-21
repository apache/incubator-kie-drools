/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.jobs.api;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

public class URIBuilder {

    private URIBuilder() {

    }

    /**
     * Transform the given url String into an {@link URI} and inserts the default port in case it was not explicit set
     * on the url String.
     * 
     * @param urlStr
     * @return
     */
    public static URI toURI(String urlStr) {
        try {
            final URL url = new URL(urlStr);
            final Integer port = Optional.of(url.getPort()).filter(p -> !p.equals(-1)).orElse(url.getDefaultPort());
            final URI uri = url.toURI();
            return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), port, uri.getPath(), uri.getQuery(),
                    uri.getFragment());

        } catch (MalformedURLException | URISyntaxException e) {
            throw new IllegalArgumentException("Not valid URI: " + urlStr, e);
        }
    }
}

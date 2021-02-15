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

import java.net.URI;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class URIBuilderTest {

    @Test
    void testToURIHttpNoPort() {
        URI jobServiceURL = URIBuilder.toURI("http://localhost/resource1/resource2?x=1&y=2");
        assertHttp(jobServiceURL, "http", 80);
    }

    @Test
    void testToURIHttpsNoPort() {
        URI jobServiceURL = URIBuilder.toURI("https://localhost/resource1/resource2?x=1&y=2");
        assertHttp(jobServiceURL, "https", 443);
    }

    @Test
    void testToURIHttpWithPort() {
        URI jobServiceURL = URIBuilder.toURI("http://localhost:8080/resource1/resource2?x=1&y=2");
        assertHttp(jobServiceURL, "http", 8080);
    }

    @Test
    void testToURIHttpsWithPort() {
        URI jobServiceURL = URIBuilder.toURI("https://localhost:4443/resource1/resource2?x=1&y=2");
        assertHttp(jobServiceURL, "https", 4443);
    }

    private void assertHttp(URI jobServiceURL, String http, int i) {
        assertThat(jobServiceURL.getScheme()).isEqualTo(http);
        assertThat(jobServiceURL.getHost()).isEqualTo("localhost");
        assertThat(jobServiceURL.getPort()).isEqualTo(i);
        assertThat(jobServiceURL.getPath()).isEqualTo("/resource1/resource2");
        assertThat(jobServiceURL.getQuery()).isEqualTo("x=1&y=2");
    }
}
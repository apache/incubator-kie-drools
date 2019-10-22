/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.jobs.service.model;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class HTTPRequestCallback {

    public enum HTTPMethod {
        GET,
        POST,
        PUT,
        DELETE,
        PATCH,
        OPTIONS,
        HEAD,
        TRACE,
        CONNECT
    }

    private String url;
    private HTTPMethod method;
    private Map<String, String> headers;
    private String body;

    public HTTPRequestCallback(String url, HTTPMethod method, Map<String, String> headers, String body) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public HTTPMethod getMethod() {
        return method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HTTPRequestCallback)) {
            return false;
        }
        HTTPRequestCallback that = (HTTPRequestCallback) o;
        return Objects.equals(getUrl(), that.getUrl()) &&
                getMethod() == that.getMethod() &&
                Objects.equals(getHeaders(), that.getHeaders()) &&
                Objects.equals(getBody(), that.getBody());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl(), getMethod(), getHeaders(), getBody());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", HTTPRequestCallback.class.getSimpleName() + "[", "]")
                .add("url='" + url + "'")
                .add("method=" + method)
                .add("headers=" + headers)
                .add("body='" + body + "'")
                .toString();
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {

        private String url;
        private HTTPMethod method;
        private Map<String, String> headers;
        private String body;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder method(String method) {
            this.method = HTTPMethod.valueOf(method);
            return this;
        }

        public Builder method(HTTPMethod method) {
            this.method = method;
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public HTTPRequestCallback build() {
            return new HTTPRequestCallback(url, method, headers, body);
        }
    }
}

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
package org.kie.kogito.job.recipient.common.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class HTTPRequest {

    public enum HTTPMethod {
        GET,
        POST,
        PUT,
        DELETE,
        PATCH,
        OPTIONS,
        HEAD,
        TRACE
    }

    private final String url;
    private final HTTPMethod method;
    private final Map<String, String> headers;
    private final Map<String, String> queryParams;
    private final Object body;

    private HTTPRequest(String url, HTTPMethod method, Map<String, String> headers, Object body, Map<String, String> queryParams) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.body = body;
        this.queryParams = queryParams;
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

    public Object getBody() {
        return body;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HTTPRequest)) {
            return false;
        }
        HTTPRequest that = (HTTPRequest) o;
        return Objects.equals(getUrl(), that.getUrl()) &&
                getMethod() == that.getMethod() &&
                Objects.equals(getHeaders(), that.getHeaders()) &&
                Objects.equals(getQueryParams(), that.getQueryParams()) &&
                Objects.equals(getBody(), that.getBody());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl(), getMethod(), getHeaders(), getQueryParams(), getBody());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", HTTPRequest.class.getSimpleName() + "[", "]")
                .add("url='" + url + "'")
                .add("method=" + method)
                .add("headers=" + headers)
                .add("queryParams=" + queryParams)
                .add("body='" + body + "'")
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String url;
        private HTTPMethod method;
        private Map<String, String> headers = new HashMap<>();
        private Object body;
        private Map<String, String> queryParams = new HashMap<>();

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

        public Builder addHeader(String name, String value) {
            headers.put(name, value);
            return this;
        }

        public Builder body(Object body) {
            this.body = body;
            return this;
        }

        public Builder queryParams(Map<String, String> queryParams) {
            this.queryParams = queryParams;
            return this;
        }

        public Builder addQueryParam(String name, String value) {
            queryParams.put(name, value);
            return this;
        }

        public HTTPRequest build() {
            return new HTTPRequest(url, method, headers, body, queryParams);
        }
    }
}

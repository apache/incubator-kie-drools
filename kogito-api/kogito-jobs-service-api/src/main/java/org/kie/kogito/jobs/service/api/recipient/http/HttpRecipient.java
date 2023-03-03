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

package org.kie.kogito.jobs.service.api.recipient.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.kie.kogito.jobs.service.api.Recipient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import static org.kie.kogito.jobs.service.api.Recipient.PAYLOAD_PROPERTY;
import static org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient.HEADERS_PROPERTY;
import static org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient.METHOD_PROPERTY;
import static org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient.QUERY_PARAMS_PROPERTY;
import static org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient.URL_PROPERTY;

@Schema(description = "Recipient definition that executes a http request on a given url and sends the configured \"payload\" as the body.",
        allOf = { Recipient.class },
        requiredProperties = { URL_PROPERTY, METHOD_PROPERTY })
@JsonPropertyOrder({ URL_PROPERTY, METHOD_PROPERTY, HEADERS_PROPERTY, QUERY_PARAMS_PROPERTY, PAYLOAD_PROPERTY })
public class HttpRecipient<T extends HttpRecipientPayloadData<?>> extends Recipient<T> {

    static final String URL_PROPERTY = "url";
    static final String METHOD_PROPERTY = "method";
    static final String HEADERS_PROPERTY = "headers";
    static final String QUERY_PARAMS_PROPERTY = "queryParams";

    @Schema(description = "Url of the recipient that will receive the request.")
    private String url;
    @Schema(description = "Http method to use for the request.",
            defaultValue = "POST",
            enumeration = { "POST", "GET", "HEAD", "PUT", "DELETE", "PATCH", "OPTIONS" })
    private String method = "POST";
    @Schema(description = "Http headers to send with the request.")
    private Map<String, String> headers;
    @Schema(description = "Http query parameters to send with the request.")
    private Map<String, String> queryParams;
    @JsonProperty("payload")
    @Schema(ref = "#/components/schemas/HttpRecipientPayloadData")
    private T payload;

    public HttpRecipient() {
        // Marshalling constructor.
        this.headers = new HashMap<>();
        this.queryParams = new HashMap<>();
    }

    @Override
    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers != null ? headers : new HashMap<>();
    }

    public HttpRecipient<T> addHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public String getHeader(String name) {
        return headers != null ? headers.get(name) : null;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams != null ? queryParams : new HashMap<>();
    }

    public HttpRecipient<T> addQueryParam(String name, String value) {
        queryParams.put(name, value);
        return this;
    }

    public String getQueryParam(String name) {
        return queryParams != null ? queryParams.get(name) : null;
    }

    @Override
    public String toString() {
        return "HttpRecipient{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", headers=" + headers +
                ", queryParams=" + queryParams +
                ", payload=" + payload +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        HttpRecipient<?> recipient = (HttpRecipient<?>) o;
        return Objects.equals(url, recipient.url) &&
                Objects.equals(method, recipient.method) &&
                Objects.equals(headers, recipient.headers) &&
                Objects.equals(queryParams, recipient.queryParams)
                && Objects.equals(payload, recipient.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, method, headers, queryParams, payload);
    }

    public static BuilderSelector builder() {
        return new BuilderSelector();
    }

    public static class BuilderSelector {

        private BuilderSelector() {

        }

        public Builder<HttpRecipientStringPayloadData> forStringPayload() {
            return new HttpRecipient.Builder<>(new HttpRecipient<>());

        }

        public Builder<HttpRecipientBinaryPayloadData> forBinaryPayload() {
            return new HttpRecipient.Builder<>(new HttpRecipient<>());
        }

        public Builder<HttpRecipientJsonPayloadData> forJsonPayload() {
            return new HttpRecipient.Builder<>(new HttpRecipient<>());
        }
    }

    public static class Builder<P extends HttpRecipientPayloadData<?>> {
        private final HttpRecipient<P> recipient;

        private Builder(HttpRecipient<P> recipient) {
            this.recipient = recipient;
        }

        public Builder<P> payload(P payload) {
            recipient.setPayload(payload);
            return this;
        }

        public Builder<P> url(String url) {
            recipient.setUrl(url);
            return this;
        }

        public Builder<P> method(String method) {
            recipient.setMethod(method);
            return this;
        }

        public Builder<P> header(String name, String value) {
            recipient.addHeader(name, value);
            return this;
        }

        public Builder<P> queryParam(String name, String value) {
            recipient.addQueryParam(name, value);
            return this;
        }

        public HttpRecipient<P> build() {
            return recipient;
        }
    }
}

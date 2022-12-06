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

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.kie.kogito.jobs.service.api.Recipient;

@Schema(description = "Recipient definition that executes a http request on a given url and sends the configured \"payload\" as the body.", allOf = { Recipient.class })
public class HttpRecipient extends Recipient<byte[]> {

    @Schema(description = "Url of the recipient that will receive the request.", required = true)
    private String url;
    @Schema(description = "Http method to use for the request.", required = true, defaultValue = "POST",
            enumeration = { "POST", "GET", "HEAD", "PUT", "DELETE", "PATCH", "OPTIONS" })
    private String method = "POST";
    @Schema(description = "Http headers to send with the request.")
    private Map<String, String> headers;
    @Schema(description = "Http query parameters to send with the request.")
    private Map<String, String> queryParams;

    public HttpRecipient() {
        // marshalling constructor.
        this.headers = new HashMap<>();
        this.queryParams = new HashMap<>();
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

    public HttpRecipient addHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams != null ? queryParams : new HashMap<>();
    }

    public HttpRecipient addQueryParam(String name, String value) {
        queryParams.put(name, value);
        return this;
    }

    @Override
    public String toString() {
        return "HttpRecipient{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", headers=" + headers +
                ", queryParams=" + queryParams +
                "} " + super.toString();
    }

    public static Builder builder() {
        return new Builder(new HttpRecipient());
    }

    public static class Builder {

        private final HttpRecipient recipient;

        private Builder(HttpRecipient recipient) {
            this.recipient = recipient;
        }

        public Builder payload(byte[] payload) {
            recipient.setPayload(payload);
            return this;
        }

        public Builder url(String url) {
            recipient.setUrl(url);
            return this;
        }

        public Builder method(String method) {
            recipient.setMethod(method);
            return this;
        }

        public Builder header(String name, String value) {
            recipient.addHeader(name, value);
            return this;
        }

        public Builder queryParam(String name, String value) {
            recipient.addQueryParam(name, value);
            return this;
        }

        public HttpRecipient build() {
            return recipient;
        }
    }
}

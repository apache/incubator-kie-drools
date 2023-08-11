/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.addons.quarkus.knative.serving.customfunctions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Operation {

    static final String CLOUD_EVENT_PARAMETER_NAME = "asCloudEvent";

    static final String PATH_PARAMETER_NAME = "path";

    private final String service;

    private final String path;

    private final boolean isCloudEvent;

    private Operation(Builder builder) {
        this.service = Objects.requireNonNull(builder.service);
        this.path = builder.path != null ? builder.path : "/";
        this.isCloudEvent = builder.isCloudEvent;
    }

    public String getService() {
        return service;
    }

    public String getPath() {
        return path;
    }

    public boolean isCloudEvent() {
        return isCloudEvent;
    }

    public static Operation parse(String value) {
        String[] parts = value.split("\\?", 2);

        String[] query = parts.length > 1 ? parts[1].split("&") : new String[0];
        Map<String, String> params = new HashMap<>();
        for (String param : query) {
            String[] pair = param.split("=", 2);
            params.put(pair[0], pair.length > 1 ? pair[1] : "");
        }

        return builder()
                .withService(parts[0])
                .withPath(params.get("path"))
                .withIsCloudEvent(Boolean.parseBoolean(params.get(CLOUD_EVENT_PARAMETER_NAME)))
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Operation operation = (Operation) o;
        return isCloudEvent == operation.isCloudEvent
                && Objects.equals(service, operation.service)
                && Objects.equals(path, operation.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(service, path, isCloudEvent);
    }

    public static class Builder {

        private String service;

        private String path;

        private boolean isCloudEvent;

        private Builder() {
        }

        public Builder withService(String service) {
            this.service = service;
            return this;
        }

        public Builder withPath(String path) {
            this.path = path;
            return this;
        }

        public Builder withIsCloudEvent(boolean isCloudEvent) {
            this.isCloudEvent = isCloudEvent;
            return this;
        }

        public Operation build() {
            return new Operation(this);
        }
    }
}

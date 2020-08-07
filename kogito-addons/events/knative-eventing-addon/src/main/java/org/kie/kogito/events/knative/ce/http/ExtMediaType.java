/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.events.knative.ce.http;

import javax.ws.rs.core.MediaType;

/**
 * Extends {@link MediaType} to CloudEvents support
 */
// this shouldn't be provided by the CE SDK? Send a PR.
public final class ExtMediaType {

    public static final String CLOUDEVENTS_JSON = "application/cloudevents+json";
    public static final MediaType CLOUDEVENTS_JSON_TYPE = new MediaType("application", "cloudevents+json");

    private ExtMediaType() {

    }
}

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

package org.kie.kogito.jobs.service.api;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import static org.kie.kogito.jobs.service.api.JobLookupId.CORRELATION_ID_PROPERTY;
import static org.kie.kogito.jobs.service.api.JobLookupId.ID_PROPERTY;

@Schema(description = "Logical identifier for executing job queries.")
@JsonPropertyOrder({ ID_PROPERTY, CORRELATION_ID_PROPERTY })
public class JobLookupId {

    static final String ID_PROPERTY = "id";
    static final String CORRELATION_ID_PROPERTY = "correlationId";

    private String id;
    private String correlationId;

    private JobLookupId() {
        // Marshalling constructor.
    }

    private JobLookupId(String id, String correlationId) {
        this.id = id;
        this.correlationId = correlationId;
    }

    public String getId() {
        return id;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public static JobLookupId fromId(String id) {
        return new JobLookupId(id, null);
    }

    public static JobLookupId fromCorrelationId(String correlationId) {
        return new JobLookupId(null, correlationId);
    }
}

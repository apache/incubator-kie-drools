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

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.media.SchemaProperty;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@Schema(discriminatorProperty = "type",
        properties = { @SchemaProperty(name = "type", type = SchemaType.STRING) },
        requiredProperties = { "type" },
        description = "Generic definition for a Recipient, users must provide instances of subclasses of this schema to create a job.")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public abstract class Recipient<T> {

    @Schema(description = "This value represents the information that is sent to the recipient entity at the job execution, and might vary depending on the particular recipient subclass.")
    protected T payload;

    public Recipient() {
        // marshalling constructor.
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Recipient{" +
                "payload=" + payload +
                '}';
    }
}

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

import static org.kie.kogito.jobs.service.api.Schedule.TYPE_PROPERTY;

@Schema(discriminatorProperty = TYPE_PROPERTY,
        properties = { @SchemaProperty(name = TYPE_PROPERTY, type = SchemaType.STRING) },
        requiredProperties = { TYPE_PROPERTY },
        description = "Generic definition for a Schedule, users must provide instances of subclasses of this schema.")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = TYPE_PROPERTY)
public abstract class Schedule {

    static final String TYPE_PROPERTY = "type";

    protected Schedule() {
        // Marshalling constructor.
    }
}

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

package org.kie.kogito.jobs.service.api.recipient.sink;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.DiscriminatorMapping;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.media.SchemaProperty;
import org.kie.kogito.jobs.service.api.PayloadData;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import static org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipientPayloadData.BINARY;
import static org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipientPayloadData.JSON;
import static org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipientPayloadData.TYPE;

@Schema(discriminatorProperty = TYPE,
        properties = { @SchemaProperty(name = TYPE, type = SchemaType.STRING) },
        requiredProperties = { TYPE },
        discriminatorMapping = {
                @DiscriminatorMapping(value = BINARY, schema = SinkRecipientBinaryPayloadData.class),
                @DiscriminatorMapping(value = JSON, schema = SinkRecipientJsonPayloadData.class)
        })
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = TYPE)
@JsonSubTypes({
        @JsonSubTypes.Type(name = BINARY, value = SinkRecipientBinaryPayloadData.class),
        @JsonSubTypes.Type(name = JSON, value = SinkRecipientJsonPayloadData.class)

})
public abstract class SinkRecipientPayloadData<T> extends PayloadData<T> {

    static final String TYPE = "type";
    static final String BINARY = "binary";
    static final String JSON = "json";

    protected SinkRecipientPayloadData() {
        // Marshalling constructor.
    }
}

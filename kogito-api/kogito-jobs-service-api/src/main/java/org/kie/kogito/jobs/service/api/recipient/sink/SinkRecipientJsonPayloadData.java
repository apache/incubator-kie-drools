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

package org.kie.kogito.jobs.service.api.recipient.sink;

import java.util.Objects;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

@Schema(allOf = { SinkRecipientPayloadData.class })
public class SinkRecipientJsonPayloadData extends SinkRecipientPayloadData<JsonNode> {

    @JsonProperty("data")
    private JsonNode dataJson;

    public SinkRecipientJsonPayloadData() {
        // Marshalling constructor.
    }

    private SinkRecipientJsonPayloadData(JsonNode data) {
        this.dataJson = data;
    }

    @Override
    public JsonNode getData() {
        return dataJson;
    }

    public static SinkRecipientJsonPayloadData from(JsonNode data) {
        return new SinkRecipientJsonPayloadData(data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SinkRecipientJsonPayloadData that = (SinkRecipientJsonPayloadData) o;
        return Objects.equals(dataJson, that.dataJson);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataJson);
    }
}

/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.trusty.service.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.kie.kogito.trusty.storage.api.model.TypedValue;

public class TypedValueResponse {

    @JsonProperty("name")
    private String name;

    @JsonProperty("typeRef")
    private String typeRef;

    @JsonProperty("value")
    private JsonNode value;

    private TypedValueResponse() {
    }

    public TypedValueResponse(String name, String typeRef, JsonNode value) {
        this.name = name;
        this.typeRef = typeRef;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getTypeRef() {
        return typeRef;
    }

    public JsonNode getValue() {
        return value;
    }

    public static TypedValueResponse from(TypedValue value) {
        return value == null ? null : new TypedValueResponse(value.getName(), value.getTypeRef(), value.getValue());
    }
}

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

package org.kie.kogito.tracing.decision.event.variable;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StructureVariable extends TypedVariable {

    @JsonProperty("value")
    private Map<String, TypedVariable> value;

    private StructureVariable() {
    }

    public StructureVariable(String type) {
        super(Kind.STRUCTURE, type);
    }

    public StructureVariable(String type, Map<String, TypedVariable> value) {
        super(Kind.STRUCTURE, type);
        this.value = value;
    }

    public Map<String, TypedVariable> getValue() {
        return value;
    }
}

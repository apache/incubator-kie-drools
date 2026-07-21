/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.tracing.typedvalue;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StructureValue extends TypedValue {

    @JsonProperty("value")
    protected Map<String, TypedValue> value;

    protected StructureValue() {
    }

    public StructureValue(String type) {
        super(Kind.STRUCTURE, type);
    }

    public StructureValue(String type, Map<String, TypedValue> value) {
        super(Kind.STRUCTURE, type);
        this.value = value;
    }

    public Map<String, TypedValue> getValue() {
        return value;
    }
}

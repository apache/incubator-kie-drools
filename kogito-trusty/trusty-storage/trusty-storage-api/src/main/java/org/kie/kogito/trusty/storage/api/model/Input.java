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
package org.kie.kogito.trusty.storage.api.model;

import org.kie.kogito.tracing.typedvalue.TypedValue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
/**
 * Base abstract class for <b>Input</b>
 * 
 * @param <T>
 * @param <E>
 */
public abstract class Input {

    public static final String ID_FIELD = "id";
    public static final String NAME_FIELD = "name";
    public static final String VALUE_FIELD = "value";

    @JsonProperty(ID_FIELD)
    private String id;

    @JsonProperty(NAME_FIELD)
    private String name;

    @JsonProperty(VALUE_FIELD)
    private TypedValue value;

    protected Input() {
    }

    protected Input(String id, String name, TypedValue value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TypedValue getValue() {
        return value;
    }

    public void setValue(TypedValue value) {
        this.value = value;
    }
}

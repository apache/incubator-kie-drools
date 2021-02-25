/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.grafana.model.panel.table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DateStyle.class, name = "date"),
        @JsonSubTypes.Type(value = NumberStyle.class, name = "number")
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseStyle {

    @JsonProperty("alias")
    public String alias;

    @JsonProperty("align")
    public String align;

    @JsonProperty("pattern")
    public String pattern;

    @JsonProperty("type")
    public String type;

    public BaseStyle() {
    }

    public BaseStyle(String alias, String type, String pattern, String align) {
        this.alias = alias;
        this.type = type;
        this.pattern = pattern;
        this.align = align;
    }
}
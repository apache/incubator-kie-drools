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
package org.kie.kogito.grafana.model.panel.stat;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldOptions {

    @JsonProperty("calcs")
    public List<String> calcs = generateDefault();

    @JsonProperty("defaults")
    public Defaults defaults = new Defaults();

    @JsonProperty("overrides")
    public List<String> overrides = new ArrayList<>();

    @JsonProperty("values")
    public boolean values = false;

    private List<String> generateDefault() {
        List<String> s = new ArrayList<>();
        s.add("last");
        return s;
    }
}

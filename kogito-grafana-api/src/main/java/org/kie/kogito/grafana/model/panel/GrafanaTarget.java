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
package org.kie.kogito.grafana.model.panel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GrafanaTarget {

    @JsonProperty("expr")
    public String expr;

    @JsonProperty("format")
    public String format;

    @JsonProperty("intervalFactor")
    public int intervalFactor = 2;

    @JsonProperty("refId")
    public String refId = "A";

    @JsonProperty("instant")
    public boolean instant;

    @JsonProperty("legendFormat")
    public String legendFormat;

    public GrafanaTarget() {
    }

    public GrafanaTarget(String expr, String format) {
        this.expr = expr;
        this.format = format;
    }
}
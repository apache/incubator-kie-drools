/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.grafana.model.panel.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class YAxis {

    @JsonProperty("decimals")
    public String decimals = null;

    @JsonProperty("format")
    public String format = "ns";

    @JsonProperty("logBase")
    public int logBase = 1;

    @JsonProperty("max")
    public String max = null;

    @JsonProperty("min")
    public String min = null;

    @JsonProperty("show")
    public boolean show = true;

    @JsonProperty("splitFactor")
    public String splitFactor = null;

    @JsonProperty("label")
    public String label;

    public YAxis() {
    }

    public YAxis(String format, boolean show) {
        this.format = format;
        this.show = show;
    }
}
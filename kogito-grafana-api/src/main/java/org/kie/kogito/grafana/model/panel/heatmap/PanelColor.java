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
package org.kie.kogito.grafana.model.panel.heatmap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PanelColor {

    @JsonProperty("cardColor")
    public String cardColor = "#b4ff00";

    @JsonProperty("colorScale")
    public String colorScale = "sqrt";

    @JsonProperty("colorScheme")
    public String colorScheme = "interpolateOranges";

    @JsonProperty("exponent")
    public double exponent = 0.5;

    @JsonProperty("mode")
    public String mode = "spectrum";
}
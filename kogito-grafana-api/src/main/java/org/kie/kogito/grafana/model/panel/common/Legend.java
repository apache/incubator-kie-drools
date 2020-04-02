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
public class Legend {

    @JsonProperty("avg")
    public boolean avg;

    @JsonProperty("alignAsTable")
    public boolean alignAsTable;

    @JsonProperty("rightSide")
    public boolean rightSide;

    @JsonProperty("legend")
    public boolean legend;

    @JsonProperty("current")
    public boolean current;

    @JsonProperty("max")
    public boolean max;

    @JsonProperty("min")
    public boolean min;

    @JsonProperty("show")
    public boolean show;

    @JsonProperty("total")
    public boolean total;

    @JsonProperty("values")
    public boolean values;
}

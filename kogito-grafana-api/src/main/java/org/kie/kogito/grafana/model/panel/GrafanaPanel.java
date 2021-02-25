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

import java.util.List;

import org.kie.kogito.grafana.model.panel.graph.GraphPanel;
import org.kie.kogito.grafana.model.panel.heatmap.HeatMapPanel;
import org.kie.kogito.grafana.model.panel.stat.SingleStatPanel;
import org.kie.kogito.grafana.model.panel.stat.StatPanel;
import org.kie.kogito.grafana.model.panel.table.TablePanel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TablePanel.class, name = "table"),
        @JsonSubTypes.Type(value = StatPanel.class, name = "stat"),
        @JsonSubTypes.Type(value = SingleStatPanel.class, name = "singleStat"),
        @JsonSubTypes.Type(value = GraphPanel.class, name = "graph"),
        @JsonSubTypes.Type(value = GaugePanel.class, name = "gauge"),
        @JsonSubTypes.Type(value = HeatMapPanel.class, name = "heatmap")
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GrafanaPanel {

    @JsonProperty("datasource")
    public String datasource;

    @JsonProperty("type")
    public String type;

    @JsonProperty("title")
    public String title;

    @JsonProperty("gridPos")
    public GrafanaGridPos gridPos;

    @JsonProperty("id")
    public int id;

    @JsonProperty("pluginVersion")
    public String pluginVersion = "6.6.1";

    @JsonProperty("mode")
    public String mode;

    @JsonProperty("content")
    public String content;

    @JsonProperty("targets")
    public List<GrafanaTarget> targets;

    @JsonProperty("links")
    public List<String> links;

    @JsonProperty("timeFrom")
    public String timeFrom;

    @JsonProperty("timeShift")
    public String timeShift;

    public GrafanaPanel() {
    }

    public GrafanaPanel(int id, String title, String type, GrafanaGridPos gridPos, List<GrafanaTarget> targets) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.gridPos = gridPos;
        this.targets = targets;
    }
}

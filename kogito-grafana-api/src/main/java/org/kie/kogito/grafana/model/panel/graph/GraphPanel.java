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
package org.kie.kogito.grafana.model.panel.graph;

import java.util.List;

import org.kie.kogito.grafana.model.panel.GrafanaGridPos;
import org.kie.kogito.grafana.model.panel.GrafanaPanel;
import org.kie.kogito.grafana.model.panel.GrafanaTarget;
import org.kie.kogito.grafana.model.panel.common.Legend;
import org.kie.kogito.grafana.model.panel.common.Options;
import org.kie.kogito.grafana.model.panel.common.Tooltip;
import org.kie.kogito.grafana.model.panel.common.XAxis;
import org.kie.kogito.grafana.model.panel.common.YAxis;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GraphPanel extends GrafanaPanel {

    @JsonProperty("bars")
    public boolean bars = false;

    @JsonProperty("dashes")
    public boolean dashes = false;

    @JsonProperty("fill")
    public int fill = 1;

    @JsonProperty("fillGradient")
    public int fillGradient = 0;

    @JsonProperty("hiddenSeries")
    public boolean hiddenSeries = false;

    @JsonProperty("linewidth")
    public int linewidth = 1;

    @JsonProperty("pointradius")
    public int pointRadius = 2;

    @JsonProperty("points")
    public boolean points = false;

    @JsonProperty("percentage")
    public boolean percentage = false;

    @JsonProperty("renderer")
    public String renderer = "flot";

    @JsonProperty("spaceLength")
    public int spaceLength = 10;

    @JsonProperty("stack")
    public boolean stack = false;

    @JsonProperty("steppedLine")
    public boolean steppedLine = false;

    @JsonProperty("lines")
    public boolean lines = true;

    @JsonProperty("aliasColors")
    public AliasColors aliasColors;

    @JsonProperty("dashLength")
    public int dashLength;

    @JsonProperty("legend")
    public Legend legend;

    @JsonProperty("nullPointMode")
    public String nullPointMode;

    @JsonProperty("seriesOverrides")
    public List<String> seriesOverrides;

    @JsonProperty("thresholds")
    public List<String> thresholds;

    @JsonProperty("timeRegions")
    public List<String> timeRegions;

    @JsonProperty("tooltip")
    public Tooltip tooltip;

    @JsonProperty("xaxis")
    public XAxis xaxis;

    @JsonProperty("yaxes")
    public List<YAxis> yaxes;

    @JsonProperty("yaxis")
    public GraphYAxis yaxis;

    @JsonProperty("options")
    public Options options;

    public GraphPanel() {
        super();
    }

    public GraphPanel(int id, String title, GrafanaGridPos gridPos, List<GrafanaTarget> targets, List<YAxis> yaxes) {
        super(id, title, "graph", gridPos, targets);
        this.yaxes = yaxes;
    }
}

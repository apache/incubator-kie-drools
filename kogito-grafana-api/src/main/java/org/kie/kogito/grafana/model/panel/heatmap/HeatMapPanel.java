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
public class HeatMapPanel extends GrafanaPanel {

    @JsonProperty("color")
    public PanelColor color = new PanelColor();

    @JsonProperty("dataFormat")
    public String dataFormat = "tsbuckets";

    @JsonProperty("reverseYBuckets")
    public boolean reverseYBuckets = false;

    @JsonProperty("hideZeroBuckets")
    public boolean hideZeroBuckets = false;

    @JsonProperty("highlightCards")
    public boolean highlighCards = true;

    @JsonProperty("yAxis")
    public YAxis yAxis = new YAxis();

    @JsonProperty("yBucketBound")
    public String yBucketBound = "auto";

    @JsonProperty("yBucketNumber")
    public String yBucketNumber = null;

    @JsonProperty("yBucketSize")
    public String yBucketSize = null;

    @JsonProperty("cards")
    public Cards cards;

    @JsonProperty("heatmap")
    public HeatMap heatMap;

    @JsonProperty("legend")
    public Legend legend;

    @JsonProperty("tooltip")
    public Tooltip tooltip;

    @JsonProperty("xAxis")
    public XAxis xAxis;

    @JsonProperty("xBucketNumber")
    public String xBucketNumber;

    @JsonProperty("xBucketSize")
    public String xBucketSize;

    @JsonProperty("options")
    public Options options;

    public HeatMapPanel() {
    }

    public HeatMapPanel(int id, String title, GrafanaGridPos gridPos, List<GrafanaTarget> targets) {
        super(id, title, "heatmap", gridPos, targets);
    }
}

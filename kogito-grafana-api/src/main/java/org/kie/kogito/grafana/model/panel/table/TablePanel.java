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

import java.util.ArrayList;
import java.util.List;

import org.kie.kogito.grafana.model.panel.GrafanaGridPos;
import org.kie.kogito.grafana.model.panel.GrafanaPanel;
import org.kie.kogito.grafana.model.panel.GrafanaTarget;
import org.kie.kogito.grafana.model.panel.common.Options;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TablePanel extends GrafanaPanel {

    @JsonProperty("fontSize")
    public String fontSize = "100%";

    @JsonProperty("showHeader")
    public boolean showHeader = true;

    @JsonProperty("styles")
    public List<BaseStyle> styles = generateStyles();

    @JsonProperty("transform")
    public String transform = "timeseries_to_rows";

    @JsonProperty("sort")
    public TableSort sort = new TableSort();

    @JsonProperty("pageSize")
    public String pageSize;

    @JsonProperty("columns")
    public List<String> columns;

    @JsonProperty("options")
    public Options options;

    public TablePanel() {
        super();
    }

    public TablePanel(int id, String title, GrafanaGridPos gridPos, List<GrafanaTarget> targets) {
        super(id, title, "table", gridPos, targets);
    }

    private List<BaseStyle> generateStyles() {
        List<BaseStyle> ll = new ArrayList<>();
        ll.add(new DateStyle());
        ll.add(new NumberStyle());
        return ll;
    }
}

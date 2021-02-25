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
package org.kie.kogito.grafana.model;

import java.util.ArrayList;
import java.util.List;

import org.kie.kogito.grafana.model.link.GrafanaLink;
import org.kie.kogito.grafana.model.panel.GrafanaPanel;
import org.kie.kogito.grafana.model.templating.GrafanaTemplating;
import org.kie.kogito.grafana.model.time.GrafanaTime;
import org.kie.kogito.grafana.model.time.GrafanaTimePicker;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GrafanaDashboard {

    @JsonProperty("id")
    public String id;

    @JsonProperty("uid")
    public String uid;

    @JsonProperty("gnetId")
    public String gnetId;

    @JsonProperty("title")
    public String title;

    @JsonProperty("tags")
    public List<String> tags = new ArrayList<>();

    @JsonProperty("style")
    public String style = "dark";

    @JsonProperty("timezone")
    public String timezone = "browser";

    @JsonProperty("editable")
    public boolean editable = true;

    @JsonProperty("hideControls")
    public boolean hideControls;

    @JsonProperty("graphTooltip")
    public int graphTooltip;

    @JsonProperty("panels")
    public List<GrafanaPanel> panels = new ArrayList<>();

    @JsonProperty("time")
    public GrafanaTime time;

    @JsonProperty("timepicker")
    public GrafanaTimePicker timepicker;

    @JsonProperty("templating")
    public GrafanaTemplating templating;

    @JsonProperty("refresh")
    public String refresh;

    @JsonProperty("schemaVersion")
    public int schemaVersion = 22;

    @JsonProperty("version")
    public int version = 1;

    @JsonProperty("links")
    public List<GrafanaLink> links = new ArrayList<>();

    @JsonProperty("annotations")
    public Object annotations;

    public GrafanaDashboard() {
    }

    public GrafanaDashboard(String id, String uid, String title) {
        this.id = id;
        this.uid = uid;
        this.title = title;
    }
}

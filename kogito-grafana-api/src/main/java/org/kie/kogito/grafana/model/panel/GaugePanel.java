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

import org.kie.kogito.grafana.model.panel.common.Options;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GaugePanel extends GrafanaPanel {

    @JsonProperty("options")
    public Options options;

    public GaugePanel() {
        super();
    }

    public GaugePanel(int id, String title, GrafanaGridPos gridPos, List<GrafanaTarget> targets) {
        super(id, title, "gauge", gridPos, targets);
    }
}

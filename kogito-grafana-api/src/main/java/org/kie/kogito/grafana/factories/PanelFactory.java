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
package org.kie.kogito.grafana.factories;

import java.util.List;

import org.kie.kogito.grafana.model.panel.GaugePanel;
import org.kie.kogito.grafana.model.panel.GrafanaPanel;
import org.kie.kogito.grafana.model.panel.PanelType;
import org.kie.kogito.grafana.model.panel.common.YAxis;
import org.kie.kogito.grafana.model.panel.graph.GraphPanel;
import org.kie.kogito.grafana.model.panel.heatmap.HeatMapPanel;
import org.kie.kogito.grafana.model.panel.stat.SingleStatPanel;
import org.kie.kogito.grafana.model.panel.stat.StatPanel;
import org.kie.kogito.grafana.model.panel.table.TablePanel;

public class PanelFactory {

    private PanelFactory() {
    }

    public static GrafanaPanel createPanel(PanelType type, int id, String title, String expr, List<YAxis> yaxes) {
        switch (type) {
            case GRAPH:
                return createGraph(id, title, expr, yaxes);
            case STAT:
                return createStat(id, title, expr);
            case HEATMAP:
                return createHeartMap(id, title, expr);
            case SINGLESTAT:
                return createSingleStat(id, title, expr);
            case TABLE:
                return createTable(id, title, expr);
            case GAUGE:
                return createGauge(id, title, expr);
            default:
                throw new UnsupportedOperationException("The panel " + type.toString() + " is not supported.");
        }
    }

    static GrafanaPanel createGraph(int id, String title, String expr, List<YAxis> yaxes) {
        return new GraphPanel(id, title,
                GridPosFactory.calculateGridPosById(id),
                TargetFactory.createTargets(expr),
                yaxes);
    }

    static GrafanaPanel createStat(int id, String title, String expr) {
        return new StatPanel(id, title,
                GridPosFactory.calculateGridPosById(id),
                TargetFactory.createTargets(expr));
    }

    static GrafanaPanel createHeartMap(int id, String title, String expr) {
        return new HeatMapPanel(id, title,
                GridPosFactory.calculateGridPosById(id),
                TargetFactory.createTargets(expr));
    }

    static GrafanaPanel createSingleStat(int id, String title, String expr) {
        return new SingleStatPanel(id, title,
                GridPosFactory.calculateGridPosById(id),
                TargetFactory.createTargets(expr));
    }

    static GrafanaPanel createGauge(int id, String title, String expr) {
        return new GaugePanel(id, title,
                GridPosFactory.calculateGridPosById(id),
                TargetFactory.createTargets(expr));
    }

    static GrafanaPanel createTable(int id, String title, String expr) {
        return new TablePanel(id, title,
                GridPosFactory.calculateGridPosById(id),
                TargetFactory.createTargets(expr));
    }
}

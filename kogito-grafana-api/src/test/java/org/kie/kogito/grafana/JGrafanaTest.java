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
package org.kie.kogito.grafana;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.kie.kogito.grafana.model.functions.GrafanaFunction;
import org.kie.kogito.grafana.model.functions.SumFunction;
import org.kie.kogito.grafana.model.panel.PanelType;
import org.kie.kogito.grafana.model.panel.common.YAxis;
import org.kie.kogito.grafana.model.panel.graph.GraphPanel;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JGrafanaTest {

    @Test
    public void givenANewContextWhenANewJGrafanaObjectIsCreatedThenTheDefaultObjectIsCreated() {
        // Arrange
        JGrafana grafanaObj = new JGrafana("My Dashboard");

        // Assert
        assertEquals(0, grafanaObj.getDashboard().panels.size());
    }

    @Test
    public void givenANewContextWhenANewGraphPanelsAreAddedThenThePanelsAreInTheCorrectPositions() {
        // Arrange
        JGrafana grafanaObj = new JGrafana("My Dashboard");

        // Act
        grafanaObj.addPanel(PanelType.GRAPH, "My Graph 1", "api_http_response_code{handler=\"world\"}");
        grafanaObj.addPanel(PanelType.GRAPH, "My Graph 2", "api_http_response_code{handler=\"world\"}");
        grafanaObj.addPanel(PanelType.GRAPH, "My Graph 3", "api_http_response_code{handler=\"world\"}");
        grafanaObj.addPanel(PanelType.GRAPH, "My Graph 4", "api_http_response_code{handler=\"world\"}");
        grafanaObj.addPanel(PanelType.GRAPH, "My Graph 5", "api_http_response_code{handler=\"world\"}");

        // Assert
        assertEquals(0, grafanaObj.getDashboard().panels.get(0).gridPos.x);
        assertEquals(0, grafanaObj.getDashboard().panels.get(0).gridPos.y);

        assertEquals(12, grafanaObj.getDashboard().panels.get(1).gridPos.x);
        assertEquals(0, grafanaObj.getDashboard().panels.get(1).gridPos.y);

        assertEquals(0, grafanaObj.getDashboard().panels.get(2).gridPos.x);
        assertEquals(8, grafanaObj.getDashboard().panels.get(2).gridPos.y);

        assertEquals(12, grafanaObj.getDashboard().panels.get(3).gridPos.x);
        assertEquals(8, grafanaObj.getDashboard().panels.get(3).gridPos.y);

        assertEquals(0, grafanaObj.getDashboard().panels.get(4).gridPos.x);
        assertEquals(16, grafanaObj.getDashboard().panels.get(4).gridPos.y);
    }

    @Test
    public void givenADashboardWhenAPanelIsDeletedThenTheDashboardIsCorrect() {
        // Arrange
        JGrafana grafanaObj = new JGrafana("My Dashboard");

        // Act
        grafanaObj.addPanel(PanelType.GRAPH, "My Graph 1", "api_http_response_code{handler=\"world\"}");
        grafanaObj.removePanelByTitle("My Graph 1");

        // Assert
        assertEquals(0, grafanaObj.getDashboard().panels.size());
    }

    @Test
    public void givenADashboardWithManyPanelsWhenAPanelIsDeletedThenTheDashboardIsCorrect() {
        // Arrange
        JGrafana grafanaObj = new JGrafana("My Dashboard");

        // Act
        grafanaObj.addPanel(PanelType.GRAPH, "My Graph 1", "api_http_response_code{handler=\"world\"}");
        grafanaObj.addPanel(PanelType.GRAPH, "My Graph 2", "api_http_response_code{handler=\"world\"}");
        grafanaObj.addPanel(PanelType.GRAPH, "My Graph 3", "api_http_response_code{handler=\"world\"}");
        grafanaObj.addPanel(PanelType.GRAPH, "My Graph 4", "api_http_response_code{handler=\"world\"}");
        grafanaObj.addPanel(PanelType.GRAPH, "My Graph 5", "api_http_response_code{handler=\"world\"}");
        grafanaObj.removePanelByTitle("My Graph 3");
        grafanaObj.removePanelByTitle("My Graph 5");

        // Assert
        assertEquals(3, grafanaObj.getDashboard().panels.size());
        assertEquals(true, grafanaObj.getDashboard().panels.stream().anyMatch(x -> x.title == "My Graph 1"));
        assertEquals(true, grafanaObj.getDashboard().panels.stream().anyMatch(x -> x.title == "My Graph 2"));
        assertEquals(true, grafanaObj.getDashboard().panels.stream().anyMatch(x -> x.title == "My Graph 4"));
        assertEquals(false, grafanaObj.getDashboard().panels.stream().anyMatch(x -> x.title == "My Graph 3"));
    }

    @Test
    public void givenADashboardWithManyPanelsWithSameTitleWhenAPanelIsDeletedThenAllThePanelsWithThatNameAreRemoved() {
        JGrafana grafanaObj = new JGrafana("My Dashboard");

        // Act
        grafanaObj.addPanel(PanelType.GRAPH, "My Graph 1", "api_http_response_code{handler=\"world\"}");
        grafanaObj.addPanel(PanelType.HEATMAP, "My Graph 2", "sum(increase(api_execution_elapsed_nanosecond_bucket{handler=\"hello\"}[1m])) by (le)");
        grafanaObj.addPanel(PanelType.STAT, "My Graph 2", "sum(api_http_stacktrace_exceptions)");
        grafanaObj.addPanel(PanelType.TABLE, "My Graph 2", "api_http_stacktrace_exceptions");
        SortedMap<Integer, GrafanaFunction> map = new TreeMap();
        map.put(1, new SumFunction());
        grafanaObj.addPanel(PanelType.GRAPH, "My Graph 3", "api_http_response_code{handler=\"world\"}", map, null);
        grafanaObj.removePanelByTitle("My Graph 2");

        // Assert
        assertEquals(2, grafanaObj.getDashboard().panels.size());
        assertEquals(true, grafanaObj.getDashboard().panels.stream().anyMatch(x -> x.title == "My Graph 1"));
        assertEquals(false, grafanaObj.getDashboard().panels.stream().anyMatch(x -> x.title == "My Graph 2"));
    }

    @Test
    public void givenADashboardWithSomePanelsWhenAnUnexistingPanelIsLookedUpThenAnExceptionIsRaised() {
        JGrafana grafanaObj = new JGrafana("My Dashboard");

        // Act
        grafanaObj.addPanel(PanelType.GRAPH, "My Graph 1", "api_http_response_code{handler=\"world\"}");
        grafanaObj.addPanel(PanelType.HEATMAP, "My Graph 2", "sum(increase(api_execution_elapsed_nanosecond_bucket{handler=\"hello\"}[1m])) by (le)");

        // Assert
        assertThrows(NoSuchElementException.class, () -> {
            grafanaObj.getPanelByTitle("Hello");
        });
    }

    @Test
    public void givenAYAxesObjectWhenAPanelIsAddedThenTheYAxesIsSet() {
        JGrafana grafanaObj = new JGrafana("My Dashboard");
        List<YAxis> yaxes = new ArrayList<>();
        yaxes.add(new YAxis("ms", true));
        yaxes.add(new YAxis("sc", false));

        // Act
        grafanaObj.addPanel(PanelType.GRAPH, "My Graph 1", "api_http_response_code{handler=\"world\"}", new TreeMap<>(), yaxes);

        // Assert
        assertEquals(2, ((GraphPanel) grafanaObj.getPanelByTitle("My Graph 1")).yaxes.size());
    }

    @Test
    public void givenAnExistingDashboardWhenParseMethodIsCalledThenTheDashboardIsImported() {
        assertDoesNotThrow(() -> {
            JGrafana dash = JGrafana.parse(readStandardDashboard());
        });
    }

    public static String readStandardDashboard() {

        InputStream is = JGrafanaTest.class.getResourceAsStream("/org/kie/kogito/grafana/test_dashboard.json");
        return new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining("\n"));
    }
}

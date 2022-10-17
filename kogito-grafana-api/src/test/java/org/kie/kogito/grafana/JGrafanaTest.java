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
import java.util.Objects;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.kie.kogito.grafana.model.panel.PanelType;
import org.kie.kogito.grafana.model.panel.common.YAxis;
import org.kie.kogito.grafana.model.panel.graph.GraphPanel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class JGrafanaTest {

    public static String readProcessOperationalDashboard() {
        return readDashboard("test_process_operational_dashboard.json");
    }

    public static String readStandardDashboard() {
        return readDashboard("test_standard_dashboard.json");
    }

    private static String readDashboard(String dashboardFileName) {

        InputStream is = JGrafanaTest.class.getResourceAsStream("/org/kie/kogito/grafana/" + dashboardFileName);
        return new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining("\n"));
    }

    @Test
    public void givenANewContextWhenANewJGrafanaObjectIsCreatedThenTheDefaultObjectIsCreated() {
        // Arrange
        JGrafana grafanaObj = new JGrafana("My Dashboard");

        // Assert
        assertThat(grafanaObj.getDashboard().panels).isEmpty();
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
        assertThat(grafanaObj.getDashboard().panels.get(0).gridPos.x).isZero();
        assertThat(grafanaObj.getDashboard().panels.get(0).gridPos.y).isZero();

        assertThat(grafanaObj.getDashboard().panels.get(1).gridPos.x).isEqualTo(12);
        assertThat(grafanaObj.getDashboard().panels.get(1).gridPos.y).isZero();

        assertThat(grafanaObj.getDashboard().panels.get(2).gridPos.x).isZero();
        assertThat(grafanaObj.getDashboard().panels.get(2).gridPos.y).isEqualTo(8);

        assertThat(grafanaObj.getDashboard().panels.get(3).gridPos.x).isEqualTo(12);
        assertThat(grafanaObj.getDashboard().panels.get(3).gridPos.y).isEqualTo(8);

        assertThat(grafanaObj.getDashboard().panels.get(4).gridPos.x).isZero();
        assertThat(grafanaObj.getDashboard().panels.get(4).gridPos.y).isEqualTo(16);
    }

    @Test
    public void givenADashboardWhenAPanelIsDeletedThenTheDashboardIsCorrect() {
        // Arrange
        JGrafana grafanaObj = new JGrafana("My Dashboard");

        // Act
        grafanaObj.addPanel(PanelType.GRAPH, "My Graph 1", "api_http_response_code{handler=\"world\"}");
        grafanaObj.removePanelByTitle("My Graph 1");

        // Assert
        assertThat(grafanaObj.getDashboard().panels).isEmpty();
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
        assertThat(grafanaObj.getDashboard().panels).hasSize(3);
        assertThat(grafanaObj.getDashboard().panels.stream().anyMatch(x -> Objects.equals(x.title, "My Graph 1"))).isTrue();
        assertThat(grafanaObj.getDashboard().panels.stream().anyMatch(x -> Objects.equals(x.title, "My Graph 2"))).isTrue();
        assertThat(grafanaObj.getDashboard().panels.stream().anyMatch(x -> Objects.equals(x.title, "My Graph 4"))).isTrue();
        assertThat(grafanaObj.getDashboard().panels.stream().anyMatch(x -> Objects.equals(x.title, "My Graph 3"))).isFalse();
    }

    @Test
    public void givenADashboardWithManyPanelsWithSameTitleWhenAPanelIsDeletedThenAllThePanelsWithThatNameAreRemoved() {
        JGrafana grafanaObj = new JGrafana("My Dashboard");

        // Act
        grafanaObj.addPanel(PanelType.GRAPH, "My Graph 1", "api_http_response_code{handler=\"world\"}");
        grafanaObj.addPanel(PanelType.HEATMAP, "My Graph 2", "sum(increase(api_execution_elapsed_nanosecond_bucket{handler=\"hello\"}[1m])) by (le)");
        grafanaObj.addPanel(PanelType.STAT, "My Graph 2", "sum(api_http_stacktrace_exceptions)");
        grafanaObj.addPanel(PanelType.TABLE, "My Graph 2", "api_http_stacktrace_exceptions");
        grafanaObj.addPanel(PanelType.GRAPH, "My Graph 3", "api_http_response_code{handler=\"world\"}", null);
        grafanaObj.removePanelByTitle("My Graph 2");

        // Assert
        assertThat(grafanaObj.getDashboard().panels).hasSize(2);
        assertThat(grafanaObj.getDashboard().panels.stream().anyMatch(x -> x.title == "My Graph 1")).isTrue();
        assertThat(grafanaObj.getDashboard().panels.stream().anyMatch(x -> x.title == "My Graph 2")).isFalse();
    }

    @Test
    public void givenADashboardWithSomePanelsWhenAnUnexistingPanelIsLookedUpThenAnExceptionIsRaised() {
        JGrafana grafanaObj = new JGrafana("My Dashboard");

        // Act
        grafanaObj.addPanel(PanelType.GRAPH, "My Graph 1", "api_http_response_code{handler=\"world\"}");
        grafanaObj.addPanel(PanelType.HEATMAP, "My Graph 2", "sum(increase(api_execution_elapsed_nanosecond_bucket{handler=\"hello\"}[1m])) by (le)");

        // Assert
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> {
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
        grafanaObj.addPanel(PanelType.GRAPH, "My Graph 1", "api_http_response_code{handler=\"world\"}", yaxes);

        // Assert
        assertThat(((GraphPanel) grafanaObj.getPanelByTitle("My Graph 1")).yaxes).hasSize(2);
    }

    @Test
    public void givenAnExistingStandardDashboardWhenParseMethodIsCalledThenTheDashboardIsImported() {
        assertThatNoException().isThrownBy(() -> {
            JGrafana dash = JGrafana.parse(readStandardDashboard());
        });
    }

    @Test
    public void givenAnExistingProcessOperationalDashboardWhenParseMethodIsCalledThenTheDashboardIsImported() {
        assertThatNoException().isThrownBy(() -> {
            JGrafana dash = JGrafana.parse(readProcessOperationalDashboard());
        });
    }
}

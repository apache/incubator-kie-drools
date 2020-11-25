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

package org.kie.kogito.grafana;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.SortedMap;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kie.kogito.grafana.factories.GridPosFactory;
import org.kie.kogito.grafana.factories.PanelFactory;
import org.kie.kogito.grafana.model.GrafanaDashboard;
import org.kie.kogito.grafana.model.functions.ExprBuilder;
import org.kie.kogito.grafana.model.functions.GrafanaFunction;
import org.kie.kogito.grafana.model.link.GrafanaLink;
import org.kie.kogito.grafana.model.panel.GrafanaPanel;
import org.kie.kogito.grafana.model.panel.PanelType;
import org.kie.kogito.grafana.model.panel.common.YAxis;

/**
 * Java configurator to create standard grafana dashboards
 */
public class JGrafana {

    private GrafanaDashboard dashboard;

    /**
     * Create a new JGrafana instance.
     *
     * @param title: The title of your dashboard.
     */
    public JGrafana(String title) {
        this(title, UUID.randomUUID().toString());
    }

    /**
     * Create a new JGrafana instance.
     *
     * @param title: The title of your dashboard.
     * @param uuid:  The uuid of your dashboard.
     */
    public JGrafana(String title, String uuid) {
        this.dashboard = new GrafanaDashboard(null, uuid, title);
    }

    /**
     * Create a new JGrafana dashboard from a grafana dashboard object.
     *
     * @param dashboard: The grafana dashboard.
     */
    public JGrafana(GrafanaDashboard dashboard) {
        this.dashboard = dashboard;
    }

    /**
     * Parse a json grafana dashboard and returns the JGrafana object containing that dashboard.
     * Panels inside the dashboard change the id and the dimensions are standardized.
     *
     * @param dashboard
     * @return
     * @throws JsonProcessingException
     */
    public static JGrafana parse(String dashboard) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        GrafanaDashboard dash = mapper.readValue(dashboard, GrafanaDashboard.class);
        for (int i = 0; i < dash.panels.size(); i++) {
            GrafanaPanel p = dash.panels.get(i);
            p.id = i + 1;
            p.gridPos = GridPosFactory.calculateGridPosById(i + 1);
        }
        return new JGrafana(dash);
    }

    /**
     * Returns the dashboard.
     *
     * @return: The GrafanaDashboard.
     */
    public GrafanaDashboard getDashboard() {
        return dashboard;
    }

    /**
     * Adds a panel of a type to the dashboard.
     *
     * @param type:  The type of the panel to be added.
     * @param title: Title of the panel.
     * @param expr:  Prompql expression of the panel.
     * @return: The grafana panel added to the dashboard.
     */
    public GrafanaPanel addPanel(PanelType type, String title, String expr) {
        return addPanel(type, title, expr, null, null);
    }

    /**
     * Remove a panel by title.
     *
     * @param title: The title of the panel to be removed.
     * @return: true if the panel has been remove, false otherwise.
     */
    public boolean removePanelByTitle(String title) {
        return this.dashboard.panels.removeIf(x -> x.title.equals(title));
    }

    /**
     * Adds a panel of a type to the dashboard.
     *
     * @param type:      The type of the panel to be added.
     * @param title:     Title of the panel.
     * @param expr:      Prompql expression of the panel.
     * @param functions: The Grafana functions to be applied to the PrompQL query.
     * @param yaxes:     The YAxis of the panel.
     * @return: The grafana panel added to the dashboard.
     */
    public GrafanaPanel addPanel(PanelType type, String title, String expr, SortedMap<Integer, GrafanaFunction> functions, List<YAxis> yaxes) {
        int id = this.dashboard.panels.size() + 1;
        if (functions != null && functions.size() != 0) {
            expr = ExprBuilder.apply(expr, functions);
        }
        GrafanaPanel panel = PanelFactory.createPanel(type, id, title, expr, yaxes);
        this.dashboard.panels.add(panel);
        return panel;
    }

    /**
     * Gets a panel by title.
     *
     * @param title: The panel title to be retrieved.
     * @return: The panel.
     */
    public GrafanaPanel getPanelByTitle(String title) {
        Optional<GrafanaPanel> panel = this.dashboard.panels.stream().filter(x -> x.title.equals(title)).findFirst();
        if (!panel.isPresent()) {
            throw new NoSuchElementException(String.format("There is no panel with title \"%s\"", title));
        }
        return panel.get();
    }

    /**
     * Sets the dashboard title.
     *
     * @param title: The title of the dashboard.
     * @return: The updated object.
     */
    public JGrafana setTitle(String title) {
        this.dashboard.title = title;
        return this;
    }

    /**
     * Adds an external link to the dashboard.
     *
     * @param title: The title of the link.
     * @param url:   The url of the link.
     * @return: The updated object.
     */
    public JGrafana addLink(String title, String url) {
        this.dashboard.links.add(new GrafanaLink(title, url));
        return this;
    }

    /**
     * Writes the dashboard to a file.
     *
     * @param file: The file to be written.
     * @throws IOException
     */
    public void writeToFile(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writer(new DefaultPrettyPrinter());
        mapper.writeValue(file, this.dashboard);
    }

    /**
     * Serialize the dashboard.
     *
     * @throws IOException
     * @return: The serialized json dashboard.
     */
    public String serialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this.dashboard);
    }
}

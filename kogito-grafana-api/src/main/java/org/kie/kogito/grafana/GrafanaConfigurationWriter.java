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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.kie.dmn.model.api.Decision;
import org.kie.kogito.grafana.dmn.SupportedDecisionTypes;
import org.kie.kogito.grafana.model.panel.PanelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrafanaConfigurationWriter {

    private static final Logger logger = LoggerFactory.getLogger(GrafanaConfigurationWriter.class);

    private static final String AUDIT_LINK_NAME = "Audit UI";
    private static final String AUDIT_LINK_URL_PLACEHOLDER = "${{urlPlaceholder}}";

    private GrafanaConfigurationWriter() {
        // Intentionally left blank.
    }

    /**
     * Generates an operational grafana dashboard based on a given template.
     *
     * @param templatePath: The path to the dashboard template. It must be a valid grafana dashboard in JSON format.
     * @param handlerName:  The name of the endpoint.
     * @return: The template customized for the endpoint.
     */
    public static String generateOperationalDashboard(String templatePath, String handlerName, boolean generateAuditLink) {
        String template = readStandardDashboard(templatePath);
        template = customizeTemplate(template, handlerName);
        JGrafana jgrafana;
        try {
            jgrafana = JGrafana.parse(template).setTitle(String.format("%s - Operational Dashboard", handlerName));
        } catch (IOException e) {
            logger.error(String.format("Could not parse the grafana template for the endpoint %s", handlerName), e);
            throw new IllegalArgumentException("Could not parse the dashboard template.", e);
        }

        if (generateAuditLink) {
            jgrafana.addLink(AUDIT_LINK_NAME, AUDIT_LINK_URL_PLACEHOLDER);
        }

        try {
            return jgrafana.serialize();
        } catch (IOException e) {
            logger.error(String.format("Could not serialize the grafana dashboard for the endpoint %s", handlerName), e);
            throw new RuntimeException("Could not serialize the grafana dashboard.", e);
        }
    }

    /**
     * Generates domain specific dashboard from a given dashboard template.
     *
     * @param templatePath: The path to the dashboard template. It must be a valid grafana dashboard in JSON format.
     * @param endpoint:     The name of the endpoint.
     * @param decisions:    The decisions in the DMN model.
     * @return: The customized template containing also specific panels for the DMN decisions that have been specified in the arguments.
     */
    public static String generateDomainSpecificDMNDashboard(String templatePath, String endpoint, List<Decision> decisions, boolean generateAuditLink) {
        String template = readStandardDashboard(templatePath);
        template = customizeTemplate(template, endpoint);

        JGrafana jgrafana = null;
        try {
            jgrafana = JGrafana.parse(template).setTitle(String.format("%s - Domain Dashboard", endpoint));
        } catch (IOException e) {
            logger.error(String.format("Could not parse the grafana template for the endpoint %s", endpoint), e);
            throw new IllegalArgumentException("Could not parse the dashboard template.", e);
        }

        if (generateAuditLink) {
            jgrafana.addLink(AUDIT_LINK_NAME, AUDIT_LINK_URL_PLACEHOLDER);
        }

        for (Decision decision : decisions) {
            QName type = decision.getVariable().getTypeRef();
            if (type == null) {
                logger.warn(String.format("DMN typeref for the decision \"%s\" with node id \"%s\" is null.", decision.getName(), decision.getId()));
            } else {
                if (SupportedDecisionTypes.isSupported(type.getLocalPart())) {
                    jgrafana.addPanel(PanelType.GRAPH,
                                      "Decision " + decision.getName(),
                                      String.format("%s_dmn_result%s{endpoint = \"%s\", decision = \"%s\"}",
                                                    type.toString().replace(" ", "_"),
                                                    SupportedDecisionTypes.getNameSuffix(type.getLocalPart()),
                                                    endpoint,
                                                    decision.getName()),
                                      SupportedDecisionTypes.getGrafanaFunction(type.getLocalPart()),
                                      SupportedDecisionTypes.getYAxis(type.getLocalPart()));
                }
            }
        }

        try {
            return jgrafana.serialize();
        } catch (IOException e) {
            logger.error(String.format("Could not serialize the grafana dashboard for the endpoint %s", endpoint), e);
            throw new RuntimeException("Could not serialize the grafana dashboard.", e);
        }
    }

    private static String readStandardDashboard(String templatePath) {
        InputStream is = GrafanaConfigurationWriter.class.getResourceAsStream(templatePath);
        return new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining("\n"));
    }

    private static String customizeTemplate(String template, String handlerName) {
        template = template.replaceAll("\\$handlerName\\$", handlerName);
        template = template.replaceAll("\\$id\\$", String.valueOf(new Random().nextInt()));
        template = template.replaceAll("\\$uid\\$", UUID.randomUUID().toString());
        return template;
    }
}
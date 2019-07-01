/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.scenariosimulation.backend.util;

import java.util.function.Function;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.drools.scenariosimulation.api.model.ExpressionElement;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.FactMappingValue;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.api.model.SimulationDescriptor;
import org.kie.soup.commons.xstream.XStreamUtils;
import org.kie.soup.project.datamodel.imports.Import;

public class InMemoryMigrationStrategy implements MigrationStrategy {

    @Override
    public Function<String, String> from1_0to1_1() {
        return rawXml -> updateVersion(rawXml, "1.0", "1.1")
                .replaceAll("EXPECTED", "EXPECT");
    }

    @Override
    public Function<String, String> from1_1to1_2() {
        return rawXml -> {
            String updatedVersion = updateVersion(rawXml, "1.1", "1.2");
            if ((updatedVersion.contains("<dmoSession>") || (updatedVersion.contains("<dmnFilePath>")) && (updatedVersion.contains("<type>")))) {
                return updatedVersion;
            } else {
                return updatedVersion.replaceAll("</simulationDescriptor>", "<dmoSession></dmoSession>\n<type>RULE</type>\n</simulationDescriptor>");
            }
        };
    }

    @Override
    public Function<String, String> from1_2to1_3() {
        return rawXml -> {
            ScenarioSimulationXMLPersistence xmlPersistence = ScenarioSimulationXMLPersistence.getInstance();
            ScenarioSimulationModel model = xmlPersistence.unmarshal(rawXml, false);
            for (FactMapping factMapping : model.getSimulation().getSimulationDescriptor().getUnmodifiableFactMappings()) {
                factMapping.getExpressionElements().add(0, new ExpressionElement(factMapping.getFactIdentifier().getName()));
            }
            return updateVersion(xmlPersistence.marshal(model), "1.2", "1.3");
        };
    }

    @Override
    public Function<String, String> from1_3to1_4() {
        return rawXml -> {
            if (rawXml.contains("<type>")) {
                StringBuilder replacementBuilder = new StringBuilder();
                String toReplace = null;
                if (rawXml.contains("<type>RULE</type>")) {
                    toReplace = "<type>RULE</type>";
                    if (!rawXml.contains("<kieSession>")) {
                        replacementBuilder.append("<kieSession>default</kieSession>\n");
                    }
                    if (!rawXml.contains("<kieBase>")) {
                        replacementBuilder.append("<kieBase>default</kieBase>\n");
                    }
                    if (!rawXml.contains("<ruleFlowGroup>")) {
                        replacementBuilder.append("<ruleFlowGroup>default</ruleFlowGroup>\n");
                    }
                    if (!rawXml.contains("<skipFromBuild>")) {
                        replacementBuilder.append("<skipFromBuild>false</skipFromBuild>\n");
                    }
                    replacementBuilder.append("<type>RULE</type>");
                } else if (rawXml.contains("<type>DMN</type>")) {
                    toReplace = "<type>DMN</type>";
                    if (!rawXml.contains("<dmnNamespace>")) {
                        replacementBuilder.append("<dmnNamespace></dmnNamespace>\n");
                    }
                    if (!rawXml.contains("<dmnName>")) {
                        replacementBuilder.append("<dmnName></dmnName>\n");
                    }
                    if (!rawXml.contains("<skipFromBuild>")) {
                        replacementBuilder.append("<skipFromBuild>false</skipFromBuild>\n");
                    }
                    replacementBuilder.append("<type>DMN</type>");
                }
                String toReturn = updateVersion(rawXml, "1.3", "1.4")
                        .replaceAll("<simulationDescriptor>", "<simulationDescriptor>\n  <fileName></fileName>");
                String replacement = replacementBuilder.toString();
                if (toReplace != null && !toReplace.equals(replacement)) {
                    toReturn = toReturn.replaceAll(toReplace, replacement);
                }
                return toReturn;
            } else {
                return rawXml;
            }
        };
    }

    @Override
    public Function<String, String> from1_4to1_5() {
        return rawXml -> {
            ScenarioSimulationXMLPersistence xmlPersistence = ScenarioSimulationXMLPersistence.getInstance();
            ScenarioSimulationModel model = xmlPersistence.unmarshal(rawXml, false);
            String dmoSession = model.getSimulation().getSimulationDescriptor().getDmoSession();
            if ("default".equals(dmoSession) || "".equals(dmoSession)) {
                model.getSimulation().getSimulationDescriptor().setDmoSession(null);
            }
            return updateVersion(xmlPersistence.marshal(model), "1.4", "1.5");
        };
    }

    @Override
    public Function<String, String> from1_5to1_6() {
        return rawXml -> {
            // We need to do those things here because to parse "old" xmls we need a differently configured Xstream
            ScenarioSimulationXMLPersistence xmlPersistence = ScenarioSimulationXMLPersistence.getInstance();
            if (rawXml == null ||  rawXml.trim().equals("")) {
                return xmlPersistence.marshal(new ScenarioSimulationModel());
            }

            // Unmarshall the 1.5 format with "older" xstream configuration, that read "reference" attributes
            Object o = getLocalXStream().fromXML(rawXml);
            ScenarioSimulationModel model = (ScenarioSimulationModel) o;

            // Marshall the model with the "new" xstream configuration, that does not write "reference" attributes
            return updateVersion(xmlPersistence.marshal(model), "1.5", "1.6");
        };
    }

    private XStream getLocalXStream() {
        // We need this local instance to instantiate XStream with older settings
        XStream toReturn = XStreamUtils.createTrustingXStream(new DomDriver());

        toReturn.autodetectAnnotations(true);

        toReturn.alias("ExpressionElement", ExpressionElement.class);
        toReturn.alias("ExpressionIdentifier", ExpressionIdentifier.class);
        toReturn.alias("FactIdentifier", FactIdentifier.class);
        toReturn.alias("FactMapping", FactMapping.class);
        toReturn.alias("FactMappingType", FactMappingType.class);
        toReturn.alias("FactMappingValue", FactMappingValue.class);
        toReturn.alias("Scenario", Scenario.class);
        toReturn.alias("ScenarioSimulationModel", ScenarioSimulationModel.class);
        toReturn.alias("Simulation", Simulation.class);
        toReturn.alias("SimulationDescriptor", SimulationDescriptor.class);

        toReturn.alias("Import", Import.class);
        return toReturn;
    }
}

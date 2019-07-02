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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class ScenarioSimulationXMLPersistence {

    private static final ScenarioSimulationXMLPersistence INSTANCE = new ScenarioSimulationXMLPersistence();
    private static final String currentVersion = new ScenarioSimulationModel().getVersion();
    private static final Pattern p = Pattern.compile("version=\"([0-9]+\\.[0-9]+)");

    private XStream xt;
    private MigrationStrategy migrationStrategy = new InMemoryMigrationStrategy();

    private ScenarioSimulationXMLPersistence() {
        xt = XStreamUtils.createTrustingXStream(new DomDriver());

        xt.setMode(XStream.NO_REFERENCES);
        xt.autodetectAnnotations(true);
        configureXStreamMappings(xt);
    }

    /**
     * Method to configure the commonly-used mappings defined in {@see XSTREAM_MAPPINGS}
     * @param toConfigure
     */
    public static void configureXStreamMappings(XStream toConfigure) {
        toConfigure.alias("ExpressionElement", ExpressionElement.class);
        toConfigure.alias("ExpressionIdentifier", ExpressionIdentifier.class);
        toConfigure.alias("FactIdentifier", FactIdentifier.class);
        toConfigure.alias("FactMapping", FactMapping.class);
        toConfigure.alias("FactMappingType", FactMappingType.class);
        toConfigure.alias("FactMappingValue", FactMappingValue.class);
        toConfigure.alias("Scenario", Scenario.class);
        toConfigure.alias("ScenarioSimulationModel", ScenarioSimulationModel.class);
        toConfigure.alias("Simulation", Simulation.class);
        toConfigure.alias("SimulationDescriptor", SimulationDescriptor.class);
        toConfigure.alias("Import", Import.class);
    }

    public static ScenarioSimulationXMLPersistence getInstance() {
        return INSTANCE;
    }

    public static String getCurrentVersion() {
        return currentVersion;
    }

    public static String cleanUpUnusedNodes(String input) {
        String toRemove = "<simulationDescriptor reference\\b[^>]*>(.*?)";
        return input.replaceAll(toRemove, "");
    }

    public String marshal(final ScenarioSimulationModel sc) {
        return xt.toXML(sc);
    }

    public ScenarioSimulationModel unmarshal(final String rawXml) {
        return unmarshal(rawXml, true);
    }

    public ScenarioSimulationModel unmarshal(final String rawXml, boolean migrate) {
        if (rawXml == null) {
            return new ScenarioSimulationModel();
        }
        if (rawXml.trim().equals("")) {
            return new ScenarioSimulationModel();
        }

        String xml = migrate ? migrateIfNecessary(rawXml) : rawXml;

        return internalUnmarshal(xml);
    }

    public String migrateIfNecessary(String rawXml) {
        rawXml = cleanUpUnusedNodes(rawXml);
        String fileVersion = extractVersion(rawXml);
        Function<String, String> migrator = getMigrationStrategy().start();
        boolean supported;
        switch (fileVersion) {
            case "1.0":
                migrator = migrator.andThen(getMigrationStrategy().from1_0to1_1());
            case "1.1":
                migrator = migrator.andThen(getMigrationStrategy().from1_1to1_2());
            case "1.2":
                migrator = migrator.andThen(getMigrationStrategy().from1_2to1_3());
            case "1.3":
                migrator = migrator.andThen(getMigrationStrategy().from1_3to1_4());
            case "1.4":
                migrator = migrator.andThen(getMigrationStrategy().from1_4to1_5());
            case "1.5":
                migrator = migrator.andThen(getMigrationStrategy().from1_5to1_6());
                supported = true;
                break;
            default:
                supported = currentVersion.equals(fileVersion);
                break;
        }
        if (!supported) {
            throw new IllegalArgumentException(new StringBuilder().append("Version ").append(fileVersion)
                                                       .append(" of the file is not supported. Current version is ")
                                                       .append(currentVersion).toString());
        }
        migrator = migrator.andThen(getMigrationStrategy().end());
        return migrator.apply(rawXml);
    }

    public String extractVersion(String rawXml) {
        Matcher m = p.matcher(rawXml);

        if (m.find()) {
            return m.group(1);
        }
        throw new IllegalArgumentException("Impossible to extract version from the file");
    }

    public MigrationStrategy getMigrationStrategy() {
        return migrationStrategy;
    }

    public void setMigrationStrategy(MigrationStrategy migrationStrategy) {
        this.migrationStrategy = migrationStrategy;
    }

    protected ScenarioSimulationModel internalUnmarshal(String xml) {
        xml = cleanUpUnusedNodes(xml);
        Object o = xt.fromXML(xml);
        return (ScenarioSimulationModel) o;
    }
}

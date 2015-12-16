/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.testscenarios.backend.util;

import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.Expectation;
import org.drools.workbench.models.testscenarios.shared.FactData;
import org.drools.workbench.models.testscenarios.shared.CollectionFieldData;
import org.drools.workbench.models.testscenarios.shared.Fact;
import org.drools.workbench.models.testscenarios.shared.FactAssignmentField;
import org.drools.workbench.models.testscenarios.shared.Field;
import org.drools.workbench.models.testscenarios.shared.FieldData;
import org.drools.workbench.models.testscenarios.shared.FieldPlaceHolder;
import org.drools.workbench.models.testscenarios.shared.Fixture;
import org.drools.workbench.models.testscenarios.shared.RetractFact;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.models.testscenarios.shared.VerifyFact;
import org.drools.workbench.models.testscenarios.shared.VerifyField;
import org.drools.workbench.models.testscenarios.shared.VerifyRuleFired;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


/**
 * Persists the scenario model.
 */
public class ScenarioXMLPersistence {

    private XStream xt;
    private static final ScenarioXMLPersistence INSTANCE = new ScenarioXMLPersistence();

    private ScenarioXMLPersistence() {
        xt = new XStream(new DomDriver());
        xt.alias("scenario", Scenario.class);
        xt.alias("execution-trace", ExecutionTrace.class);
        xt.alias("expectation", Expectation.class);
        xt.alias("fact-data", FactData.class);
        xt.alias("fact", Fact.class);
        xt.alias("field-data", Field.class);
        xt.alias("field-data", FieldPlaceHolder.class);
        xt.alias("field-data", FieldData.class);
        xt.alias("field-data", FactAssignmentField.class);
        xt.alias("field-data", CollectionFieldData.class);
        xt.alias("fixture", Fixture.class);
        xt.alias("retract-fact", RetractFact.class);
        xt.alias("expect-fact", VerifyFact.class);
        xt.alias("expect-field", VerifyField.class);
        xt.alias("expect-rule", VerifyRuleFired.class);


        xt.omitField(ExecutionTrace.class, "rulesFired");

        //See https://issues.jboss.org/browse/GUVNOR-1115
        xt.aliasPackage("org.drools.guvnor.client", "org.drools.ide.common.client");

        xt.registerConverter(new FieldConverter(xt));

    }

    public static ScenarioXMLPersistence getInstance() {
        return INSTANCE;
    }


    public String marshal(Scenario sc) {
        if (sc.getFixtures().size() > 1 && sc.getFixtures().get(sc.getFixtures().size() - 1) instanceof ExecutionTrace) {
            Object f = sc.getFixtures().get(sc.getFixtures().size() - 2);

            if (f instanceof Expectation) {
                sc.getFixtures().remove(sc.getFixtures().size() - 1);
            }

        }
        String s = xt.toXML(sc);
        return s;
    }

    public Scenario unmarshal(String xml) {
        if (xml == null) return new Scenario();
        if (xml.trim().equals("")) return new Scenario();
        Object o = xt.fromXML(xml);

        Scenario scenario = (Scenario) o;

        if (scenario.getImports() == null) {
            scenario.setImports(new Imports());
        }

        return scenario;
    }

}

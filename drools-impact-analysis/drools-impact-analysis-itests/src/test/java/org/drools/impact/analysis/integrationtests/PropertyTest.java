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

package org.drools.impact.analysis.integrationtests;

import org.drools.impact.analysis.graph.Graph;
import org.drools.impact.analysis.graph.ModelToGraphConverter;
import org.drools.impact.analysis.graph.ReactivityType;
import org.drools.impact.analysis.integrationtests.domain.Address;
import org.drools.impact.analysis.integrationtests.domain.Person;
import org.drools.impact.analysis.model.AnalysisModel;
import org.drools.impact.analysis.parser.ModelBuilder;
import org.junit.Test;

/**
 * 
 * Tests related to property analysis. Nested, function, etc.
 *
 */
public class PropertyTest extends AbstractGraphTest {

    @Test
    public void testNestedProperty() {
        String str =
                "package mypkg;\n" +
                     "import " + Person.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "  $p : Person()\n" +
                     "then\n" +
                     "  modify ($p) {getAddress().setNumber(10)};" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $p : Person(address.number == 10)\n" +
                     "then\n" +
                     "end\n";

        runRule(str, new Person("John", 20, new Address()));

        AnalysisModel analysisModel = new ModelBuilder().build(str);

        // [modify ($p) {getAddress().setNumber(10)};] is parsed to modifiedProperties=[ModifiedProperty{property='address', value=null}]
        // [address.number == 10] is parsed to Constraint{type=EQUAL, property='null', value=10}
        // Currently, it results in UNKNOWN impact. Do we want to analyze this to POSITIVE? (TODO: Confirm customer's expectation)

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.UNKNOWN);

        generatePng(graph);
    }

    @Test
    public void testPropertyInFunction() {
        String str =
                "package mypkg;\n" +
                     "import " + Person.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "  $p : Person()\n" +
                     "then\n" +
                     "  modify ($p) {setAge(42)};" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $p : Person(calculateToMonth(age) > 480)\n" +
                     "then\n" +
                     "end\n";

        runRule(str, new Person("John"));

        AnalysisModel analysisModel = new ModelBuilder().build(str);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.UNKNOWN);

        generatePng(graph);
    }
}

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
 * Tests related to DRL syntax. exists, not, ,accumulate etc.
 *
 */
public class DrlSyntaxTest extends AbstractGraphTest {

    @Test
    public void testExists1() {
        String str =
                "package mypkg;\n" +
                     "import " + Person.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "then\n" +
                     "  insert(new Person());" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  exists (Person())\n" +
                     "then\n" +
                     "end\n";

        AnalysisModel analysisModel = new ModelBuilder().build(str);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.POSITIVE);

        generatePng(graph);

    }

    @Test
    public void testExists2() {
        String str =
                "package mypkg;\n" +
                     "import " + Person.class.getCanonicalName() + ";" +
                     "import " + Address.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "  $p : Person()\n" +
                     "then\n" +
                     "  delete($p);" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  exists (Person())\n" +
                     "then\n" +
                     "end\n";

        AnalysisModel analysisModel = new ModelBuilder().build(str);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R1", ReactivityType.NEGATIVE);
        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.NEGATIVE);

        generatePng(graph);
    }

    @Test
    public void testNot1() {
        String str =
                "package mypkg;\n" +
                     "import " + Person.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "then\n" +
                     "  insert(new Person());" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  not (Person())\n" +
                     "then\n" +
                     "end\n";

        AnalysisModel analysisModel = new ModelBuilder().build(str);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.NEGATIVE);

        generatePng(graph);

    }

    @Test
    public void testNot2() {
        String str =
                "package mypkg;\n" +
                     "import " + Person.class.getCanonicalName() + ";" +
                     "import " + Address.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "  $p : Person()\n" +
                     "then\n" +
                     "  delete($p);" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  not (Person())\n" +
                     "then\n" +
                     "end\n";

        AnalysisModel analysisModel = new ModelBuilder().build(str);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R1", ReactivityType.NEGATIVE);
        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.POSITIVE);

        generatePng(graph);
    }
}

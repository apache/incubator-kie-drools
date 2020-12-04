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
import org.drools.impact.analysis.graph.Link;
import org.drools.impact.analysis.graph.ModelToGraphConverter;
import org.drools.impact.analysis.graph.graphviz.GraphImageGenerator;
import org.drools.impact.analysis.integrationtests.domain.Address;
import org.drools.impact.analysis.integrationtests.domain.Person;
import org.drools.impact.analysis.model.AnalysisModel;
import org.drools.impact.analysis.parser.ModelBuilder;
import org.junit.Test;

public class BasicGraphTest extends AbstractGraphTest {

    @Test
    public void test3Rules() {
        String str =
                "package mypkg;\n" +
                     "import " + Person.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "  $p : Person(name == \"Mario\")\n" +
                     "then\n" +
                     "  modify($p) { setAge( 18 ) };" +
                     "  insert(\"Done\");\n" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $p : Person(age > 15)\n" +
                     "then\n" +
                     "end\n" +
                     "rule R3 when\n" +
                     "  $p : String(this == \"Done\")\n" +
                     "then\n" +
                     "end\n";

        AnalysisModel analysisModel = new ModelBuilder().build(str);
        //System.out.println(analysisModel);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertNodeLink(graph, "mypkg.R1", "mypkg.R2", Link.Type.POSITIVE);
        assertNodeLink(graph, "mypkg.R1", "mypkg.R3", Link.Type.POSITIVE);

        GraphImageGenerator generator = new GraphImageGenerator("3rules");
        generator.generatePng(graph);
    }

    @Test
    public void test5Rules() {
        String str =
                "package mypkg;\n" +
                     "import " + Person.class.getCanonicalName() + ";" +
                     "import " + Address.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "  $p : Person(name == \"Mario\")\n" +
                     "then\n" +
                     "  modify($p) { setAge( 18 ) };" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $p : Person(age > 15)\n" +
                     "then\n" +
                     "  insert(new Address(\"Milan\"));" +
                     "end\n" +
                     "rule R3 when\n" +
                     "  $p : Person(age < 15)\n" +
                     "then\n" +
                     "  insert(new Address(\"Milan\"));" +
                     "end\n" +
                     "rule R4 when\n" +
                     "  $a : Address()\n" +
                     "then\n" +
                     "end\n" +
                     "rule R5 when\n" +
                     "  $i : Integer()\n" +
                     "then\n" +
                     "end\n";

        AnalysisModel analysisModel = new ModelBuilder().build(str);
        //System.out.println(analysisModel);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertNodeLink(graph, "mypkg.R1", "mypkg.R2", Link.Type.POSITIVE);
        assertNodeLink(graph, "mypkg.R1", "mypkg.R3", Link.Type.NEGATIVE);
        assertNodeLink(graph, "mypkg.R2", "mypkg.R4", Link.Type.POSITIVE);
        assertNodeLink(graph, "mypkg.R3", "mypkg.R4", Link.Type.POSITIVE);

        GraphImageGenerator generator = new GraphImageGenerator("5rules");
        generator.generatePng(graph);
    }

    @Test
    public void testBeta() {
        String str =
                "package mypkg;\n" +
                     "import " + Person.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "  $p : Person(name == \"Mario\")\n" +
                     "then\n" +
                     "  modify($p) { setAge( 18 ) };" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $a : Integer()\n" +
                     "  $p2 : Person(age > $a)\n" +
                     "then\n" +
                     "end\n";

        AnalysisModel analysisModel = new ModelBuilder().build(str);
        //System.out.println(analysisModel);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertNodeLink(graph, "mypkg.R1", "mypkg.R2", Link.Type.UNKNOWN);

        GraphImageGenerator generator = new GraphImageGenerator("beta");
        generator.generatePng(graph);
    }

    @Test
    public void testLoop() {
        String str =
                "package mypkg;\n" +
                     "import " + Person.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "  $p : Person(name == \"Mario\")\n" +
                     "then\n" +
                     "  modify($p) { setAge( 18 ) };" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $p : Person(age > 10)\n" +
                     "then\n" +
                     "  modify($p) { setName( \"Toshiya\" ) };" +
                     "end\n";

        AnalysisModel analysisModel = new ModelBuilder().build(str);
        //System.out.println(analysisModel);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertNodeLink(graph, "mypkg.R1", "mypkg.R2", Link.Type.POSITIVE);
        assertNodeLink(graph, "mypkg.R2", "mypkg.R1", Link.Type.NEGATIVE);

        GraphImageGenerator generator = new GraphImageGenerator("loop");
        generator.generatePng(graph);
    }

    @Test
    public void testNoConstraint() {
        String str =
                "package mypkg;\n" +
                     "import " + Person.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "  $p : Person()\n" +
                     "then\n" +
                     "  modify($p) { setAge( 18 ) };" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $p : Person()\n" +
                     "then\n" +
                     "end\n";

        AnalysisModel analysisModel = new ModelBuilder().build(str);
        //System.out.println(analysisModel);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertNoNodeLink(graph, "mypkg.R1", "mypkg.R2");

        GraphImageGenerator generator = new GraphImageGenerator("no-constraint");
        generator.generatePng(graph);
    }

    @Test
    public void testBlackBoxMethod() {
        String str =
                "package mypkg;\n" +
                     "import " + Person.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "  $p : Person(name == \"Mario\")\n" +
                     "then\n" +
                     "  modify($p) { setAge( 18 ) };" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $p : Person(blackBoxMethod())\n" +
                     "then\n" +
                     "end\n";

        AnalysisModel analysisModel = new ModelBuilder().build(str);
        //System.out.println(analysisModel);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertNodeLink(graph, "mypkg.R1", "mypkg.R2", Link.Type.UNKNOWN);

        GraphImageGenerator generator = new GraphImageGenerator("blackBox");
        generator.generatePng(graph);
    }
}

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.POSITIVE);
        assertLink(graph, "mypkg.R1", "mypkg.R3", ReactivityType.POSITIVE);
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

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.POSITIVE);
        assertLink(graph, "mypkg.R1", "mypkg.R3", ReactivityType.NEGATIVE);
        assertLink(graph, "mypkg.R2", "mypkg.R4", ReactivityType.POSITIVE);
        assertLink(graph, "mypkg.R3", "mypkg.R4", ReactivityType.POSITIVE);

        ModelToGraphConverter converterPositiveOnly = new ModelToGraphConverter(true);
        Graph graph2 = converterPositiveOnly.toGraph(analysisModel);

        assertLink(graph2, "mypkg.R1", "mypkg.R2", ReactivityType.POSITIVE);
        assertLink(graph2, "mypkg.R1", "mypkg.R3");
        assertLink(graph2, "mypkg.R2", "mypkg.R4", ReactivityType.POSITIVE);
        assertLink(graph2, "mypkg.R3", "mypkg.R4", ReactivityType.POSITIVE);
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

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.UNKNOWN);
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

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.POSITIVE);
        assertLink(graph, "mypkg.R2", "mypkg.R1", ReactivityType.NEGATIVE);
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

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2");
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

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.UNKNOWN);
    }

    @Test
    public void testInsertDelete() {
        String str =
                "package mypkg;\n" +
                     "import " + Person.class.getCanonicalName() + ";" +
                     "import " + Address.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "  $p : Person()\n" +
                     "then\n" +
                     "  insert(new Address());" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $p : Person()\n" +
                     "  $a : Address()\n" +
                     "then\n" +
                     "  delete($p);" +
                     "end\n";

        AnalysisModel analysisModel = new ModelBuilder().build(str);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.POSITIVE);
        assertLink(graph, "mypkg.R2", "mypkg.R1", ReactivityType.NEGATIVE);
        assertLink(graph, "mypkg.R2", "mypkg.R2", ReactivityType.NEGATIVE);
    }

    @Test
    public void testInsertRelation() {
        String str =
                "package mypkg;\n" +
                     "import " + Person.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "  String(this == \"Start\")\n" +
                     "then\n" +
                     "  Person p = new Person();\n" +
                     "  p.setName(\"John\");\n" +
                     "  insert(p);\n" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $p : Person(name == \"John\")\n" +
                     "then\n" +
                     "end\n" +
                     "rule R3 when\n" +
                     "  $p : Person(name == \"Paul\")\n" +
                     "then\n" +
                     "end\n";

        AnalysisModel analysisModel = new ModelBuilder().build(str);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.POSITIVE);
        assertLink(graph, "mypkg.R1", "mypkg.R3");
    }

    @Test
    public void testInsertRelationNot() {
        String str =
                "package mypkg;\n" +
                     "import " + Person.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "  String(this == \"Start\")\n" +
                     "then\n" +
                     "  Person p = new Person();\n" +
                     "  p.setName(\"John\");\n" +
                     "  insert(p);\n" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  not( Person(name == \"John\") )\n" +
                     "then\n" +
                     "end\n" +
                     "rule R3 when\n" +
                     "  not( Person(name == \"Paul\") )\n" +
                     "then\n" +
                     "end\n";

        AnalysisModel analysisModel = new ModelBuilder().build(str);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.NEGATIVE);
        assertLink(graph, "mypkg.R1", "mypkg.R3");
    }
}

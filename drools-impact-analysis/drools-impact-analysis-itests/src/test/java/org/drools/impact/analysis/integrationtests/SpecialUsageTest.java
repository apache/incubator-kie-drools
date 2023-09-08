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
import org.drools.impact.analysis.integrationtests.domain.ControlFact;
import org.drools.impact.analysis.integrationtests.domain.FunctionUtils;
import org.drools.impact.analysis.integrationtests.domain.Person;
import org.drools.impact.analysis.integrationtests.domain.ProductItem;
import org.drools.impact.analysis.model.AnalysisModel;
import org.drools.impact.analysis.parser.ModelBuilder;
import org.junit.Test;

/**
 * 
 * Not very common way of writing but used in real rules
 *
 */
public class SpecialUsageTest extends AbstractGraphTest {

    @Test
    public void testModifyMap() {
        String str =
                "package mypkg;\n" +
                     "import " + ControlFact.class.getCanonicalName() + ";" +
                     "dialect \"mvel\"" +
                     "rule R1 when\n" +
                     "  $c : ControlFact()\n" +
                     "then\n" +
                     "  $c.mapData[\"Key1\"] = \"Value1\";" +
                     "  modify ($c) {mapData = $c.mapData};" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $c : ControlFact(mapData[\"Key1\"] == \"Value1\")\n" +
                     "then\n" +
                     "end\n" +
                     "rule R3 when\n" +
                     "  $c : ControlFact(mapData[\"Key1\"] != \"Value1\")\n" +
                     "then\n" +
                     "end\n" +
                     "rule R4 when\n" +
                     "  $c : ControlFact(mapData[\"Key2\"] == \"Value1\")\n" +
                     "then\n" +
                     "end\n" +
                     "rule R5 when\n" +
                     "  $c : ControlFact(mapData[\"Key1\"] == \"Value2\")\n" +
                     "then\n" +
                     "end\n";

        // runRule(str, new ControlFact());

        AnalysisModel analysisModel = new ModelBuilder().build(str);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.POSITIVE);
        assertLink(graph, "mypkg.R1", "mypkg.R3", ReactivityType.NEGATIVE);
        assertLink(graph, "mypkg.R1", "mypkg.R4", ReactivityType.UNKNOWN);
        assertLink(graph, "mypkg.R1", "mypkg.R5", ReactivityType.NEGATIVE);
    }

    @Test
    public void testModifyMapInt() {
        String str =
                "package mypkg;\n" +
                     "import " + ControlFact.class.getCanonicalName() + ";" +
                     "dialect \"mvel\"" +
                     "rule R1 when\n" +
                     "  $c : ControlFact()\n" +
                     "then\n" +
                     "  $c.mapDataInt[\"Key1\"] = 100;" +
                     "  modify ($c) {mapDataInt = $c.mapDataInt};" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $c : ControlFact(mapDataInt[\"Key1\"] == 100)\n" +
                     "then\n" +
                     "end\n" +
                     "rule R3 when\n" +
                     "  $c : ControlFact(mapDataInt[\"Key1\"] != 100)\n" +
                     "then\n" +
                     "end\n" +
                     "rule R4 when\n" +
                     "  $c : ControlFact(mapDataInt[\"Key2\"] == 100)\n" +
                     "then\n" +
                     "end\n" +
                     "rule R5 when\n" +
                     "  $c : ControlFact(mapDataInt[\"Key1\"] == 200)\n" +
                     "then\n" +
                     "end\n";

        // runRule(str, new ControlFact());

        AnalysisModel analysisModel = new ModelBuilder().build(str);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.POSITIVE);
        assertLink(graph, "mypkg.R1", "mypkg.R3", ReactivityType.NEGATIVE);
        assertLink(graph, "mypkg.R1", "mypkg.R4", ReactivityType.UNKNOWN);
        assertLink(graph, "mypkg.R1", "mypkg.R5", ReactivityType.NEGATIVE);
    }

    @Test
    public void testInsertWithValue() {
        String str =
                "package mypkg;\n" +
                     "import " + ControlFact.class.getCanonicalName() + ";" +
                     "dialect \"mvel\"" +
                     "rule R1 when\n" +
                     "  $c : ControlFact(keyword == \"ABC\", $orderId : orderId)\n" +
                     "then\n" +
                     "  ControlFact $newFact = new ControlFact();" +
                     "  $newFact.keyword = \"DEF\";" +
                     "  $newFact.orderId = $orderId;" +
                     "  insert($newFact);" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $c : ControlFact(keyword == \"DEF\")\n" +
                     "then\n" +
                     "end\n";

        // runRule(str, new ControlFact("123", "ABC"));

        AnalysisModel analysisModel = new ModelBuilder().build(str);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.POSITIVE);
    }

    @Test
    public void testExistsNot() {
        String str =
                "package mypkg;\n" +
                     "import " + ControlFact.class.getCanonicalName() + ";" +
                     "dialect \"mvel\"" +
                     "rule R1 when\n" +
                     "  $c : ControlFact(keyword == \"ABC\", $orderId : orderId)\n" +
                     "then\n" +
                     "  ControlFact $newFact = new ControlFact();" +
                     "  $newFact.keyword = \"DEF\";" +
                     "  $newFact.orderId = $orderId;" +
                     "  insert($newFact);" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $c : ControlFact(keyword == \"ABC\")\n" +
                     "  exists(not ControlFact(keyword == \"DEF\", $orderId : orderId))\n" +
                     "then\n" +
                     "end\n";

        // runRule(str, new ControlFact("123", "ABC"));

        AnalysisModel analysisModel = new ModelBuilder().build(str);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.NEGATIVE, ReactivityType.UNKNOWN); // we may not need to have UNKNOWN
    }

    @Test
    public void testMapWithFunction() {
        String str =
                "package mypkg;\n" +
                     "import " + ControlFact.class.getCanonicalName() + ";" +
                     "import " + ProductItem.class.getCanonicalName() + ";" +
                     "import static " + FunctionUtils.class.getCanonicalName() + ".convertMapToBigDecimal;" +
                     "dialect \"mvel\"" +
                     "rule R1 when\n" +
                     "  $c : ControlFact()\n" +
                     "then\n" +
                     "  $c.mapData[\"Price\"] = \"100.0\";" +
                     "  modify ($c) {mapData = $c.mapData};" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  ControlFact($price : convertMapToBigDecimal(mapData, \"Price\"))\n" +
                     "  ProductItem(price == $price)\n" +
                     "then\n" +
                     "end\n";

        // runRule(str, new ControlFact(), new ProductItem("Product1", new BigDecimal("100.0")));

        AnalysisModel analysisModel = new ModelBuilder().build(str);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.UNKNOWN);
    }

    @Test
    public void testBindVariableLiteralModify() {
        String str =
                "package mypkg;\n" +
                     "import " + Person.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "  $p : Person($data : \"ABC\")\n" +
                     "then\n" +
                     "  modify($p) { setLikes( $data ) };" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $p : Person(likes == \"ABC\")\n" +
                     "then\n" +
                     "end\n";

        AnalysisModel analysisModel = new ModelBuilder().build(str);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.POSITIVE);
    }

    @Test
    public void testBindVariableLiteralInsert() {
        String str =
                "package mypkg;\n" +
                     "import " + Address.class.getCanonicalName() + ";" +
                     "import " + Person.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "  Address(city == \"Tokyo\", $data : \"ABC\")\n" +
                     "then\n" +
                     "  Person person = new Person(\"John\");\n" +
                     "  person.setLikes($data);\n" +
                     "  insert(person);\n" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $p : Person(likes == \"ABC\")\n" +
                     "then\n" +
                     "end\n";

        AnalysisModel analysisModel = new ModelBuilder().build(str);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.POSITIVE);
    }

    @Test
    public void testBindVariableLiteralModifyMap() {
        String str =
                "package mypkg;\n" +
                     "import " + ControlFact.class.getCanonicalName() + ";" +
                     "dialect \"mvel\"" +
                     "rule R1\n" +
                     "when\n" +
                     "  $c : ControlFact( $value : \"Value1\" )\n" +
                     "then\n" +
                     "  $c.mapData[\"Key1\"] = $value;\n" +
                     "  modify ($c) {mapData = $c.mapData};\n" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $c : ControlFact(mapData[\"Key1\"] == \"Value1\")\n" +
                     "then\n" +
                     "end";

        AnalysisModel analysisModel = new ModelBuilder().build(str);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.POSITIVE);
    }
}

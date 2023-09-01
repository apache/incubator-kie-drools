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

import java.util.List;

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
 * Tests related to DRL RHS
 *
 */
public class RhsTest extends AbstractGraphTest {

    @Test
    public void testForEachInsert() {
        String str =
                "package mypkg;\n" +
                     "import " + Person.class.getCanonicalName() + ";" +
                     "import " + Address.class.getCanonicalName() + ";" +
                     "rule R1\n" +
                     "  when\n" +
                     "    $person : Person()\n" +
                     "  then\n" +
                     "    for(Address $address : $person.getAddresses()){\n" +
                     "      insert($address);\n" +
                     "    }\n" +
                     "end\n" +
                     "rule R2\n" +
                     "  when\n" +
                     "    Address()\n" +
                     "  then\n" +
                     "end\n";

        AnalysisModel analysisModel = new ModelBuilder().build(str);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.POSITIVE);
    }

    @Test
    public void testForEachDelete() {
        String str =
                "package mypkg;\n" +
                     "import " + List.class.getCanonicalName() + ";" +
                     "rule R1\n" +
                     "  when\n" +
                     "    $objectList : List() from collect(Object())\n" +
                     "  then\n" +
                     "    for(Object $object : $objectList){\n" +
                     "      delete($object);\n" +
                     "    }\n" +
                     "end";

        AnalysisModel analysisModel = new ModelBuilder().build(str);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R1"); // at the moment, R1's patternClass is parsed as `List`. It will be addressed in DROOLS-6616
    }
}

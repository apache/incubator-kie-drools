/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
import org.drools.impact.analysis.model.right.ConsequenceAction;
import org.drools.impact.analysis.model.right.DeleteSpecificFactAction;
import org.drools.impact.analysis.model.right.SpecificProperty;
import org.drools.impact.analysis.parser.ModelBuilder;
import org.junit.Test;

/**
 * This test is to verify that DeleteSpecificFactAction can be handled correctly.
 * DeleteSpecificFactAction cannot be created by ModelBuilder from DRL, so we programmatically add it to the model.
 */
public class DeleteSpecificFactActionTest extends AbstractGraphTest {

    @Test
    public void insertDeleteSpecific() {
        String str =
                "package mypkg;\n" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Address.class.getCanonicalName() + ";" +
                        "rule R1 when\n" +
                        "  $p : Person(name == \"John\")\n" +
                        "then\n" +
                        "  Address address = new Address();" +
                        "  address.setStreet(\"ABC\");" +
                        "  insert(address);" +
                        "end\n" +
                        "rule R2 when\n" +
                        "  $p : Person(name == \"Paul\")\n" +
                        "then\n" +
                        // Here, delete a fact with street == "ABC" (ansible-rulebook can do it)
                        "end\n" +
                        "rule R3 when\n" +
                        "  $a : Address(street == \"ABC\")\n" +
                        "then\n" +
                        "end\n";

        AnalysisModel analysisModel = new ModelBuilder().build(str);

        // Tweak analysisModel because DeleteSpecificFactAction cannot be created by ModelBuilder
        List<ConsequenceAction> actions = analysisModel.getPackages().get(0).getRules().get(1).getRhs().getActions();
        DeleteSpecificFactAction deleteSpecificFactAction = new DeleteSpecificFactAction(Address.class);
        deleteSpecificFactAction.addSpecificProperty(new SpecificProperty("street", "ABC"));
        actions.add(deleteSpecificFactAction);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R3", ReactivityType.POSITIVE);
        assertLink(graph, "mypkg.R2", "mypkg.R3", ReactivityType.NEGATIVE);
    }
}

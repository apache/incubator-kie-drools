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

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.drools.impact.analysis.graph.Graph;
import org.drools.impact.analysis.graph.ImpactAnalysisHelper;
import org.drools.impact.analysis.graph.ModelToGraphConverter;
import org.drools.impact.analysis.graph.Node;
import org.drools.impact.analysis.graph.Node.Status;
import org.drools.impact.analysis.graph.TextReporter;
import org.drools.impact.analysis.integrationtests.domain.Order;
import org.drools.impact.analysis.model.AnalysisModel;
import org.drools.impact.analysis.parser.ModelBuilder;
import org.junit.Test;

import static org.drools.impact.analysis.graph.TextReporter.INDENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ImpactAnalysisTest extends AbstractGraphTest {

    @Test
    public void testOrderRules() {
        String str =
                "package mypkg;\n" +
                     "import " + Order.class.getCanonicalName() + ";" +
                     "\n" +
                     "rule R1\n" +
                     "  when\n" +
                     "    $o : Order(customerMembershipRank > 5)\n" +
                     "  then\n" +
                     "    modify($o) {\n" +
                     "      setDiscount(1000);\n" +
                     "    }\n" +
                     "end\n" +
                     "\n" +
                     "rule R2\n" +
                     "  when\n" +
                     "    $o : Order(customerAge > 60)\n" +
                     "  then\n" +
                     "    modify($o) {\n" +
                     "      setDiscount(2000);\n" +
                     "    }\n" +
                     "end\n" +
                     "\n" +
                     "rule R3\n" +
                     "  when\n" +
                     "    $o : Order(itemPrice < 2000, discount >= 2000)\n" +
                     "  then\n" +
                     "    modify($o) {\n" +
                     "      setStatus(\"Too much discount\");\n" +
                     "    }\n" +
                     "end\n" +
                     "\n" +
                     "rule R4\n" +
                     "  when\n" +
                     "    $o : Order(itemPrice > 5000)\n" +
                     "  then\n" +
                     "    modify($o) {\n" +
                     "      setStatus(\"Exclusive order\");\n" +
                     "    }\n" +
                     "end\n" +
                     "\n" +
                     "rule R5\n" +
                     "  when\n" +
                     "    $o : Order(status == \"Too much discount\")\n" +
                     "  then\n" +
                     "    modify($o) {\n" +
                     "      setDiscount(500);\n" +
                     "    }\n" +
                     "end\n" +
                     "\n" +
                     "rule R6\n" +
                     "  when\n" +
                     "    Order(status == \"Exclusive order\")\n" +
                     "  then\n" +
                     "    // Do some work...\n" +
                     "end";

        AnalysisModel analysisModel = new ModelBuilder().build(str);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        generatePng(graph, "_all");

        // Assuming that "modify" action in R2 is changed 
        Node changedNode = graph.getNodeMap().get("mypkg.R2"); // modify action in R2

        ImpactAnalysisHelper impactFilter = new ImpactAnalysisHelper();
        Graph impactedSubGraph = impactFilter.filterImpactedNodes(graph, changedNode);

        generatePng(impactedSubGraph, "_impactedSubGraph");

        generatePng(graph, "_impacted");

        assertNull(impactedSubGraph.getNodeMap().get("mypkg.R1"));
        assertEquals(Status.CHANGED, impactedSubGraph.getNodeMap().get("mypkg.R2").getStatus());
        assertEquals(Status.IMPACTED, impactedSubGraph.getNodeMap().get("mypkg.R3").getStatus());
        assertNull(impactedSubGraph.getNodeMap().get("mypkg.R4"));
        assertEquals(Status.IMPACTED, impactedSubGraph.getNodeMap().get("mypkg.R5").getStatus());
        assertEquals(Status.IMPACTED, impactedSubGraph.getNodeMap().get("mypkg.R6").getStatus());

        // TextReporter test

        System.out.println("--- toHierarchyText ---");
        String hierarchyText = TextReporter.toHierarchyText(impactedSubGraph);
        System.out.println(hierarchyText);
        List<String> lines = Arrays.asList(hierarchyText.split(System.lineSeparator()));
        Assertions.assertThat(lines).containsExactlyInAnyOrder("R2[*]",
                                                               INDENT + "R3[+]",
                                                               INDENT + INDENT + "R6[+]",
                                                               INDENT + INDENT + "R5[+]",
                                                               INDENT + INDENT + INDENT + "(R3)");

        System.out.println("--- toFlatText ---");
        String flatText = TextReporter.toFlatText(impactedSubGraph);
        System.out.println(flatText);
        List<String> lines2 = Arrays.asList(flatText.split(System.lineSeparator()));
        Assertions.assertThat(lines2).containsExactlyInAnyOrder("R2[*]",
                                                                "R3[+]",
                                                                "R6[+]",
                                                                "R5[+]");
    }
}

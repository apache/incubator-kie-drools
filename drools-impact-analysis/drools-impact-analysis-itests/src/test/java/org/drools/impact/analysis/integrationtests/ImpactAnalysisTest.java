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

import java.util.Arrays;
import java.util.List;

import org.drools.impact.analysis.graph.Graph;
import org.drools.impact.analysis.graph.ImpactAnalysisHelper;
import org.drools.impact.analysis.graph.ModelToGraphConverter;
import org.drools.impact.analysis.graph.Node.Status;
import org.drools.impact.analysis.graph.TextReporter;
import org.drools.impact.analysis.integrationtests.domain.Order;
import org.drools.impact.analysis.model.AnalysisModel;
import org.drools.impact.analysis.parser.ModelBuilder;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.impact.analysis.graph.TextReporter.INDENT;

public class ImpactAnalysisTest extends AbstractGraphTest {

    private static final String ORDER_RULES = "package mypkg;\n" +
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

    @Test
    public void testOrderRules() {
        AnalysisModel analysisModel = new ModelBuilder().build(ORDER_RULES);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        // View rules which are impacted by R2
        ImpactAnalysisHelper impactFilter = new ImpactAnalysisHelper();
        Graph impactedSubGraph = impactFilter.filterImpactedNodes(graph, "mypkg.R2");

        assertThat(impactedSubGraph.getNodeMap().get("mypkg.R1")).isNull();
        assertThat(impactedSubGraph.getNodeMap().get("mypkg.R2").getStatus()).isEqualTo(Status.CHANGED);
        assertThat(impactedSubGraph.getNodeMap().get("mypkg.R3").getStatus()).isEqualTo(Status.IMPACTED);
        assertThat(impactedSubGraph.getNodeMap().get("mypkg.R4")).isNull();
        assertThat(impactedSubGraph.getNodeMap().get("mypkg.R5").getStatus()).isEqualTo(Status.IMPACTED);
        assertThat(impactedSubGraph.getNodeMap().get("mypkg.R6").getStatus()).isEqualTo(Status.IMPACTED);

        // TextReporter test
        String hierarchyText = TextReporter.toHierarchyText(impactedSubGraph);
        List<String> lines = Arrays.asList(hierarchyText.split(System.lineSeparator()));
        assertThat(lines).containsExactlyInAnyOrder("R2[*]",
                                                               INDENT + "R3[+]",
                                                               INDENT + INDENT + "R6[+]",
                                                               INDENT + INDENT + "R5[+]",
                                                               INDENT + INDENT + INDENT + "(R3)");

        String flatText = TextReporter.toFlatText(impactedSubGraph);
        List<String> lines2 = Arrays.asList(flatText.split(System.lineSeparator()));
        assertThat(lines2).containsExactlyInAnyOrder("R2[*]",
                                                                "R3[+]",
                                                                "R6[+]",
                                                                "R5[+]");
    }

    @Test
    public void testOrderRulesBackward() {
        AnalysisModel analysisModel = new ModelBuilder().build(ORDER_RULES);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        // View rules which impact R5
        ImpactAnalysisHelper impactFilter = new ImpactAnalysisHelper();
        Graph impactingSubGraph = impactFilter.filterImpactingNodes(graph, "mypkg.R5");

        assertThat(impactingSubGraph.getNodeMap().get("mypkg.R1").getStatus()).isEqualTo(Status.IMPACTING);
        assertThat(impactingSubGraph.getNodeMap().get("mypkg.R2").getStatus()).isEqualTo(Status.IMPACTING);
        assertThat(impactingSubGraph.getNodeMap().get("mypkg.R3").getStatus()).isEqualTo(Status.IMPACTING);
        assertThat(impactingSubGraph.getNodeMap().get("mypkg.R4").getStatus()).isEqualTo(Status.IMPACTING);
        assertThat(impactingSubGraph.getNodeMap().get("mypkg.R5").getStatus()).isEqualTo(Status.TARGET);
        assertThat(impactingSubGraph.getNodeMap().get("mypkg.R6")).isNull();

        // TextReporter test
        String hierarchyText = TextReporter.toHierarchyText(impactingSubGraph);
        List<String> lines = Arrays.asList(hierarchyText.split(System.lineSeparator()));
        assertThat(lines).containsExactlyInAnyOrder("R1[!]",
                                                               INDENT + "R3[!]",
                                                               INDENT + INDENT + "R5[@]",
                                                               INDENT + INDENT + INDENT + "(R3)",
                                                               "R2[!]",
                                                               INDENT + "(R3)",
                                                               "R4[!]",
                                                               INDENT + "(R5)");

        String flatText = TextReporter.toFlatText(impactingSubGraph);
        List<String> lines2 = Arrays.asList(flatText.split(System.lineSeparator()));
        assertThat(lines2).containsExactlyInAnyOrder("R1[!]",
                                                                "R2[!]",
                                                                "R3[!]",
                                                                "R4[!]",
                                                                "R5[@]");
    }
}

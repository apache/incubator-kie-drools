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
import org.drools.impact.analysis.graph.GraphCollapsionHelper;
import org.drools.impact.analysis.graph.ImpactAnalysisHelper;
import org.drools.impact.analysis.graph.LinkFilter;
import org.drools.impact.analysis.graph.ModelToGraphConverter;
import org.drools.impact.analysis.graph.ReactivityType;
import org.drools.impact.analysis.integrationtests.domain.Order;
import org.drools.impact.analysis.integrationtests.domain.Person;
import org.drools.impact.analysis.model.AnalysisModel;
import org.drools.impact.analysis.parser.ModelBuilder;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LinkFilterTest extends AbstractGraphTest {

    private static final String SIMPLE_RULE = "package mypkg;\n" +
                                              "import " + Person.class.getCanonicalName() + ";" +
                                              "rule R1 when\n" +
                                              "  $p : Person(name == \"John\")\n" +
                                              "then\n" +
                                              "  modify($p) { setAge( 18 ), setLikes(\"cheddar\"), setEmployed(true) };" +
                                              "end\n" +
                                              "rule R2 when\n" +
                                              "  $p : Person(age > 15)\n" + // POSITIVE
                                              "then\n" +
                                              "end\n" +
                                              "rule R3 when\n" +
                                              "  $p : Person(likes != \"cheddar\")\n" + // NEGATIVE
                                              "then\n" +
                                              "end\n" +
                                              "rule R4 when\n" +
                                              "  $b : Boolean()\n" +
                                              "  $p : Person(employed == $b)\n" + // UNKNOWN
                                              "then\n" +
                                              "end\n";

    private static final String RULE_WITH_PREFIX = "package mypkg;\n" +
                                                   "import " + Order.class.getCanonicalName() + ";" +
                                                   "\n" +
                                                   "rule CustomerCheck_1\n" +
                                                   "  when\n" +
                                                   "    $o : Order(customerMembershipRank > 5)\n" +
                                                   "  then\n" +
                                                   "    modify($o) {\n" +
                                                   "      setDiscount(1000);\n" +
                                                   "    }\n" +
                                                   "end\n" +
                                                   "\n" +
                                                   "rule CustomerCheck_2\n" +
                                                   "  when\n" +
                                                   "    $o : Order(customerAge > 60)\n" +
                                                   "  then\n" +
                                                   "    modify($o) {\n" +
                                                   "      setDiscount(2000);\n" +
                                                   "    }\n" +
                                                   "end\n" +
                                                   "\n" +
                                                   "rule PriceCheck_1\n" +
                                                   "  when\n" +
                                                   "    $o : Order(itemPrice < 2000, discount >= 2000)\n" +
                                                   "  then\n" +
                                                   "    modify($o) {\n" +
                                                   "      setStatus(\"Too much discount\");\n" +
                                                   "    }\n" +
                                                   "end\n" +
                                                   "\n" +
                                                   "rule PriceCheck_2\n" +
                                                   "  when\n" +
                                                   "    $o : Order(itemPrice > 5000)\n" +
                                                   "  then\n" +
                                                   "    modify($o) {\n" +
                                                   "      setStatus(\"Exclusive order\");\n" +
                                                   "    }\n" +
                                                   "end\n" +
                                                   "\n" +
                                                   "rule StatusCheck_1\n" +
                                                   "  when\n" +
                                                   "    $o : Order(status == \"Too much discount\")\n" +
                                                   "  then\n" +
                                                   "    modify($o) {\n" +
                                                   "      setDiscount(500);\n" +
                                                   "    }\n" +
                                                   "end\n" +
                                                   "\n" +
                                                   "rule StatusCheck_2\n" +
                                                   "  when\n" +
                                                   "    $s : String()\n" +
                                                   "    Order(status == $s)\n" +
                                                   "  then\n" +
                                                   "    // Do some work...\n" +
                                                   "end";

    @Test
    public void testModelToGraphConverter() {

        AnalysisModel analysisModel = new ModelBuilder().build(SIMPLE_RULE);

        ModelToGraphConverter converter = new ModelToGraphConverter(LinkFilter.POSITIVE);
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.POSITIVE);
        assertLink(graph, "mypkg.R1", "mypkg.R3");
        assertLink(graph, "mypkg.R1", "mypkg.R4");

        converter = new ModelToGraphConverter(LinkFilter.NEGATIVE);
        graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2");
        assertLink(graph, "mypkg.R1", "mypkg.R3", ReactivityType.NEGATIVE);
        assertLink(graph, "mypkg.R1", "mypkg.R4");

        converter = new ModelToGraphConverter(LinkFilter.UNKNOWN);
        graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2");
        assertLink(graph, "mypkg.R1", "mypkg.R3");
        assertLink(graph, "mypkg.R1", "mypkg.R4", ReactivityType.UNKNOWN);

        converter = new ModelToGraphConverter(LinkFilter.POSITIVE_NEGATIVE);
        graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.POSITIVE);
        assertLink(graph, "mypkg.R1", "mypkg.R3", ReactivityType.NEGATIVE);
        assertLink(graph, "mypkg.R1", "mypkg.R4");

        converter = new ModelToGraphConverter(LinkFilter.POSITIVE_UNKNOWN);
        graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.POSITIVE);
        assertLink(graph, "mypkg.R1", "mypkg.R3");
        assertLink(graph, "mypkg.R1", "mypkg.R4", ReactivityType.UNKNOWN);

        converter = new ModelToGraphConverter(LinkFilter.NEGATIVE_UNKNOWN);
        graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2");
        assertLink(graph, "mypkg.R1", "mypkg.R3", ReactivityType.NEGATIVE);
        assertLink(graph, "mypkg.R1", "mypkg.R4", ReactivityType.UNKNOWN);

        converter = new ModelToGraphConverter(LinkFilter.ALL);
        graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R2", ReactivityType.POSITIVE);
        assertLink(graph, "mypkg.R1", "mypkg.R3", ReactivityType.NEGATIVE);
        assertLink(graph, "mypkg.R1", "mypkg.R4", ReactivityType.UNKNOWN);
    }

    @Test
    public void testImpactAnalysisHelper() {
        // ImpactAnalysisHelper simply returns a sub map which contains only a changed node and impacted nodes
        // So we can assertNull for non-impacted nodes. (Links are not modified)

        AnalysisModel analysisModel = new ModelBuilder().build(SIMPLE_RULE);

        ModelToGraphConverter converter = new ModelToGraphConverter(); // default ALL
        Graph graph = converter.toGraph(analysisModel);

        ImpactAnalysisHelper impactFilter = new ImpactAnalysisHelper(LinkFilter.POSITIVE);
        Graph impactedSubGraph = impactFilter.filterImpactedNodes(graph, "mypkg.R1");

        assertLink(impactedSubGraph, "mypkg.R1", "mypkg.R2", ReactivityType.POSITIVE);
        assertThat(impactedSubGraph.getNodeMap().get("mypkg.R3")).isNull();
        assertThat(impactedSubGraph.getNodeMap().get("mypkg.R4")).isNull();

        graph = converter.toGraph(analysisModel);
        impactFilter = new ImpactAnalysisHelper(LinkFilter.NEGATIVE);
        impactedSubGraph = impactFilter.filterImpactedNodes(graph, "mypkg.R1");

        assertThat(impactedSubGraph.getNodeMap().get("mypkg.R2")).isNull();
        assertLink(impactedSubGraph, "mypkg.R1", "mypkg.R3", ReactivityType.NEGATIVE);
        assertThat(impactedSubGraph.getNodeMap().get("mypkg.R4")).isNull();

        graph = converter.toGraph(analysisModel);
        impactFilter = new ImpactAnalysisHelper(LinkFilter.UNKNOWN);
        impactedSubGraph = impactFilter.filterImpactedNodes(graph, "mypkg.R1");

        assertThat(impactedSubGraph.getNodeMap().get("mypkg.R2")).isNull();
        assertThat(impactedSubGraph.getNodeMap().get("mypkg.R3")).isNull();
        assertLink(impactedSubGraph, "mypkg.R1", "mypkg.R4", ReactivityType.UNKNOWN);

        graph = converter.toGraph(analysisModel);
        impactFilter = new ImpactAnalysisHelper(LinkFilter.POSITIVE_NEGATIVE);
        impactedSubGraph = impactFilter.filterImpactedNodes(graph, "mypkg.R1");

        assertLink(impactedSubGraph, "mypkg.R1", "mypkg.R2", ReactivityType.POSITIVE);
        assertLink(impactedSubGraph, "mypkg.R1", "mypkg.R3", ReactivityType.NEGATIVE);
        assertThat(impactedSubGraph.getNodeMap().get("mypkg.R4")).isNull();

        graph = converter.toGraph(analysisModel);
        impactFilter = new ImpactAnalysisHelper(LinkFilter.POSITIVE_UNKNOWN);
        impactedSubGraph = impactFilter.filterImpactedNodes(graph, "mypkg.R1");

        assertLink(impactedSubGraph, "mypkg.R1", "mypkg.R2", ReactivityType.POSITIVE);
        assertThat(impactedSubGraph.getNodeMap().get("mypkg.R3")).isNull();
        assertLink(impactedSubGraph, "mypkg.R1", "mypkg.R4", ReactivityType.UNKNOWN);

        graph = converter.toGraph(analysisModel);
        impactFilter = new ImpactAnalysisHelper(LinkFilter.NEGATIVE_UNKNOWN);
        impactedSubGraph = impactFilter.filterImpactedNodes(graph, "mypkg.R1");

        assertThat(impactedSubGraph.getNodeMap().get("mypkg.R2")).isNull();
        assertLink(impactedSubGraph, "mypkg.R1", "mypkg.R3", ReactivityType.NEGATIVE);
        assertLink(impactedSubGraph, "mypkg.R1", "mypkg.R4", ReactivityType.UNKNOWN);

        graph = converter.toGraph(analysisModel);
        impactFilter = new ImpactAnalysisHelper(LinkFilter.ALL);
        impactedSubGraph = impactFilter.filterImpactedNodes(graph, "mypkg.R1");

        assertLink(impactedSubGraph, "mypkg.R1", "mypkg.R2", ReactivityType.POSITIVE);
        assertLink(impactedSubGraph, "mypkg.R1", "mypkg.R3", ReactivityType.NEGATIVE);
        assertLink(impactedSubGraph, "mypkg.R1", "mypkg.R4", ReactivityType.UNKNOWN);
    }

    @Test
    public void testGraphCollapsionHelper() {

        // GraphCollapsionHelper creates a map with new nodes and links

        AnalysisModel analysisModel = new ModelBuilder().build(RULE_WITH_PREFIX); // default ALL

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        GraphCollapsionHelper collapsionHelper = new GraphCollapsionHelper(LinkFilter.POSITIVE);
        Graph collapsedGraph = collapsionHelper.collapseWithRuleNamePrefix(graph);

        assertLink(collapsedGraph, "mypkg.CustomerCheck", "mypkg.PriceCheck", ReactivityType.POSITIVE);
        assertLink(collapsedGraph, "mypkg.PriceCheck", "mypkg.StatusCheck", ReactivityType.POSITIVE);
        assertLink(collapsedGraph, "mypkg.StatusCheck", "mypkg.PriceCheck");

        collapsionHelper = new GraphCollapsionHelper(LinkFilter.NEGATIVE);
        collapsedGraph = collapsionHelper.collapseWithRuleNamePrefix(graph);

        assertLink(collapsedGraph, "mypkg.CustomerCheck", "mypkg.PriceCheck", ReactivityType.NEGATIVE);
        assertLink(collapsedGraph, "mypkg.PriceCheck", "mypkg.StatusCheck", ReactivityType.NEGATIVE);
        assertLink(collapsedGraph, "mypkg.StatusCheck", "mypkg.PriceCheck", ReactivityType.NEGATIVE);

        collapsionHelper = new GraphCollapsionHelper(LinkFilter.UNKNOWN);
        collapsedGraph = collapsionHelper.collapseWithRuleNamePrefix(graph);

        assertLink(collapsedGraph, "mypkg.CustomerCheck", "mypkg.PriceCheck");
        assertLink(collapsedGraph, "mypkg.PriceCheck", "mypkg.StatusCheck", ReactivityType.UNKNOWN);
        assertLink(collapsedGraph, "mypkg.StatusCheck", "mypkg.PriceCheck");

        collapsionHelper = new GraphCollapsionHelper(LinkFilter.POSITIVE_NEGATIVE);
        collapsedGraph = collapsionHelper.collapseWithRuleNamePrefix(graph);

        assertLink(collapsedGraph, "mypkg.CustomerCheck", "mypkg.PriceCheck", ReactivityType.POSITIVE, ReactivityType.NEGATIVE);
        assertLink(collapsedGraph, "mypkg.PriceCheck", "mypkg.StatusCheck", ReactivityType.POSITIVE, ReactivityType.NEGATIVE);
        assertLink(collapsedGraph, "mypkg.StatusCheck", "mypkg.PriceCheck", ReactivityType.NEGATIVE);

        collapsionHelper = new GraphCollapsionHelper(LinkFilter.POSITIVE_UNKNOWN);
        collapsedGraph = collapsionHelper.collapseWithRuleNamePrefix(graph);

        assertLink(collapsedGraph, "mypkg.CustomerCheck", "mypkg.PriceCheck", ReactivityType.POSITIVE);
        assertLink(collapsedGraph, "mypkg.PriceCheck", "mypkg.StatusCheck", ReactivityType.POSITIVE, ReactivityType.UNKNOWN);
        assertLink(collapsedGraph, "mypkg.StatusCheck", "mypkg.PriceCheck");

        collapsionHelper = new GraphCollapsionHelper(LinkFilter.NEGATIVE_UNKNOWN);
        collapsedGraph = collapsionHelper.collapseWithRuleNamePrefix(graph);

        assertLink(collapsedGraph, "mypkg.CustomerCheck", "mypkg.PriceCheck", ReactivityType.NEGATIVE);
        assertLink(collapsedGraph, "mypkg.PriceCheck", "mypkg.StatusCheck", ReactivityType.NEGATIVE, ReactivityType.UNKNOWN);
        assertLink(collapsedGraph, "mypkg.StatusCheck", "mypkg.PriceCheck", ReactivityType.NEGATIVE);

        collapsionHelper = new GraphCollapsionHelper(LinkFilter.ALL);
        collapsedGraph = collapsionHelper.collapseWithRuleNamePrefix(graph);

        assertLink(collapsedGraph, "mypkg.CustomerCheck", "mypkg.PriceCheck", ReactivityType.POSITIVE, ReactivityType.NEGATIVE);
        assertLink(collapsedGraph, "mypkg.PriceCheck", "mypkg.StatusCheck", ReactivityType.POSITIVE, ReactivityType.NEGATIVE, ReactivityType.UNKNOWN);
        assertLink(collapsedGraph, "mypkg.StatusCheck", "mypkg.PriceCheck", ReactivityType.NEGATIVE);
    }
}

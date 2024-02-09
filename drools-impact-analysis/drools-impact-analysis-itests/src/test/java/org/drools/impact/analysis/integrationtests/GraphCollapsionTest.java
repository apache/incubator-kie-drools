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

import java.io.IOException;

import org.drools.impact.analysis.graph.Graph;
import org.drools.impact.analysis.graph.GraphCollapsionHelper;
import org.drools.impact.analysis.graph.ImpactAnalysisHelper;
import org.drools.impact.analysis.graph.ModelToGraphConverter;
import org.drools.impact.analysis.graph.Node;
import org.drools.impact.analysis.graph.Node.Status;
import org.drools.impact.analysis.graph.ReactivityType;
import org.drools.impact.analysis.integrationtests.domain.Order;
import org.drools.impact.analysis.model.AnalysisModel;
import org.drools.impact.analysis.parser.ModelBuilder;
import org.drools.impact.analysis.parser.internal.ImpactAnalysisKieModule;
import org.drools.impact.analysis.parser.internal.ImpactAnalysisProject;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;

import static org.assertj.core.api.Assertions.assertThat;

public class GraphCollapsionTest extends AbstractGraphTest {

    @Test
    public void testDrlRuleNamePrefix() {
        String str =
                "package mypkg;\n" +
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
                     "    Order(status == \"Exclusive order\")\n" +
                     "  then\n" +
                     "    // Do some work...\n" +
                     "end";

        AnalysisModel analysisModel = new ModelBuilder().build(str);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        Graph collapsedGraph = new GraphCollapsionHelper().collapseWithRuleNamePrefix(graph);

        assertThat(collapsedGraph.getNodeMap().size()).isEqualTo(3);

        assertLink(collapsedGraph, "mypkg.CustomerCheck", "mypkg.PriceCheck", ReactivityType.POSITIVE, ReactivityType.NEGATIVE);
        assertLink(collapsedGraph, "mypkg.PriceCheck", "mypkg.StatusCheck", ReactivityType.POSITIVE, ReactivityType.NEGATIVE);
        assertLink(collapsedGraph, "mypkg.StatusCheck", "mypkg.PriceCheck", ReactivityType.NEGATIVE);

        //--- impact analysis
        // Assuming that "modify" action in PriceCheck_X is changed
        Node changedNode = collapsedGraph.getNodeMap().get("mypkg.PriceCheck"); // modify action in PriceCheck_X

        ImpactAnalysisHelper impactFilter = new ImpactAnalysisHelper();
        Graph impactedSubGraph = impactFilter.filterImpactedNodes(collapsedGraph, changedNode);

        assertThat(impactedSubGraph.getNodeMap().get("mypkg.CustomerCheck")).isNull();
        assertThat(impactedSubGraph.getNodeMap().get("mypkg.PriceCheck").getStatus()).isEqualTo(Status.CHANGED);
        assertThat(impactedSubGraph.getNodeMap().get("mypkg.StatusCheck").getStatus()).isEqualTo(Status.IMPACTED);
    }

    @Test
    public void testSpreadsheet() throws IOException {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.drools.impact.analysis.integrationtests", "spreadsheet-test", "1.0.0");
        KieFileSystem kfs = createKieFileSystemWithClassPathResourceNames(releaseId, getClass(),
                                                                          "collapsion01.drl.xls", "collapsion02.drl.xls", "collapsion03.drl.xls");

        KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll(ImpactAnalysisProject.class);
        ImpactAnalysisKieModule analysisKieModule = (ImpactAnalysisKieModule) kieBuilder.getKieModule();
        AnalysisModel analysisModel = analysisKieModule.getAnalysisModel();

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        Graph collapsedGraph = new GraphCollapsionHelper().collapseWithRuleNamePrefix(graph);

        assertThat(collapsedGraph.getNodeMap().size()).isEqualTo(3);

        assertLink(collapsedGraph, "mypkg2.CustomerCheck", "mypkg2.PriceCheck", ReactivityType.POSITIVE, ReactivityType.NEGATIVE);
        assertLink(collapsedGraph, "mypkg2.PriceCheck", "mypkg2.StatusCheck", ReactivityType.POSITIVE, ReactivityType.NEGATIVE);
        assertLink(collapsedGraph, "mypkg2.StatusCheck", "mypkg2.PriceCheck", ReactivityType.NEGATIVE);

        //--- impact analysis
        // Assuming that "modify" action in PriceCheck_X is changed
        Node changedNode = collapsedGraph.getNodeMap().get("mypkg2.PriceCheck"); // modify action in PriceCheck_X

        ImpactAnalysisHelper impactFilter = new ImpactAnalysisHelper();
        Graph impactedSubGraph = impactFilter.filterImpactedNodes(collapsedGraph, changedNode);

        assertThat(impactedSubGraph.getNodeMap().get("mypkg2.CustomerCheck")).isNull();
        assertThat(impactedSubGraph.getNodeMap().get("mypkg2.PriceCheck").getStatus()).isEqualTo(Status.CHANGED);
        assertThat(impactedSubGraph.getNodeMap().get("mypkg2.StatusCheck").getStatus()).isEqualTo(Status.IMPACTED);
    }

}

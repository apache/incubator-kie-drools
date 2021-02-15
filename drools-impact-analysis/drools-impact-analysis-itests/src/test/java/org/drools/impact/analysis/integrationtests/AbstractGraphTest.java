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

import java.util.Optional;

import org.drools.impact.analysis.graph.Graph;
import org.drools.impact.analysis.graph.Link;
import org.drools.impact.analysis.graph.Node;
import org.drools.impact.analysis.graph.ReactivityType;
import org.drools.impact.analysis.graph.graphviz.GraphImageGenerator;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AbstractGraphTest {

    @Rule
    public TestName testName = new TestName();

    protected String getTestMethodName() {
        return testName.getMethodName();
    }

    protected void generatePng(Graph graph) {
        generatePng(graph, "");
    }

    protected void generatePng(Graph graph, String suffix) {
        GraphImageGenerator generator = new GraphImageGenerator(getTestMethodName() + suffix);
        generator.generatePng(graph);
    }

    protected void assertNodeLink(Graph graph, String sourceFqdn, String targetFqdn, ReactivityType type) {
        Node source = graph.getNodeMap().get(sourceFqdn);
        Node target = graph.getNodeMap().get(targetFqdn);
        Optional<Link> optOutgoing = source.getOutgoingLinks().stream().filter(l -> l.getTarget().equals(target)).findFirst();
        if (!optOutgoing.isPresent()) {
            fail("outgoingLink doesn't exist : source = " + sourceFqdn + ", target = " + targetFqdn);
        }
        Link outgoingLink = optOutgoing.get();
        Optional<Link> optIncoming = target.getIncomingLinks().stream().filter(l -> l.getSource().equals(source)).findFirst();
        if (!optIncoming.isPresent()) {
            fail("incomingLink doesn't exist : source = " + sourceFqdn + ", target = " + targetFqdn);
        }
        Link incomingLink = optIncoming.get();
        if (outgoingLink != incomingLink) {
            fail("links are not the same : outgoingLink = " + outgoingLink + ", incomingLink = " + incomingLink);
        }

        assertEquals(type, outgoingLink.getReactivityType());
    }

    protected void assertNoNodeLink(Graph graph, String sourceFqdn, String targetFqdn) {
        Node source = graph.getNodeMap().get(sourceFqdn);
        Node target = graph.getNodeMap().get(targetFqdn);
        Optional<Link> optOutgoing = source.getOutgoingLinks().stream().filter(l -> l.getTarget().equals(target)).findFirst();
        if (optOutgoing.isPresent()) {
            fail("outgoingLink exists : source = " + sourceFqdn + ", target = " + targetFqdn);
        }
        Optional<Link> optIncoming = target.getIncomingLinks().stream().filter(l -> l.getSource().equals(source)).findFirst();
        if (optIncoming.isPresent()) {
            fail("incomingLink exists : source = " + sourceFqdn + ", target = " + targetFqdn);
        }
    }

    /*
     * Only for test development convenience (to confirm if the rule is valid)
     */
    protected void runRule(String drl, Object... facts) {
        final KieSession ksession = RuleExecutionHelper.getKieSession(drl);
        for (Object fact : facts) {
            ksession.insert(fact);
        }
        int fired = ksession.fireAllRules(100);
        System.out.println("fired = " + fired);
        ksession.dispose();
    }

}

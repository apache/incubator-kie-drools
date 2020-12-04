package org.drools.impact.analysis.integrationtests;

import java.util.Optional;

import org.drools.impact.analysis.graph.Graph;
import org.drools.impact.analysis.graph.Link;
import org.drools.impact.analysis.graph.Node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AbstractGraphTest {

    protected void assertNodeLink(Graph graph, String sourceFqdn, String targetFqdn, Link.Type type) {
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

        assertEquals(type, outgoingLink.getType());
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
}

package org.drools.compiler.xml.processes;

import org.drools.process.core.timer.Timer;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.TimerNode;
import org.drools.xml.ExtensibleXmlParser;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class TimerNodeHandler extends AbstractNodeHandler {

    protected Node createNode() {
        return new TimerNode();
    }

    public void handleNode(final Node node, final Element element, final String uri,
            final String localName, final ExtensibleXmlParser parser)
            throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        TimerNode timerNode = (TimerNode) node;
        String delay = element.getAttribute("delay");
        String period = element.getAttribute("period");
        if ((delay != null && delay.length() > 0) || (period != null && period.length() > 0)) {
            Timer timer = timerNode.getTimer();
            if (timer == null) {
                timer = new Timer();
                timerNode.setTimer(timer);
            }
            if (delay != null && delay.length() != 0 ) {
                timer.setDelay(delay);
            }
            if (period != null && period.length() != 0 ) {
                timer.setPeriod(period);
            }
        }
    }

    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return TimerNode.class;
    }

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		TimerNode timerNode = (TimerNode) node;
		writeNode("timerNode", timerNode, xmlDump, includeMeta);
        Timer timer = timerNode.getTimer();
        if (timer != null) {
            xmlDump.append("delay=\"" + timer.getDelay() + "\" ");
            if (timer.getPeriod() != null) {
                xmlDump.append(" period=\"" + timer.getPeriod() + "\" ");
            }
        }
        endNode(xmlDump);
	}

}

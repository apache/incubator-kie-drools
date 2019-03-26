/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.compiler.xml.processes;

import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.TimerNode;
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
        if (includeMeta && containsMetaData(timerNode)) {
        	xmlDump.append(">" + EOL);
        	writeMetaData(timerNode, xmlDump);
        	endNode("timerNode", xmlDump);
        } else {
            endNode(xmlDump);
        }
	}

}

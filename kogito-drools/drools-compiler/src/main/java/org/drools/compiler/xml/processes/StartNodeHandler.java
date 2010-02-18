package org.drools.compiler.xml.processes;

import java.util.List;
import java.util.Map;

import org.drools.compiler.xml.XmlDumper;
import org.drools.process.core.event.EventFilter;
import org.drools.process.core.event.EventTypeFilter;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.ConstraintTrigger;
import org.drools.workflow.core.node.EventTrigger;
import org.drools.workflow.core.node.StartNode;
import org.drools.workflow.core.node.Trigger;

public class StartNodeHandler extends AbstractNodeHandler {
    
    protected Node createNode() {
        return new StartNode();
    }
    
    @SuppressWarnings("unchecked")
	public Class generateNodeFor() {
        return StartNode.class;
    }

	public void writeNode(Node node, StringBuilder xmlDump, boolean includeMeta) {
		StartNode startNode = (StartNode) node;
		writeNode("start", startNode, xmlDump, includeMeta);
		List<Trigger> triggers = startNode.getTriggers();
		if (triggers == null || triggers.isEmpty()) {
			endNode(xmlDump);
		} else {
			xmlDump.append(">" + EOL);
			xmlDump.append("        <triggers>" + EOL);
			for (Trigger trigger: triggers) {
				if (trigger instanceof ConstraintTrigger) {
					xmlDump.append("         <trigger type=\"constraint\" >" + EOL);
					xmlDump.append("           <constraint type=\"rule\" dialect=\"mvel\" >"
						+ ((ConstraintTrigger) trigger).getConstraint() + "</constraint>" + EOL);
					Map<String, String> inMappings = trigger.getInMappings();
			    	if (inMappings != null && !inMappings.isEmpty()) {
			    		for (Map.Entry<String, String> entry: inMappings.entrySet()) {
				    		xmlDump.append("          <mapping type=\"in\" from=\""
			    				+ XmlDumper.replaceIllegalChars(entry.getValue())
			    				+ "\" to=\"" + entry.getKey() + "\" />" + EOL);
				    	}
			    	}
					xmlDump.append("         </trigger>" + EOL);
				} else if (trigger instanceof EventTrigger) {
					xmlDump.append("         <trigger type=\"event\" >" + EOL);
			        xmlDump.append("           <eventFilters>" + EOL);
					for (EventFilter filter: ((EventTrigger) trigger).getEventFilters()) {
			        	if (filter instanceof EventTypeFilter) {
			        		xmlDump.append("             <eventFilter "
			                    + "type=\"eventType\" "
			                    + "eventType=\"" + ((EventTypeFilter) filter).getType() + "\" />" + EOL);
			        	} else {
			        		throw new IllegalArgumentException(
			    				"Unknown filter type: " + filter);
			        	}
			        }
			        xmlDump.append("           </eventFilters>" + EOL);
					Map<String, String> inMappings = trigger.getInMappings();
			    	if (inMappings != null && !inMappings.isEmpty()) {
			    		for (Map.Entry<String, String> entry: inMappings.entrySet()) {
				    		xmlDump.append("          <mapping type=\"in\" from=\""
			    				+ XmlDumper.replaceIllegalChars(entry.getValue())
			    				+ "\" to=\"" + entry.getKey() + "\" />" + EOL);
				    	}
			    	}
					xmlDump.append("         </trigger>" + EOL);
				} else {
					throw new IllegalArgumentException(
						"Unknown trigger type " + trigger);
				}
			}
			xmlDump.append("        </triggers>" + EOL);
			endNode("start", xmlDump);
		}
	}

}

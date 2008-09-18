package org.drools.process.builder;

import java.util.Map;

import org.drools.lang.descr.ProcessDescr;
import org.drools.process.core.Process;
import org.drools.process.core.timer.Timer;
import org.drools.rule.builder.ProcessBuildContext;
import org.drools.workflow.core.DroolsAction;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.EventBasedNode;

public class EventBasedNodeBuilder extends ExtendedNodeBuilder {

    public void build(Process process,
                      ProcessDescr processDescr,
                      ProcessBuildContext context,
                      Node node) {
        super.build(process, processDescr, context, node);
        Map<Timer, DroolsAction> timers = ((EventBasedNode) node).getTimers();
        if (timers != null) {
	        for (DroolsAction action: timers.values()) {
	        	buildAction(action, context);
	        }
        }
    }
    
}

package org.drools.process.builder;

import java.util.Map;

import org.drools.definition.process.Node;
import org.drools.definition.process.Process;
import org.drools.lang.descr.ProcessDescr;
import org.drools.process.core.timer.Timer;
import org.drools.rule.builder.ProcessBuildContext;
import org.drools.workflow.core.DroolsAction;
import org.drools.workflow.core.impl.NodeImpl;
import org.drools.workflow.core.node.StateBasedNode;

public class EventBasedNodeBuilder extends ExtendedNodeBuilder {

    public void build(Process process,
                      ProcessDescr processDescr,
                      ProcessBuildContext context,
                      Node node) {
        super.build(process, processDescr, context, node);
        Map<Timer, DroolsAction> timers = ((StateBasedNode) node).getTimers();
        if (timers != null) {
	        for (DroolsAction action: timers.values()) {
	        	buildAction(action, context, (NodeImpl) node );
	        }
        }
    }
    
}

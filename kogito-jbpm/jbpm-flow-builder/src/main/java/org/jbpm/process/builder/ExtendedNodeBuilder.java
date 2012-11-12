package org.jbpm.process.builder;

import java.util.List;

import org.drools.lang.descr.ActionDescr;
import org.drools.lang.descr.ProcessDescr;
import org.jbpm.process.builder.dialect.ProcessDialect;
import org.jbpm.process.builder.dialect.ProcessDialectRegistry;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.impl.ExtendedNodeImpl;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.kie.definition.process.Node;
import org.kie.definition.process.Process;

public class ExtendedNodeBuilder
    implements
    ProcessNodeBuilder {

    public void build(Process process,
                      ProcessDescr processDescr,
                      ProcessBuildContext context,
                      Node node) {
        ExtendedNodeImpl extendedNode = ( ExtendedNodeImpl ) node;
        for (String type: extendedNode.getActionTypes()) {
        	List<DroolsAction> actions = extendedNode.getActions(type);
        	if (actions != null) {
	        	for (DroolsAction droolsAction: actions) {
	                buildAction(droolsAction, context, (NodeImpl) node);
	        	}
        	}
        }
    }
    
    protected void buildAction(DroolsAction droolsAction, ProcessBuildContext context, NodeImpl node) {
    	DroolsConsequenceAction action = (DroolsConsequenceAction) droolsAction;
        ActionDescr actionDescr = new ActionDescr();
        actionDescr.setText( action.getConsequence() );   
        ProcessDialect dialect = ProcessDialectRegistry.getDialect( action.getDialect() );            
    	dialect.getActionBuilder().build( context, action, actionDescr, node);
    }

}

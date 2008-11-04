package org.drools.process.builder;

import java.util.List;

import org.drools.compiler.Dialect;
import org.drools.definition.process.Node;
import org.drools.definition.process.Process;
import org.drools.lang.descr.ActionDescr;
import org.drools.lang.descr.ProcessDescr;
import org.drools.rule.builder.ProcessBuildContext;
import org.drools.workflow.core.DroolsAction;
import org.drools.workflow.core.impl.DroolsConsequenceAction;
import org.drools.workflow.core.impl.ExtendedNodeImpl;

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
	                buildAction(droolsAction, context);
	        	}
        	}
        }
    }
    
    protected void buildAction(DroolsAction droolsAction, ProcessBuildContext context) {
    	DroolsConsequenceAction action = (DroolsConsequenceAction) droolsAction;
        ActionDescr actionDescr = new ActionDescr();
        actionDescr.setText( action.getConsequence() );   
        Dialect dialect = context.getDialectRegistry().getDialect( action.getDialect() );            
        dialect.getActionBuilder().build( context, action, actionDescr );
    }

}

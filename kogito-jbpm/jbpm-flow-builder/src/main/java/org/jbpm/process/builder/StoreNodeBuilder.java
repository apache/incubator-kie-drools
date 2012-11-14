package org.jbpm.process.builder;

import org.kie.definition.process.Node;
import org.kie.definition.process.Process;
import org.drools.lang.descr.ActionDescr;
import org.drools.lang.descr.ProcessDescr;
import org.jbpm.process.builder.dialect.ProcessDialect;
import org.jbpm.process.builder.dialect.ProcessDialectRegistry;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.ActionNode;

public class StoreNodeBuilder
    implements
    ProcessNodeBuilder {

    public void build(Process process,
                      ProcessDescr processDescr,
                      ProcessBuildContext context,
                      Node node) {
        ActionNode actionNode = ( ActionNode ) node;
        DroolsConsequenceAction action = (DroolsConsequenceAction) actionNode.getAction();
        ActionDescr actionDescr = new ActionDescr();
        actionDescr.setText( action.getConsequence() );   
        
        ProcessDialect dialect = ProcessDialectRegistry.getDialect( action.getDialect() );            
        dialect.getActionBuilder().build( context, action, actionDescr, (NodeImpl) node );
    }

}

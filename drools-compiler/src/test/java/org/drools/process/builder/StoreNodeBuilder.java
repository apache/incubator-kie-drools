package org.drools.process.builder;

import org.drools.compiler.Dialect;
import org.drools.knowledge.definitions.process.Node;
import org.drools.knowledge.definitions.process.Process;
import org.drools.lang.descr.ActionDescr;
import org.drools.lang.descr.ProcessDescr;
import org.drools.rule.builder.ProcessBuildContext;
import org.drools.workflow.core.impl.DroolsConsequenceAction;
import org.drools.workflow.core.node.ActionNode;

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
        
        Dialect dialect = context.getDialectRegistry().getDialect( action.getDialect() );            
        
        dialect.getActionBuilder().build( context, action, actionDescr );
    }

}

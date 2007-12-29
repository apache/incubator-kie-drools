package org.drools.process.builder;

import org.drools.compiler.Dialect;
import org.drools.lang.descr.ActionDescr;
import org.drools.lang.descr.ProcessDescr;
import org.drools.rule.builder.ProcessBuildContext;
import org.drools.ruleflow.common.core.Process;
import org.drools.ruleflow.core.Node;
import org.drools.ruleflow.core.impl.ActionNodeImpl;
import org.drools.ruleflow.core.impl.DroolsConsequenceAction;

public class ActionNodeBuilder
    implements
    ProcessNodeBuilder {

    public void build(Process process,
                           ProcessDescr processDescr,
                           ProcessBuildContext context,
                           Node node) {
        ActionNodeImpl actionNode = ( ActionNodeImpl ) node;
        DroolsConsequenceAction action = (DroolsConsequenceAction) actionNode.getAction();
        ActionDescr actionDescr = new ActionDescr();
        actionDescr.setText( action.getConsequence() );   
        
        Dialect dialect = context.getDialectRegistry().getDialect( action.getDialect() );            
        
        dialect.getActionBuilder().build( context, actionNode, actionDescr );
    }

}

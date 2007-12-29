package org.drools.process.builder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.compiler.Dialect;
import org.drools.compiler.ReturnValueDescr;
import org.drools.lang.descr.ProcessDescr;
import org.drools.rule.builder.ProcessBuildContext;
import org.drools.ruleflow.common.core.Process;
import org.drools.ruleflow.core.Connection;
import org.drools.ruleflow.core.Node;
import org.drools.ruleflow.core.Split;
import org.drools.ruleflow.core.impl.ConstraintImpl;
import org.drools.ruleflow.core.impl.ReturnValueConstraintEvaluator;
import org.drools.ruleflow.core.impl.RuleFlowConstraintEvaluator;
import org.drools.ruleflow.core.impl.SplitImpl;

public class SplitNodeBuilder
    implements
    ProcessNodeBuilder {

    public void build(Process process,
                           ProcessDescr processDescr,
                           ProcessBuildContext context,
                           Node node) {
        SplitImpl splitNode = ( SplitImpl ) node;
        
        if ( splitNode.getType() == Split.TYPE_AND ) {
            // we only process or/xor
            return;
        }
        // we need to clone the map, so we can update the original while iterating.
        Map map = new HashMap( splitNode.getConstraints() );
        for ( Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
            Entry entry = (Entry) it.next();
            Connection connection = (Connection) entry.getKey();
            ConstraintImpl constraint = (ConstraintImpl) entry.getValue();
            
            if ( "rule".equals( constraint.getType() )) {
                RuleFlowConstraintEvaluator ruleConstraint = new RuleFlowConstraintEvaluator();
                ruleConstraint.setDialect( constraint.getDialect() );
                ruleConstraint.setName( constraint.getName() );
                ruleConstraint.setPriority( constraint.getPriority() );
                ruleConstraint.setPriority( constraint.getPriority() );
                splitNode.setConstraint( connection, ruleConstraint );
            } else if ( "code".equals( constraint.getType() ) ) {
                ReturnValueConstraintEvaluator returnValueConstraint = new ReturnValueConstraintEvaluator();
                returnValueConstraint.setDialect( constraint.getDialect() );
                returnValueConstraint.setName( constraint.getName() );
                returnValueConstraint.setPriority( constraint.getPriority() );
                returnValueConstraint.setPriority( constraint.getPriority() );
                splitNode.setConstraint( connection, returnValueConstraint );            
                
                ReturnValueDescr returnValueDescr = new ReturnValueDescr();
                returnValueDescr.setText( constraint.getConstraint() );
                
                Dialect dialect = context.getDialectRegistry().getDialect( constraint.getDialect() );                               
                dialect.getReturnValueEvaluatorBuilder().build( context, returnValueConstraint, returnValueDescr );
            }
        }
    }

}

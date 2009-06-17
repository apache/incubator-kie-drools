package org.drools.process.builder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.drools.compiler.Dialect;
import org.drools.compiler.ReturnValueDescr;
import org.drools.definition.process.Connection;
import org.drools.definition.process.Node;
import org.drools.definition.process.Process;
import org.drools.lang.descr.ProcessDescr;
import org.drools.rule.builder.ProcessBuildContext;
import org.drools.workflow.core.Constraint;
import org.drools.workflow.core.impl.ConnectionRef;
import org.drools.workflow.core.impl.ConstraintImpl;
import org.drools.workflow.core.impl.NodeImpl;
import org.drools.workflow.core.node.Split;
import org.drools.workflow.instance.impl.ReturnValueConstraintEvaluator;
import org.drools.workflow.instance.impl.RuleConstraintEvaluator;

public class SplitNodeBuilder implements ProcessNodeBuilder {

    public void build(Process process,
                      ProcessDescr processDescr,
                      ProcessBuildContext context,
                      Node node) {
        Split splitNode = ( Split ) node;
        
        if ( splitNode.getType() == Split.TYPE_AND ) {
            // we only process or/xor
            return;
        }
        // we need to clone the map, so we can update the original while iterating.
        Map<ConnectionRef, Constraint> map = new HashMap<ConnectionRef, Constraint>( splitNode.getConstraints() );
        for ( Iterator<Map.Entry<ConnectionRef, Constraint>> it = map.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<ConnectionRef, Constraint> entry = it.next();
            ConnectionRef connection = entry.getKey();
            ConstraintImpl constraint = (ConstraintImpl) entry.getValue();
            Connection outgoingConnection = null; 
            for (Connection out: splitNode.getDefaultOutgoingConnections()) {
                if (out.getToType().equals(connection.getToType())
                    && out.getTo().getId() == connection.getNodeId()) {
                    outgoingConnection = out;
                }
            }
            if (outgoingConnection == null) {
                throw new IllegalArgumentException("Could not find outgoing connection");
            }
            if ( "rule".equals( constraint.getType() )) {
                RuleConstraintEvaluator ruleConstraint = new RuleConstraintEvaluator();
                ruleConstraint.setDialect( constraint.getDialect() );
                ruleConstraint.setName( constraint.getName() );
                ruleConstraint.setPriority( constraint.getPriority() );
                splitNode.setConstraint( outgoingConnection, ruleConstraint );
            } else if ( "code".equals( constraint.getType() ) ) {
                ReturnValueConstraintEvaluator returnValueConstraint = new ReturnValueConstraintEvaluator();
                returnValueConstraint.setDialect( constraint.getDialect() );
                returnValueConstraint.setName( constraint.getName() );
                returnValueConstraint.setPriority( constraint.getPriority() );
                splitNode.setConstraint( outgoingConnection, returnValueConstraint );            
                
                ReturnValueDescr returnValueDescr = new ReturnValueDescr();
                returnValueDescr.setText( constraint.getConstraint() );
                
                Dialect dialect = context.getDialectRegistry().getDialect( constraint.getDialect() );                               
                dialect.getReturnValueEvaluatorBuilder().build( context, returnValueConstraint, returnValueDescr, (NodeImpl) node );
            }
        }
    }

}

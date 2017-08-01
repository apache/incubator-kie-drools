/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.builder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.drools.compiler.compiler.ReturnValueDescr;
import org.kie.api.definition.process.Connection;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.Process;
import org.drools.compiler.lang.descr.ProcessDescr;
import org.jbpm.process.builder.dialect.ProcessDialect;
import org.jbpm.process.builder.dialect.ProcessDialectRegistry;
import org.jbpm.process.instance.impl.ReturnValueConstraintEvaluator;
import org.jbpm.process.instance.impl.RuleConstraintEvaluator;
import org.jbpm.workflow.core.Constraint;
import org.jbpm.workflow.core.impl.ConnectionRef;
import org.jbpm.workflow.core.impl.ConstraintImpl;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.Split;

public class MultiConditionalSequenceFlowNodeBuilder implements ProcessNodeBuilder {

	public void build(Process process, ProcessDescr processDescr,
			ProcessBuildContext context, Node node) {

		Map<ConnectionRef, Constraint> constraints = ((NodeImpl) node).getConstraints();

		// exclude split as it is handled with separate builder and nodes with non conditional sequence flows
		if (node instanceof Split || constraints.size() == 0) {
			return;
		}

		// we need to clone the map, so we can update the original while iterating.
        Map<ConnectionRef, Constraint> map = new HashMap<ConnectionRef, Constraint>( constraints );
        for ( Iterator<Map.Entry<ConnectionRef, Constraint>> it = map.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<ConnectionRef, Constraint> entry = it.next();
            ConnectionRef connection = entry.getKey();
            ConstraintImpl constraint = (ConstraintImpl) entry.getValue();
            Connection outgoingConnection = null;
            for (Connection out: ((NodeImpl) node).getDefaultOutgoingConnections()) {
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
                ruleConstraint.setDefault( constraint.isDefault() );
                ((NodeImpl) node).setConstraint( outgoingConnection, ruleConstraint );
            } else if ( "code".equals( constraint.getType() ) ) {
                ReturnValueConstraintEvaluator returnValueConstraint = new ReturnValueConstraintEvaluator();
                returnValueConstraint.setDialect( constraint.getDialect() );
                returnValueConstraint.setName( constraint.getName() );
                returnValueConstraint.setPriority( constraint.getPriority() );
                returnValueConstraint.setDefault( constraint.isDefault() );
                ((NodeImpl) node).setConstraint( outgoingConnection, returnValueConstraint );

                ReturnValueDescr returnValueDescr = new ReturnValueDescr();
                returnValueDescr.setText( constraint.getConstraint() );
                returnValueDescr.setResource(processDescr.getResource());

                ProcessDialect dialect = ProcessDialectRegistry.getDialect( constraint.getDialect() );
            	dialect.getReturnValueEvaluatorBuilder().build( context, returnValueConstraint, returnValueDescr, (NodeImpl) node );
            }
        }

	}

}

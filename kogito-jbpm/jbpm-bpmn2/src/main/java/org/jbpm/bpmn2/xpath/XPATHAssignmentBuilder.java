package org.jbpm.bpmn2.xpath;

import org.drools.compiler.rule.builder.PackageBuildContext;
import org.jbpm.process.builder.AssignmentBuilder;
import org.jbpm.process.core.ContextResolver;
import org.jbpm.workflow.core.node.Assignment;

public class XPATHAssignmentBuilder implements AssignmentBuilder {

	public void build(PackageBuildContext context, Assignment assignment, String sourceExpr, String targetExpr,
					  ContextResolver contextResolver, boolean isInput) {
		assignment.setMetaData("Action", new XPATHAssignmentAction(assignment, sourceExpr, targetExpr, isInput));
	}

}

package org.jbpm.process.builder;

import org.drools.rule.builder.PackageBuildContext;
import org.jbpm.process.core.ContextResolver;
import org.jbpm.workflow.core.node.Assignment;

public interface AssignmentBuilder {

    public void build(final PackageBuildContext context,
                      final Assignment assignment,
                      final String sourceExpr,
                      final String targetExpr,
                      final ContextResolver contextResolver,
                      boolean isInput);

}
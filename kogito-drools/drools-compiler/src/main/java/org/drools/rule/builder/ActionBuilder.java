package org.drools.rule.builder;

import org.drools.lang.descr.ActionDescr;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.workflow.core.node.ActionNode;

public interface ActionBuilder {

    public void build(final PackageBuildContext context,
                      final ActionNode actionNode,
                      final ActionDescr actionDescr);

}
package org.jbpm.process.builder;

import org.drools.compiler.lang.descr.ActionDescr;
import org.drools.rule.builder.PackageBuildContext;
import org.jbpm.process.core.ContextResolver;
import org.jbpm.workflow.core.DroolsAction;

public interface ActionBuilder {

    public void build(final PackageBuildContext context,
                      final DroolsAction action,
                      final ActionDescr actionDescr,
                      final ContextResolver contextResolver);

}

package org.drools.rule.builder;

import org.drools.lang.descr.ActionDescr;
import org.drools.process.core.ContextResolver;
import org.drools.workflow.core.DroolsAction;

public interface ActionBuilder {

    public void build(final PackageBuildContext context,
                      final DroolsAction action,
                      final ActionDescr actionDescr,
                      final ContextResolver contextResolver);

}
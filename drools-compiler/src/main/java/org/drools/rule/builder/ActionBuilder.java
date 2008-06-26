package org.drools.rule.builder;

import org.drools.lang.descr.ActionDescr;
import org.drools.workflow.core.DroolsAction;

public interface ActionBuilder {

    public void build(final PackageBuildContext context,
                      final DroolsAction action,
                      final ActionDescr actionDescr);

}